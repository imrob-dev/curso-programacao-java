import java.util.Scanner;

/**
 * A classe Console cuida de TODA a entrada de dados do usuario.
 *
 * Para evitar erros comuns de iniciante (como o problema do nextInt() que
 * deixa "lixo" no Scanner), aqui sempre lemos uma linha inteira com
 * nextLine() e depois transformamos esse texto no tipo que precisamos.
 * Se o usuario digitar algo errado, mostramos uma mensagem e pedimos de novo.
 */
public class Console {

    private static final Scanner sc = new Scanner(System.in);

    /**
     * Le um numero inteiro qualquer (usado nos menus).
     *
     * @return O numero digitado pelo usuario.
     */
    public static int lerOpcao() {
        while (true) {
            System.out.print("\nDigite uma opcao: ");
            String linha = sc.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                Menu.erro("Digite um numero valido.");
            }
        }
    }

    /**
     * Le um texto que nao pode ficar vazio.
     *
     * @param rotulo Texto mostrado antes da digitacao.
     * @return O texto digitado.
     */
    public static String lerTexto(String rotulo) {
        while (true) {
            System.out.print(rotulo + ": ");
            String texto = sc.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            Menu.erro("Este campo nao pode ficar vazio.");
        }
    }

    /**
     * Le um valor de dinheiro que nao pode ser negativo (aceita virgula ou ponto).
     *
     * @param rotulo Texto mostrado antes da digitacao.
     * @return O valor digitado (sempre maior ou igual a zero).
     */
    public static double lerPrecoNaoNegativo(String rotulo) {
        while (true) {
            System.out.print(rotulo + ": ");
            String linha = sc.nextLine().trim().replace(",", ".");
            try {
                double valor = Double.parseDouble(linha);
                if (valor >= 0) {
                    return valor;
                }
                Menu.erro("O valor nao pode ser negativo.");
            } catch (NumberFormatException e) {
                Menu.erro("Digite um valor numerico valido.");
            }
        }
    }

    /**
     * Le uma quantidade inteira que nao pode ser negativa.
     *
     * @param rotulo Texto mostrado antes da digitacao.
     * @return A quantidade digitada (sempre maior ou igual a zero).
     */
    public static int lerInteiroNaoNegativo(String rotulo) {
        while (true) {
            System.out.print(rotulo + ": ");
            String linha = sc.nextLine().trim();
            try {
                int valor = Integer.parseInt(linha);
                if (valor >= 0) {
                    return valor;
                }
                Menu.erro("O valor nao pode ser negativo.");
            } catch (NumberFormatException e) {
                Menu.erro("Digite um numero inteiro valido.");
            }
        }
    }

    /**
     * Le um numero que precisa estar entre 1 e total (escolha de item da lista).
     *
     * @param rotulo Texto mostrado antes da digitacao.
     * @param total  Quantidade de itens da lista.
     * @return Um numero valido entre 1 e total.
     */
    public static int lerIndice(String rotulo, int total) {
        while (true) {
            System.out.print(rotulo + ": ");
            String linha = sc.nextLine().trim();
            try {
                int numero = Integer.parseInt(linha);
                if (numero >= 1 && numero <= total) {
                    return numero;
                }
                Menu.erro("Escolha um numero entre 1 e " + total + ".");
            } catch (NumberFormatException e) {
                Menu.erro("Digite um numero valido.");
            }
        }
    }

    /**
     * Faz uma pergunta de Sim ou Nao ao usuario.
     *
     * @param pergunta Pergunta a ser exibida.
     * @return true para S, false para N.
     */
    public static boolean confirmar(String pergunta) {
        while (true) {
            System.out.print(pergunta + " (S/N)? ");
            String resposta = sc.nextLine().trim();
            if (resposta.equalsIgnoreCase("S")) {
                return true;
            }
            if (resposta.equalsIgnoreCase("N")) {
                return false;
            }
            Menu.erro("Responda com S ou N.");
        }
    }

    /**
     * Pausa o programa ate o usuario apertar ENTER.
     */
    public static void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        sc.nextLine();
    }

    /**
     * Limpa a tela do terminal (cls no Windows, codigo ANSI nos demais).
     */
    public static void limpar() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
