# Exercício 4 — Sistema de Estoque com CRUD (JAVA-104)

Solução do **Exercício 4 da Aula 4 (Métodos em Java)**.

É um sistema de estoque de terminal com operações **CRUD** (Create, Read,
Update, Delete) para **produtos** e **categorias**, além de **relatórios**.
Os menus e tabelas são coloridos, usando cores com códigos **ANSI** e tabelas
com **largura de coluna automática**.

O código foi feito para ser **simples de entender** e é **compatível com Java 8**.

---

## Como executar

Dentro desta pasta, abra um terminal e rode:

```bash
javac *.java
java Main
```

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

## Estrutura dos arquivos

Cada classe tem **uma responsabilidade** (boa prática de coesão vista na aula):

| Arquivo              | Responsabilidade                                            |
|----------------------|-------------------------------------------------------------|
| `Produto.java`       | Entidade: dados de um produto (nome, preço, qtde, categoria)|
| `Categoria.java`     | Entidade: dados de uma categoria                            |
| `Estoque.java`       | Guarda os dados (arrays) e faz inserir/buscar/remover       |
| `Console.java`       | Lê e **valida** tudo que o usuário digita                   |
| `Menu.java`          | Textos coloridos: menus e mensagens (cores ANSI)            |
| `TabelaPrinter.java` | Exibe os dados em **tabelas coloridas** alinhadas           |
| `Main.java`          | Junta tudo e controla o fluxo dos menus                     |

---

## O que o exercício pede e onde está implementado

- ✅ **Menu principal em loop até "Sair"** → `Main.main`
- ✅ **Sub-menus em loop com "Voltar"** → `menuProdutos`, `menuCategorias`, `menuRelatorios`
- ✅ **Scanner para ler as entradas** → classe `Console`
- ✅ **CRUD completo de produtos e categorias** → métodos `inserir/listar/alterar/excluir`
- ✅ **Um método separado para cada funcionalidade** → toda a classe `Main`
- ✅ **Relatórios calculam o valor total por categoria** → `relatorioEstoque` e `relatorioPorCategoria`
- ✅ **Não permite excluir categoria com produtos vinculados** → `excluirCategoria` + `Estoque.contarProdutosDaCategoria`
- ✅ **Desafio extra — validação de dados** → preço e quantidade não podem ser negativos (`Console.lerPrecoNaoNegativo` / `lerInteiroNaoNegativo`) e aviso de estoque baixo (`Estoque.ESTOQUE_MINIMO`)

---

## Técnicas de cores e tabelas

**Cores (ANSI):** cada cor fica em uma constante (ex.: `"\033[48;05;243m"`),
e há métodos prontos como `Menu.titulo()`, `Menu.erro()`, `Menu.sucesso()` e
`Menu.aviso()` para padronizar as mensagens.

**Tabelas:** a classe `TabelaPrinter` descobre sozinha a largura de cada coluna
(olhando o maior texto da coluna) e alinha tudo com `printf("%-Ns")`, pintando
o cabeçalho com fundo cinza e as linhas com fundo claro.

---

## Observação sobre os dados de exemplo

Ao iniciar, o sistema já vem com algumas categorias e produtos de exemplo
(método `carregarDadosExemplo` no `Main`). Para começar com o estoque **vazio**,
basta apagar esse método e a chamada a ele dentro do `main`.
