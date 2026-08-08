# Pacote `dao` — reservado (ainda sem implementação)

Esta pasta é a camada de **persistência** do sistema, mas está
**propositalmente vazia**: o padrão DAO só é estudado na
**Aula 12 — JDBC e DAO**.

## O que é o DAO

**DAO** = *Data Access Object* (Objeto de Acesso a Dados).

É a camada que sabe **onde os dados ficam guardados** (banco de dados, arquivo,
memória...) e isola esse detalhe do resto do sistema. Assim, trocar o banco de
dados por um arquivo — ou vice-versa — não obriga a mexer na `gui`, nem no
`service`, nem no `model`.

## Onde os dados estão hoje

Por enquanto, quem guarda os dados é a classe **`model/Estoque.java`**, usando
duas listas em memória:

```java
private final List<Produto> produtos = new ArrayList<Produto>();
private final List<Categoria> categorias = new ArrayList<Categoria>();
```

Por isso os dados **somem quando o programa fecha** — é justamente o problema
que o DAO vem resolver.

## Como isso vai evoluir na Aula 12

O plano é criar aqui as classes de acesso a dados (algo como `ProdutoDAO` e
`CategoriaDAO`, com os métodos `inserir`, `listar`, `atualizar` e `excluir`).
O `EstoqueService` deixa de usar o `Estoque` e passa a **pedir os dados ao
DAO**, sem saber se eles vêm de um banco ou de um arquivo:

```
hoje:     GUI -> Service -> Estoque (listas na memória)
amanhã:   GUI -> Service -> DAO -> banco de dados
```

A `gui` **não muda nada**, e o `service` muda só na linha em que ele busca os
dados — as regras de negócio continuam iguais. É exatamente esse o ganho de ter
separado as camadas desde agora.
