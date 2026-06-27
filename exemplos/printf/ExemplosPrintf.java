import java.util.Locale;

/**
 * Arquivo de estudo sobre o metodo System.out.printf.
 *
 * Cada bloco abaixo mostra um recurso do printf com um comentario explicando
 * o que acontece e qual a saida esperada. Rode o programa e compare a saida
 * do terminal com os comentarios.
 *
 * Para executar:
 *   javac ExemplosPrintf.java
 *   java ExemplosPrintf
 */
public class ExemplosPrintf {

    public static void main(String[] args) {

        // ============================================================
        // 1) AS LETRAS (o tipo do valor que entra no lugar do %)
        // ============================================================

        // %s = String (texto)
        // Saida: Ola, Java!
        System.out.printf("%s, %s!%n", "Ola", "Java");

        // %d = numero inteiro (int, long)
        // Saida: Voce tem 3 mensagens
        System.out.printf("Voce tem %d mensagens%n", 3);

        // %f = numero com casas decimais (double, float)
        // Por padrao o %f mostra 6 casas decimais.
        // Saida: 3,500000  (em maquina BR a virgula e o separador decimal)
        System.out.printf("%f%n", 3.5);

        // %c = um unico caractere (char)
        // Saida: Sexo: M
        System.out.printf("Sexo: %c%n", 'M');

        // %b = boolean (true / false)
        // Saida: Aprovado: true
        System.out.printf("Aprovado: %b%n", true);

        // %% = imprime o simbolo de porcentagem literal
        // Saida: Desconto de 10%
        System.out.printf("Desconto de 10%%%n");

        // %n = quebra de linha (igual apertar Enter)
        // Use %n no lugar de \n: ele usa a quebra correta de cada sistema.
        System.out.printf("Linha 1%nLinha 2%n");


        // ============================================================
        // 2) PRECISAO com %f  ->  .numeroDeCasas
        // ============================================================

        // %.2f = duas casas decimais
        // Saida: 3,50
        System.out.printf("%.2f%n", 3.5);

        // %.0f = nenhuma casa decimal (arredonda)
        // Saida: 4
        System.out.printf("%.0f%n", 3.5);


        // ============================================================
        // 3) LARGURA (tamanho minimo do campo) e a flag - (esquerda)
        // ============================================================

        // %10s = ocupa no minimo 10 caracteres, alinhado a DIREITA (padrao)
        // Saida: |     Mouse|
        System.out.printf("|%10s|%n", "Mouse");

        // %-10s = o sinal - alinha a ESQUERDA
        // Saida: |Mouse     |
        System.out.printf("|%-10s|%n", "Mouse");

        // Largura tambem funciona com numeros.
        // Saida: |        42|
        System.out.printf("|%10d|%n", 42);


        // ============================================================
        // 4) OUTRAS FLAGS uteis
        // ============================================================

        // 0 = preenche o espaco que sobra com ZEROS (muito usado em codigos)
        // Saida: 00042
        System.out.printf("%05d%n", 42);

        // + = mostra o sinal tambem nos numeros positivos
        // Saida: +42
        System.out.printf("%+d%n", 42);

        // , = agrupa os milhares (o separador depende do idioma da maquina)
        // Saida (BR): 1.000.000
        System.out.printf("%,d%n", 1000000);


        // ============================================================
        // 5) LARGURA DINAMICA (montando o formato com concatenacao)
        // ============================================================

        // Esta e a tecnica usada nas tabelas: a largura nao e fixa,
        // ela e calculada e juntada na string de formato com o +.
        int largura = 12;
        // O formato vira "%-12s" e o texto fica alinhado a esquerda nessa largura.
        // Saida: |Notebook    |
        System.out.printf("|%-" + largura + "s|%n", "Notebook");


        // ============================================================
        // 6) VARIOS VALORES NA MESMA LINHA (ordem importa!)
        // ============================================================

        // Os marcadores consomem os argumentos NA ORDEM em que aparecem.
        // 1o %s -> "Notebook", %d -> 15, %.2f -> 3500.0
        // Saida: Produto: Notebook | Qtde: 15 | Preco: 3500,00
        System.out.printf("Produto: %s | Qtde: %d | Preco: %.2f%n", "Notebook", 15, 3500.0);


        // ============================================================
        // 7) CONTROLANDO O IDIOMA (Locale) - separador decimal
        // ============================================================

        // Por padrao o printf usa o idioma da maquina. Da para forcar um
        // idioma passando um Locale como primeiro argumento.

        // Locale.US -> usa PONTO como separador decimal
        // Saida: 3.50
        System.out.printf(Locale.US, "%.2f%n", 3.5);

        // Locale pt-BR -> usa VIRGULA como separador decimal
        // Saida: 3,50
        System.out.printf(new Locale("pt", "BR"), "%.2f%n", 3.5);


        // ============================================================
        // 8) REUSANDO O MESMO ARGUMENTO com indice  ->  %1$, %2$ ...
        // ============================================================

        // O 1$ aponta para o 1o argumento, o 2$ para o 2o, e assim por diante.
        // Aqui usamos o argumento "Ana" duas vezes.
        // Saida: Ola Ana, seja bem-vinda Ana!
        System.out.printf("Ola %1$s, seja bem-vinda %1$s!%n", "Ana");
    }
}
