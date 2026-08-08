# Exercício 4 — Sistema de Estoque com CRUD (JAVA-104)

Solução do **Exercício 4 da Aula 4 (Métodos em Java)**.

É um sistema de estoque de terminal com operações **CRUD** (Create, Read,
Update, Delete) para **produtos** e **categorias**, além de **relatórios**.
Os menus e tabelas são coloridos, usando cores com códigos **ANSI** e tabelas
com **largura de coluna automática**.

O código está organizado em **camadas** (`gui`, `service`, `model` e `dao`),
foi feito para ser **simples de entender** — só sintaxe básica de Java — e é
**compatível com Java 8**.

---

## Como executar

Dentro desta pasta, abra um terminal e rode:

```bash
javac Main.java model/*.java gui/*.java service/*.java
java Main
```

Como agora as classes estão em **pacotes** (`model`, `gui`, `service`),
não dá mais para usar só `javac *.java`: é preciso listar as pastas dos
pacotes. O `java Main` continua igual, porque a pasta do exercício é a raiz
dos pacotes.

> 💡 As cores funcionam em terminais que suportam ANSI (Windows Terminal,
> PowerShell, terminal do VS Code, Linux e macOS). Em janelas muito antigas
> do `cmd.exe` as cores podem não aparecer, mas o programa funciona igual.

---

## Menu do sistema

```
MENU PRINCIPAL
[ 1 ] - Gerenciar Produtos     -> sub-menu CRUD
[ 2 ] - Gerenciar Categorias   -> sub-menu CRUD
[ 3 ] - Relatorios             -> relatório de estoque e por categoria
[ 0 ] - Sair
```

Cada sub-menu funciona em **loop** até escolher "Voltar", e o menu principal
roda em loop até escolher "Sair".

---

## Estrutura de pastas (camadas)

Cada **pasta** é um pacote Java e representa uma **camada** do sistema:

```
aula-04-sistema-estoque/
├── Main.java              -> ponto de entrada (só monta e liga o sistema)
├── model/                 -> os DADOS
│   ├── Produto.java
│   ├── Categoria.java
│   └── Estoque.java
├── gui/                   -> a TELA (mostra e lê o teclado)
│   ├── Console.java
│   ├── Menu.java
│   ├── TabelaPrinter.java
│   └── MenuConsole.java
├── service/               -> as REGRAS DE NEGÓCIO
│   └── EstoqueService.java
└── dao/                   -> a PERSISTÊNCIA (ainda vazio, ver dao/README.md)
```

Cada classe tem **uma responsabilidade** (boa prática de coesão vista na aula):

| Camada    | Arquivo                 | Responsabilidade                                             |
|-----------|-------------------------|--------------------------------------------------------------|
| —         | `Main.java`             | Cria o service e a tela, e manda a tela iniciar — nada mais  |
| `model`   | `Produto.java`          | Entidade: dados de um produto (nome, preço, qtde, categoria) |
| `model`   | `Categoria.java`        | Entidade: dados de uma categoria                             |
| `model`   | `Estoque.java`          | Só **guarda** os dados em `ArrayList` (inserir/buscar/remover)|
| `gui`     | `Console.java`          | Lê e **valida** tudo que o usuário digita                    |
| `gui`     | `Menu.java`             | Textos coloridos: menus e mensagens (cores ANSI)             |
| `gui`     | `TabelaPrinter.java`    | Exibe os dados em **tabelas coloridas** alinhadas            |
| `gui`     | `MenuConsole.java`      | O **fluxo**: mostra os menus em loop e chama o service        |
| `service` | `EstoqueService.java`   | As **regras**: CRUD, validações e cálculos dos relatórios    |
| `dao`     | *(reservado)*           | Acesso a dados — será visto na **Aula 12 (JDBC e DAO)**      |

### Quem conversa com quem

```
Main  ->  GUI  ->  Service  ->  Model
```

- A **gui** não decide nada: ela só mostra, lê o teclado e pergunta ao service.
- O **service** não mostra nada: ele não tem nenhum `System.out` nem `Scanner`.
- O **model** não sabe que existe tela: ele só guarda e devolve dados.

Um exemplo dessa divisão é a exclusão de categoria. Quem **decide** é o service:

```java
// service/EstoqueService.java  -> a REGRA
public boolean podeExcluirCategoria(int indice) {
    return contarProdutosDaCategoria(estoque.getCategoria(indice)) == 0;
}
```

```java
// gui/MenuConsole.java  -> só a MENSAGEM
if (!servico.podeExcluirCategoria(numero - 1)) {
    Menu.erro("Nao e possivel excluir: existem produtos nesta categoria.");
    ...
}
```

### Por que essa divisão ajuda no futuro

Quando o sistema virar uma aplicação **desktop com Java Swing**, só a camada
`gui` é trocada — o `MenuConsole` dá lugar a uma janela (`JFrame`), e o
`service` e o `model` continuam **exatamente iguais**, porque eles nunca
souberam que a tela era um terminal:

```
hoje:     gui/MenuConsole.java  ->  service  ->  model
amanhã:   gui/TelaProdutos.java ->  service  ->  model  (só a gui mudou)
```

É por isso que o `Main` cria **um único** `EstoqueService` e o entrega para a
tela pelo construtor: no Swing haverá várias janelas, e todas precisam usar o
mesmo service — senão cada uma teria a sua própria cópia dos dados.

> ⚠️ A pasta `dao/` está **propositalmente vazia** por enquanto. Hoje quem
> guarda os dados é o `Estoque`, em listas na memória — por isso tudo se perde
> ao fechar o programa. A explicação completa está em [`dao/README.md`](dao/README.md).

---

## O que o exercício pede e onde está implementado

- ✅ **Menu principal em loop até "Sair"** → `EstoqueController.iniciar`
- ✅ **Sub-menus em loop com "Voltar"** → `menuProdutos`, `menuCategorias`, `menuRelatorios`
- ✅ **Scanner para ler as entradas** → classe `Console`
- ✅ **CRUD completo de produtos e categorias** → métodos `inserir/listar/alterar/excluir`
- ✅ **Um método separado para cada funcionalidade** → `MenuConsole` (tela) e `EstoqueService` (regras)
- ✅ **Relatórios calculam o valor total por categoria** → `EstoqueService.calcularValorDaCategoria` e `contarUnidadesDaCategoria`
- ✅ **Não permite excluir categoria com produtos vinculados** → `EstoqueService.podeExcluirCategoria`
- ✅ **Desafio extra — validação de dados** → preço e quantidade não podem ser negativos (`Console.lerPrecoNaoNegativo` / `lerInteiroNaoNegativo`) e aviso de estoque baixo (`EstoqueService.estoqueBaixo` / `ESTOQUE_MINIMO`)

---

## Armazenamento com `ArrayList`

Os dados são guardados em **`ArrayList`** (e não em arrays de tamanho fixo):

```java
private final List<Produto> produtos = new ArrayList<Produto>();
private final List<Categoria> categorias = new ArrayList<Categoria>();
```

O que isso muda na prática:

| Com array de tamanho fixo                    | Com `ArrayList`                              |
|----------------------------------------------|----------------------------------------------|
| Capacidade máxima (`MAX`) — dava "cheio"      | Cresce sozinho, sem limite fixo              |
| Contador manual (`totalProdutos++`)           | O próprio `size()` sabe quantos itens tem    |
| `for` para "empurrar" os itens ao remover     | `remove(indice)` já reorganiza a lista        |
| `for (int i = 0; i < total; i++)`             | `for (Produto p : estoque.getProdutos())`    |

A declaração usa `List` (a interface) e a criação usa `ArrayList` (a
implementação) — assim o resto do código depende apenas do "contrato" `List`.

A classe `TabelaPrinter` também usa listas: o cabeçalho é uma `List<String>` e
a tabela é uma `List<List<String>>` (uma lista de linhas).

---

## Técnicas de cores e tabelas

**Cores (ANSI):** cada cor fica em uma constante (ex.: `"\033[48;05;243m"`),
e há métodos prontos como `Menu.titulo()`, `Menu.erro()`, `Menu.sucesso()` e
`Menu.aviso()` para padronizar as mensagens.

**Tabelas:** a classe `TabelaPrinter` descobre sozinha a largura de cada coluna
(olhando o maior texto da coluna) e alinha tudo com `printf("%-Ns")`, pintando
o cabeçalho com fundo cinza e as linhas com fundo claro. O trabalho está
dividido em três métodos pequenos: `calcularLarguras`, `imprimirLinha` e
`imprimir`, que junta os dois.

---

## Observação sobre os dados de exemplo

Ao iniciar, o sistema já vem com algumas categorias e produtos de exemplo
(método `carregarDadosExemplo` no `EstoqueService`). Para começar com o
estoque **vazio**, basta apagar a chamada dentro do `Main`:

```java
servico.carregarDadosExemplo();   // <- apague esta linha
```

Esse método deixa de existir quando o **DAO** for implementado: aí os dados
passam a vir do banco de dados.
