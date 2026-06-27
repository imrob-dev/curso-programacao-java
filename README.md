# ☕ Curso de Programação Java

Repositório com os materiais do curso de Java: **slides** das aulas, **exercícios
resolvidos**, **exemplos de código** e uma **apostila de consulta rápida**.

> Todo o código é **compatível com Java 8**.

---

## 📂 Estrutura do repositório

```
curso-programacao-java/
├── slides/        → slides das aulas (PDF)
├── exercicios/    → exercícios resolvidos (código Java)
├── exemplos/      → exemplos curtos de código
└── apostila/      → apostila de consulta rápida (md, html, pdf)
```

---

## 📚 Aulas

| #  | Tema | Slides | Código |
|----|------|:------:|:------:|
| 01 | Introdução e Variáveis | [📄 slide](slides/Aula%2001%20-%20Introducao%20-%20Variaveis.pdf) | — |
| 02 | Estruturas de Controle e Laços de Repetição | [📄 slide](slides/Aula%2002%20-%20Estruturas%20de%20Controle%20e%20Lacos%20de%20Repeticao.pdf) · [📝 exercícios](slides/Aula%2002%20-%20Exercicios.pdf) | — |
| 03 | Arrays e Strings em Java | [📄 slide](slides/Aula%2003%20-%20Arrays%20e%20Strings%20em%20Java.pdf) | — |
| 04 | Métodos em Java | [📄 slide](slides/Aula%2004%20-%20Metodos%20em%20Java.pdf) | [💻 Sistema de Estoque](exercicios/aula-04-sistema-estoque) · [💻 printf](exemplos/printf) |
| 05 | Classes — Programação Orientada a Objetos (POO) | [📄 slide](slides/Aula%2005%20-%20Classes%20-%20Programacao%20Orientada%20a%20Objetos%20%28POO%29.pdf) | — |
| 06 | Encapsulamento | [📄 slide](slides/Aula%2006%20-%20Encapsulamento.pdf) | — |
| 07 | POO — Herança e Polimorfismo | [📄 slide](slides/Aula%2007%20-%20POO%20-%20Heranca%20e%20Polimorfismo.pdf) | — |
| 08 | Interfaces e Classes Abstratas | [📄 slide](slides/Aula%2008%20-%20Interfaces%20e%20Classes%20Abstratas.pdf) | — |
| 09 | Collections — List | [📄 slide](slides/Aula%2009%20-%20Collections%20-%20List.pdf) | — |
| 10 | Collections — Set e Map | [📄 slide](slides/Aula%2010%20-%20Collections%20-%20Set%20e%20Map.pdf) | — |

---

## 💻 Exercícios

| Exercício | Aula | Descrição |
|-----------|:----:|-----------|
| [Sistema de Estoque (CRUD)](exercicios/aula-04-sistema-estoque) | 04 | Sistema de terminal com CRUD de produtos e categorias, relatórios e menus/tabelas coloridos. [Ver README](exercicios/aula-04-sistema-estoque/README.md) |

---

## 🧪 Exemplos

| Exemplo | Descrição |
|---------|-----------|
| [printf](exemplos/printf) | Programa comentado mostrando os símbolos e recursos do `System.out.printf`. |

---

## 📘 Apostila de consulta rápida

Resumo do conteúdo de Java básico (das variáveis até Collections):

- [📄 PDF (com design)](apostila/Apostila%20-%20Java%20Basico.pdf)
- [📝 Markdown](apostila/Apostila%20-%20Java%20Basico.md)
- [🌐 HTML](apostila/Apostila%20-%20Java%20Basico.html)

---

## ▶️ Como rodar os códigos Java

Os exemplos e exercícios são compatíveis com **Java 8 ou superior**.
Entre na pasta do código e rode:

```bash
javac *.java     # compila todos os arquivos .java
java Main        # executa (troque "Main" pela classe que tem o metodo main)
```

> 💡 Para o exemplo de `printf`, a classe principal é `ExemplosPrintf`
> (use `java ExemplosPrintf`).
