<#
.SYNOPSIS
    Assistente interativo para configurar o Git com autenticacao via SSH no GitHub.

.DESCRIPTION
    Guia o usuario, passo a passo, por toda a configuracao necessaria:
      1. Verifica se git e ssh estao instalados
      2. Configura nome e e-mail globais do Git
      3. Gera (ou reaproveita) um par de chaves SSH
      4. Habilita e inicia o servico ssh-agent, registrando a chave
      5. Detecta se a porta 22 esta bloqueada e escolhe a rota SSH adequada
      6. Cria/atualiza o arquivo ~/.ssh/config
      7. Copia a chave publica e orienta o cadastro no GitHub
      8. Testa a conexao SSH e converte o remote do repositorio para SSH

    O script e idempotente: pode ser executado varias vezes sem quebrar nada.

.PARAMETER NaoInterativo
    Executa sem perguntas, usando os valores ja existentes na maquina.
    Util para conferir se o ambiente ja esta configurado.

.PARAMETER Forcar443
    Pula a deteccao automatica e ja configura a rota alternativa
    ssh.github.com na porta 443 (util em redes corporativas conhecidas).

.EXAMPLE
    .\Configurar-GitSSH.ps1

.EXAMPLE
    .\Configurar-GitSSH.ps1 -Forcar443

.NOTES
    Requer PowerShell 5.1 ou superior e Git for Windows instalado.
    Nao requer privilegios de administrador (o passo do ssh-agent e degradado
    graciosamente caso o servico nao possa ser habilitado).
#>

[CmdletBinding()]
param(
    [switch]$NaoInterativo,
    [switch]$Forcar443
)

$ErrorActionPreference = 'Stop'

# ------------------------------------------------------------------------------
# Constantes do GitHub
# ------------------------------------------------------------------------------

$GitHubHost         = 'github.com'
$GitHubHostAlt      = 'ssh.github.com'   # mesma chave de host, porta 443
$GitHubUsuario      = 'git'
$GitHubUrlChaves    = 'https://github.com/settings/keys'
$GitHubTextoSucesso = 'successfully authenticated'

# ------------------------------------------------------------------------------
# Funcoes auxiliares de apresentacao
# ------------------------------------------------------------------------------

$script:PassoAtual  = 0
$script:TotalPassos = 8

function Write-Titulo {
    param([string]$Texto)
    $linha = '=' * 74
    Write-Host ''
    Write-Host $linha -ForegroundColor Cyan
    Write-Host "  $Texto" -ForegroundColor Cyan
    Write-Host $linha -ForegroundColor Cyan
}

function Write-Passo {
    param([string]$Texto)
    $script:PassoAtual++
    Write-Host ''
    Write-Host "[$script:PassoAtual/$script:TotalPassos] $Texto" -ForegroundColor White -BackgroundColor DarkBlue
}

function Write-Ok    { param([string]$T) Write-Host "  [OK]    $T" -ForegroundColor Green }
function Write-Info  { param([string]$T) Write-Host "  [INFO]  $T" -ForegroundColor Gray }
function Write-Aviso { param([string]$T) Write-Host "  [AVISO] $T" -ForegroundColor Yellow }
function Write-Erro  { param([string]$T) Write-Host "  [ERRO]  $T" -ForegroundColor Red }
function Write-Acao  { param([string]$T) Write-Host "  ->      $T" -ForegroundColor Magenta }

function Read-Texto {
    <#  Le um texto do usuario, oferecendo um valor padrao entre colchetes.  #>
    param(
        [string]$Pergunta,
        [string]$Padrao = ''
    )
    if ($NaoInterativo) { return $Padrao }

    $sufixo = if ($Padrao) { " [$Padrao]" } else { '' }
    while ($true) {
        $resposta = Read-Host "  $Pergunta$sufixo"
        if ([string]::IsNullOrWhiteSpace($resposta)) {
            if ($Padrao) { return $Padrao }
            Write-Aviso 'Valor obrigatorio. Digite algo.'
            continue
        }
        return $resposta.Trim()
    }
}

function Read-Confirmacao {
    <#  Pergunta S/N e devolve booleano.  #>
    param(
        [string]$Pergunta,
        [bool]$Padrao = $true
    )
    if ($NaoInterativo) { return $Padrao }

    $sufixo = if ($Padrao) { '[S/n]' } else { '[s/N]' }
    while ($true) {
        $resposta = (Read-Host "  $Pergunta $sufixo").Trim().ToLower()
        if ([string]::IsNullOrWhiteSpace($resposta)) { return $Padrao }
        if ($resposta -in @('s', 'sim', 'y', 'yes')) { return $true }
        if ($resposta -in @('n', 'nao', 'não', 'no')) { return $false }
        Write-Aviso 'Responda com S (sim) ou N (nao).'
    }
}

function Test-Comando {
    <#  Verifica se um executavel existe no PATH.  #>
    param([string]$Nome)
    $null -ne (Get-Command $Nome -ErrorAction SilentlyContinue)
}

function Invoke-Externo {
    <#
        Executa um programa externo capturando stdout+stderr sem que o
        $ErrorActionPreference='Stop' aborte o script por causa do stderr.
        Devolve um objeto com Saida e CodigoSaida.
    #>
    param(
        [string]$Arquivo,
        [string[]]$Argumentos = @()
    )
    $anterior = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $saida = & $Arquivo @Argumentos 2>&1 | Out-String
        [pscustomobject]@{
            Saida       = $saida.Trim()
            CodigoSaida = $LASTEXITCODE
        }
    }
    finally {
        $ErrorActionPreference = $anterior
    }
}

# ------------------------------------------------------------------------------
# Funcoes de rede e de manipulacao do ~/.ssh/config
# ------------------------------------------------------------------------------

function Test-PortaTcp {
    <#
        Tenta abrir uma conexao TCP com timeout proprio.
        O Test-NetConnection padrao demora demais quando a porta esta filtrada,
        entao usamos TcpClient assincrono para controlar o tempo de espera.
    #>
    param(
        [string]$Alvo,
        [int]$Porta,
        [int]$TimeoutMs = 6000
    )

    $cliente = New-Object System.Net.Sockets.TcpClient
    try {
        $async = $cliente.BeginConnect($Alvo, $Porta, $null, $null)
        if (-not $async.AsyncWaitHandle.WaitOne($TimeoutMs, $false)) {
            return $false   # timeout: porta filtrada por firewall/proxy
        }
        $cliente.EndConnect($async)
        return $cliente.Connected
    }
    catch {
        return $false       # conexao recusada ou DNS falhou
    }
    finally {
        $cliente.Close()
    }
}

function New-BlocoConfigSsh {
    <#  Monta o bloco Host do ~/.ssh/config, na porta 22 ou 443.  #>
    param(
        [string]$CaminhoChaveUnix,
        [bool]$Via443
    )

    $nomeHost = if ($Via443) { $GitHubHostAlt } else { $GitHubHost }
    $porta    = if ($Via443) { 443 } else { 22 }

    return @"
Host $GitHubHost
    HostName $nomeHost
    Port $porta
    User $GitHubUsuario
    IdentityFile $CaminhoChaveUnix
    IdentitiesOnly yes
    AddKeysToAgent yes
"@
}

function Test-BlocoHostExiste {
    <#  Verifica se o config ja declara um bloco para o host informado.  #>
    param(
        [string]$Conteudo,
        [string]$NomeHost
    )
    # \s+ apos "Host" impede casar com a diretiva "HostName".
    return $Conteudo -match "(?m)^\s*Host\s+$([regex]::Escape($NomeHost))\s*$"
}

function Remove-BlocoHost {
    <#
        Remove do conteudo o bloco "Host <nome>" inteiro, ou seja, da linha
        Host ate a proxima diretiva Host (ou o fim do arquivo).
    #>
    param(
        [string]$Conteudo,
        [string]$NomeHost
    )

    $linhas = $Conteudo -split "`r?`n"
    $mantidas = New-Object System.Collections.Generic.List[string]
    $dentroDoBloco = $false

    foreach ($linha in $linhas) {
        if ($linha -match '^\s*Host\s+(.+?)\s*$') {
            $dentroDoBloco = ($Matches[1] -eq $NomeHost)
        }
        if (-not $dentroDoBloco) { $mantidas.Add($linha) }
    }

    return (($mantidas -join "`r`n").Trim())
}

function Set-BlocoConfigSsh {
    <#  Grava o bloco no ~/.ssh/config, substituindo o anterior se existir.  #>
    param(
        [string]$Caminho,
        [string]$Bloco
    )

    $conteudo = if (Test-Path $Caminho) { (Get-Content $Caminho -Raw) } else { '' }
    $conteudo = Remove-BlocoHost -Conteudo $conteudo -NomeHost $GitHubHost

    $novo = if ([string]::IsNullOrWhiteSpace($conteudo)) { $Bloco } else { "$conteudo`r`n`r`n$Bloco" }
    Set-Content -Path $Caminho -Value $novo -Encoding utf8

    $Bloco -split "`r?`n" | ForEach-Object { Write-Host "         $_" -ForegroundColor DarkGray }
}

# ==============================================================================
# INICIO
# ==============================================================================

Write-Titulo 'ASSISTENTE DE CONFIGURACAO DO GIT VIA SSH (GITHUB)'
Write-Host @'
  Este assistente vai te guiar na configuracao do Git com chave SSH no GitHub.
  Nada e feito sem a sua confirmacao e nenhuma chave existente e sobrescrita
  sem aviso. Pressione Ctrl+C a qualquer momento para cancelar.
'@ -ForegroundColor Gray

# ------------------------------------------------------------------------------
# PASSO 1 - Pre-requisitos
# ------------------------------------------------------------------------------

Write-Passo 'Verificando pre-requisitos (git e ssh)'

$faltando = @()

if (Test-Comando 'git') {
    $versaoGit = (Invoke-Externo 'git' @('--version')).Saida
    Write-Ok "Git encontrado: $versaoGit"
}
else {
    Write-Erro 'Git nao encontrado no PATH.'
    $faltando += 'Git for Windows -> https://git-scm.com/download/win'
}

if (Test-Comando 'ssh') {
    $versaoSsh = (Invoke-Externo 'ssh' @('-V')).Saida
    Write-Ok "OpenSSH encontrado: $versaoSsh"
}
else {
    Write-Erro 'Cliente ssh nao encontrado no PATH.'
    $faltando += 'OpenSSH Client -> Configuracoes > Sistema > Recursos opcionais'
}

if (-not (Test-Comando 'ssh-keygen')) {
    Write-Erro 'ssh-keygen nao encontrado no PATH.'
    $faltando += 'ssh-keygen (vem junto com o OpenSSH Client ou o Git for Windows)'
}

if ($faltando.Count -gt 0) {
    Write-Host ''
    Write-Erro 'Instale os itens abaixo, reabra o PowerShell e rode o script de novo:'
    $faltando | ForEach-Object { Write-Acao $_ }
    exit 1
}

# ------------------------------------------------------------------------------
# PASSO 2 - Identidade do Git (user.name / user.email)
# ------------------------------------------------------------------------------

Write-Passo 'Configurando sua identidade no Git (nome e e-mail)'

$nomeAtual  = (Invoke-Externo 'git' @('config', '--global', 'user.name')).Saida
$emailAtual = (Invoke-Externo 'git' @('config', '--global', 'user.email')).Saida

if ($nomeAtual)  { Write-Info "user.name  atual: $nomeAtual" }  else { Write-Aviso 'user.name ainda nao configurado.' }
if ($emailAtual) { Write-Info "user.email atual: $emailAtual" } else { Write-Aviso 'user.email ainda nao configurado.' }

$precisaIdentidade = (-not $nomeAtual) -or (-not $emailAtual)
$alterarIdentidade = $precisaIdentidade

if (-not $precisaIdentidade) {
    $alterarIdentidade = Read-Confirmacao 'Deseja alterar esses dados?' $false
}

$email = $emailAtual

if ($alterarIdentidade -and (-not $NaoInterativo)) {
    $nome  = Read-Texto 'Seu nome completo (aparece nos commits)' $nomeAtual
    $email = Read-Texto 'Seu e-mail (o mesmo cadastrado na sua conta do GitHub)' $emailAtual

    Invoke-Externo 'git' @('config', '--global', 'user.name', $nome)   | Out-Null
    Invoke-Externo 'git' @('config', '--global', 'user.email', $email) | Out-Null
    Write-Ok "Identidade gravada: $nome <$email>"
}
else {
    Write-Ok 'Identidade mantida como esta.'
}

# Ajuste recomendado no Windows: evita bagunca de fim de linha nos arquivos.
$autocrlf = (Invoke-Externo 'git' @('config', '--global', 'core.autocrlf')).Saida
if (-not $autocrlf) {
    if (Read-Confirmacao 'Configurar core.autocrlf=true (recomendado no Windows)?' $true) {
        Invoke-Externo 'git' @('config', '--global', 'core.autocrlf', 'true') | Out-Null
        Write-Ok 'core.autocrlf=true configurado.'
    }
}

# ------------------------------------------------------------------------------
# PASSO 3 - Par de chaves SSH
# ------------------------------------------------------------------------------

Write-Passo 'Gerando o par de chaves SSH'

$pastaSsh = Join-Path $HOME '.ssh'
if (-not (Test-Path $pastaSsh)) {
    New-Item -ItemType Directory -Path $pastaSsh -Force | Out-Null
    Write-Ok "Pasta criada: $pastaSsh"
}
else {
    Write-Info "Pasta ja existe: $pastaSsh"
}

$caminhoChave = Join-Path $pastaSsh 'id_ed25519_github'
$caminhoPub   = "$caminhoChave.pub"

# Reaproveita uma chave padrao ja existente, se o usuario preferir.
$chavePadrao = Join-Path $pastaSsh 'id_ed25519'
if ((Test-Path $chavePadrao) -and (-not (Test-Path $caminhoChave))) {
    Write-Info "Encontrada uma chave padrao existente: $chavePadrao"
    if (Read-Confirmacao 'Deseja reutilizar essa chave em vez de criar uma nova?' $true) {
        $caminhoChave = $chavePadrao
        $caminhoPub   = "$chavePadrao.pub"
    }
}

if (Test-Path $caminhoChave) {
    Write-Ok "Chave ja existe: $caminhoChave"
    if (Read-Confirmacao 'Gerar uma NOVA chave por cima (a antiga sera perdida)?' $false) {
        $backup = "$caminhoChave.bak-$(Get-Date -Format 'yyyyMMddHHmmss')"
        Move-Item $caminhoChave $backup -Force
        if (Test-Path $caminhoPub) { Move-Item $caminhoPub "$backup.pub" -Force }
        Write-Aviso "Chave antiga movida para: $backup"
    }
}

if (-not (Test-Path $caminhoChave)) {
    $comentario = if ($email) { $email } else { "$env:USERNAME@$env:COMPUTERNAME" }

    Write-Info 'Voce pode proteger a chave com uma senha (passphrase).'
    Write-Info 'Se deixar em branco, o Git nao pedira senha nenhuma ao usar a chave.'

    # Em modo interativo o proprio ssh-keygen pergunta a passphrase.
    $argsKeygen = @('-t', 'ed25519', '-C', $comentario, '-f', $caminhoChave)
    if ($NaoInterativo) { $argsKeygen += @('-N', '') }

    $resultado = Invoke-Externo 'ssh-keygen' $argsKeygen
    if (-not (Test-Path $caminhoChave)) {
        Write-Erro 'Falha ao gerar a chave.'
        Write-Host $resultado.Saida -ForegroundColor DarkGray
        exit 1
    }
    Write-Ok "Chave gerada: $caminhoChave"
}

$chavePublica = (Get-Content $caminhoPub -Raw).Trim()
$fingerprint  = (Invoke-Externo 'ssh-keygen' @('-lf', $caminhoPub)).Saida
Write-Info "Fingerprint: $fingerprint"

# ------------------------------------------------------------------------------
# PASSO 4 - ssh-agent
# ------------------------------------------------------------------------------

Write-Passo 'Habilitando o ssh-agent e registrando a chave'

$servicoAgent = Get-Service ssh-agent -ErrorAction SilentlyContinue

if ($null -eq $servicoAgent) {
    Write-Aviso 'Servico ssh-agent do Windows nao encontrado (OpenSSH pode nao estar instalado como recurso).'
    Write-Info  'Sem o agente, a passphrase sera pedida a cada operacao. Nao impede o funcionamento.'
}
else {
    if ($servicoAgent.StartType -eq 'Disabled') {
        Write-Aviso 'O servico ssh-agent esta desabilitado. Habilitar requer privilegio de administrador.'
        try {
            Set-Service ssh-agent -StartupType Automatic -ErrorAction Stop
            Write-Ok 'Servico habilitado para iniciar automaticamente.'
        }
        catch {
            Write-Aviso 'Nao foi possivel habilitar automaticamente.'
            Write-Acao 'Abra o PowerShell como Administrador e rode:'
            Write-Host '          Set-Service ssh-agent -StartupType Automatic' -ForegroundColor DarkGray
            Write-Host '          Start-Service ssh-agent' -ForegroundColor DarkGray
        }
    }

    $servicoAgent.Refresh()
    if ($servicoAgent.Status -ne 'Running' -and $servicoAgent.StartType -ne 'Disabled') {
        try {
            Start-Service ssh-agent -ErrorAction Stop
            Write-Ok 'Servico ssh-agent iniciado.'
        }
        catch {
            Write-Aviso "Nao foi possivel iniciar o ssh-agent: $($_.Exception.Message)"
        }
    }
    elseif ($servicoAgent.Status -eq 'Running') {
        Write-Ok 'Servico ssh-agent ja esta em execucao.'
    }
}

# Adiciona a chave ao agente somente se ainda nao estiver la.
if (Test-Comando 'ssh-add') {
    $listadas = (Invoke-Externo 'ssh-add' @('-l')).Saida
    $fpCurta  = ($fingerprint -split '\s+')[1]

    if ($listadas -like "*$fpCurta*") {
        Write-Ok 'A chave ja esta carregada no ssh-agent.'
    }
    else {
        $add = Invoke-Externo 'ssh-add' @($caminhoChave)
        if ($add.CodigoSaida -eq 0) {
            Write-Ok 'Chave adicionada ao ssh-agent.'
        }
        else {
            Write-Aviso 'Nao foi possivel adicionar a chave ao agente (nao e um problema critico).'
            Write-Host "         $($add.Saida)" -ForegroundColor DarkGray
        }
    }
}

# ------------------------------------------------------------------------------
# PASSO 5 - Diagnostico de rede: a porta 22 esta liberada?
# ------------------------------------------------------------------------------

Write-Passo 'Verificando se a porta 22 esta liberada na sua rede'

$usar443 = $false

if ($Forcar443) {
    Write-Info 'Parametro -Forcar443 informado: pulando a deteccao automatica.'
    $usar443 = $true
}
else {
    Write-Info "Testando conexao TCP em ${GitHubHost}:22 (ate 6s)..."

    if (Test-PortaTcp -Alvo $GitHubHost -Porta 22) {
        Write-Ok 'Porta 22 acessivel. Sera usada a rota padrao do SSH.'
    }
    else {
        Write-Aviso 'Porta 22 bloqueada ou filtrada (comum em redes corporativas e Wi-Fi publico).'
        Write-Info  "Testando a rota alternativa ${GitHubHostAlt}:443 (ate 6s)..."

        if (Test-PortaTcp -Alvo $GitHubHostAlt -Porta 443) {
            Write-Ok 'Rota alternativa na porta 443 funciona. Ela sera configurada automaticamente.'
            $usar443 = $true
        }
        else {
            Write-Erro 'Nem a porta 22 nem a 443 responderam.'
            Write-Info  'Isso costuma indicar falta de internet ou um proxy corporativo obrigatorio.'
            Write-Acao  'Verifique sua conexao ou fale com o suporte de TI da sua empresa.'
            Write-Info  'O script continua, mas o teste de autenticacao provavelmente vai falhar.'
        }
    }
}

# ------------------------------------------------------------------------------
# PASSO 6 - Arquivo ~/.ssh/config
# ------------------------------------------------------------------------------

Write-Passo 'Configurando o arquivo ~/.ssh/config'

$caminhoConfig = Join-Path $pastaSsh 'config'

# O OpenSSH exige caminho estilo Unix dentro do config.
$chaveParaConfig = $caminhoChave -replace '\\', '/'
$blocoConfig     = New-BlocoConfigSsh -CaminhoChaveUnix $chaveParaConfig -Via443 $usar443

$conteudoConfig = if (Test-Path $caminhoConfig) { Get-Content $caminhoConfig -Raw } else { '' }
$jaExiste       = Test-BlocoHostExiste -Conteudo $conteudoConfig -NomeHost $GitHubHost

if ($jaExiste) {
    Write-Info "Ja existe um bloco 'Host $GitHubHost' em $caminhoConfig"
    if (Read-Confirmacao 'Deseja substitui-lo pelo bloco gerado agora?' $false) {
        Set-BlocoConfigSsh -Caminho $caminhoConfig -Bloco $blocoConfig
        Write-Ok 'Bloco substituido.'
    }
    else {
        Write-Aviso 'Bloco existente mantido. Confira se o IdentityFile e a porta estao corretos.'
    }
}
else {
    Set-BlocoConfigSsh -Caminho $caminhoConfig -Bloco $blocoConfig
    Write-Ok "Bloco adicionado em $caminhoConfig"
}

# ------------------------------------------------------------------------------
# PASSO 7 - Cadastro da chave publica no GitHub
# ------------------------------------------------------------------------------

Write-Passo 'Cadastrando a chave publica no GitHub'

try {
    Set-Clipboard -Value $chavePublica
    Write-Ok 'Chave publica copiada para a area de transferencia (Ctrl+V para colar).'
}
catch {
    Write-Aviso 'Nao foi possivel copiar automaticamente. Copie o texto abaixo manualmente.'
}

Write-Host ''
Write-Host '  ----- INICIO DA CHAVE PUBLICA -----' -ForegroundColor DarkGray
Write-Host "  $chavePublica" -ForegroundColor Yellow
Write-Host '  ----- FIM DA CHAVE PUBLICA --------' -ForegroundColor DarkGray
Write-Host ''

Write-Acao "1. Abra: $GitHubUrlChaves"
Write-Acao '2. Clique em "New SSH key"'
Write-Acao '3. Title: algo que identifique esta maquina (ex.: Notebook Casa)'
Write-Acao '4. Key type: Authentication Key'
Write-Acao '5. Cole a chave no campo "Key" e clique em "Add SSH key"'
Write-Info  'Somente a chave PUBLICA (.pub) vai para o GitHub. A privada nunca sai da sua maquina.'

if (-not $NaoInterativo) {
    if (Read-Confirmacao 'Abrir a pagina de chaves do GitHub no navegador agora?' $true) {
        Start-Process $GitHubUrlChaves
    }
    Read-Host '  Pressione ENTER depois de cadastrar a chave no GitHub' | Out-Null
}

# ------------------------------------------------------------------------------
# PASSO 8 - Teste de conexao e ajuste do remote
# ------------------------------------------------------------------------------

Write-Passo 'Testando a conexao SSH'

function Invoke-TesteSsh {
    <#  Roda ssh -T contra o GitHub usando o ~/.ssh/config recem-configurado.  #>
    Write-Info "Executando: ssh -T $GitHubUsuario@$GitHubHost"
    return Invoke-Externo 'ssh' @(
        '-T',
        '-o', 'StrictHostKeyChecking=accept-new',
        '-o', 'ConnectTimeout=15',
        "$GitHubUsuario@$GitHubHost"
    )
}

$teste   = Invoke-TesteSsh
$sucesso = $teste.Saida -match [regex]::Escape($GitHubTextoSucesso)

# Se falhou por conectividade e ainda nao estamos na 443, tenta o fallback.
$falhaDeRede = $teste.Saida -match 'Connection timed out|Connection refused|Connection closed|Network is unreachable|Operation timed out|Could not resolve hostname'

if ((-not $sucesso) -and $falhaDeRede -and (-not $usar443)) {
    Write-Host ''
    Write-Aviso 'A conexao pela porta 22 falhou. Isso indica bloqueio de rede.'
    Write-Host "         $($teste.Saida)" -ForegroundColor DarkGray

    if (Read-Confirmacao "Reconfigurar para $GitHubHostAlt na porta 443 e tentar de novo?" $true) {
        $blocoConfig = New-BlocoConfigSsh -CaminhoChaveUnix $chaveParaConfig -Via443 $true
        Set-BlocoConfigSsh -Caminho $caminhoConfig -Bloco $blocoConfig
        Write-Ok 'Config atualizado para a porta 443.'

        $usar443 = $true
        $teste   = Invoke-TesteSsh
        $sucesso = $teste.Saida -match [regex]::Escape($GitHubTextoSucesso)
    }
}

if ($sucesso) {
    Write-Ok 'Autenticacao SSH funcionando!'
    Write-Host "         $($teste.Saida)" -ForegroundColor DarkGray
}
else {
    Write-Erro 'A autenticacao nao foi confirmada. Resposta do servidor:'
    Write-Host "         $($teste.Saida)" -ForegroundColor DarkGray
    Write-Host ''
    Write-Info 'Causas mais comuns:'
    Write-Acao 'A chave publica ainda nao foi salva no GitHub (ou foi colada incompleta)'
    Write-Acao 'O IdentityFile no ~/.ssh/config aponta para outra chave'
    Write-Acao 'Ha um proxy corporativo obrigatorio bloqueando SSH nas duas portas'
}

# Converte o remote do repositorio atual, se houver um e se estiver em HTTPS.
Write-Host ''
$dentroDeRepo = (Invoke-Externo 'git' @('rev-parse', '--is-inside-work-tree')).Saida -eq 'true'

if ($dentroDeRepo) {
    $remoteAtual = (Invoke-Externo 'git' @('remote', 'get-url', 'origin')).Saida

    if ($remoteAtual -match "^https://$([regex]::Escape($GitHubHost))/(.+?)(\.git)?/?$") {
        $repo = $Matches[1]
        $novoRemote = "${GitHubUsuario}@${GitHubHost}:$repo.git"

        Write-Info "Remote atual (HTTPS): $remoteAtual"
        Write-Info "Remote via SSH:       $novoRemote"

        if (Read-Confirmacao 'Trocar o remote origin deste repositorio para SSH?' $true) {
            Invoke-Externo 'git' @('remote', 'set-url', 'origin', $novoRemote) | Out-Null
            Write-Ok "Remote origin atualizado para: $novoRemote"
        }
    }
    elseif ($remoteAtual -like "*$GitHubHost*") {
        Write-Ok "O remote origin ja usa SSH: $remoteAtual"
    }
    else {
        Write-Info 'Nenhum remote HTTPS do GitHub foi encontrado no repositorio atual.'
    }
}
else {
    Write-Info 'Voce nao esta dentro de um repositorio Git - o ajuste de remote foi pulado.'
}

# ------------------------------------------------------------------------------
# RESUMO FINAL
# ------------------------------------------------------------------------------

Write-Titulo 'RESUMO DA CONFIGURACAO'

$rota = if ($usar443) { "$GitHubHostAlt : 443 (rota alternativa)" } else { "$GitHubHost : 22 (rota padrao)" }

Write-Host "  Servidor .......... GitHub ($GitHubHost)"          -ForegroundColor Gray
Write-Host "  Rota SSH .......... $rota"                         -ForegroundColor Gray
Write-Host "  Nome .............. $((Invoke-Externo 'git' @('config','--global','user.name')).Saida)"  -ForegroundColor Gray
Write-Host "  E-mail ............ $((Invoke-Externo 'git' @('config','--global','user.email')).Saida)" -ForegroundColor Gray
Write-Host "  Chave privada ..... $caminhoChave"                 -ForegroundColor Gray
Write-Host "  Chave publica ..... $caminhoPub"                   -ForegroundColor Gray
Write-Host "  Config SSH ........ $caminhoConfig"                -ForegroundColor Gray
Write-Host "  Teste de conexao .. $(if ($sucesso) { 'OK' } else { 'FALHOU - veja as mensagens acima' })" -ForegroundColor $(if ($sucesso) { 'Green' } else { 'Red' })

Write-Host ''
Write-Host '  Comandos uteis:' -ForegroundColor Cyan
Write-Host "    ssh -T $GitHubUsuario@$GitHubHost                     # testar a autenticacao" -ForegroundColor DarkGray
Write-Host '    ssh-add -l                            # listar chaves no agente'              -ForegroundColor DarkGray
Write-Host '    git remote -v                         # conferir a URL do repositorio'        -ForegroundColor DarkGray
Write-Host ''

if ($sucesso) {
    Write-Host '  Tudo pronto! Voce ja pode usar git clone/pull/push via SSH.' -ForegroundColor Green
}
else {
    Write-Host '  Resolva os pontos indicados e rode o script novamente.' -ForegroundColor Yellow
}
Write-Host ''
