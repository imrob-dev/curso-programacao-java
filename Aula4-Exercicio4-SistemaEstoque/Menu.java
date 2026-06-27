import java.text.NumberFormat;
import java.util.Locale;

/**
 * A classe Menu concentra tudo que aparece colorido na tela: os menus,
 * os titulos e as mensagens de erro, sucesso e aviso.
 *
 * As cores sao feitas com codigos ANSI (textos que comecam com \033[ ).
 * Cada constante guarda uma cor para deixar o codigo mais facil de ler.
 * No fim de cada texto colorido usamos RESET para voltar a cor normal.
 */
public class Menu {

    private static final String CT_BRANCO = "\033[1;38;05;231m";
    private static final String CT_LARANJA = "\033[1;38;5;166m";
    private static final String CT_VERMELHO = "\033[38;5;1m";

    private static final String CF_CINZA = "\033[48;05;243m";
    private static final String CF_AMARELO = "\033[48;5;190m";
    private static final String CF_VERMELHO = "\033[48;5;124m";

    private static final String COR_ERRO = "\033[38;05;9m\033[48;5;16m";
    private static final String COR_SUCESSO = "\033[38;05;10m\033[48;5;16m";
    private static final String COR_AVISO = "\033[1;38;5;220m\033[48;5;16m";
    private static final String CT_PRETO = "\033[1;38;5;232m";

    private static final String RESET = "\033[0m";

    /**
     * Formatador de moeda no padrao brasileiro (ex.: R$ 3.500,00).
     */
    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /**
     * Transforma um numero em texto de dinheiro no formato brasileiro.
     *
     * @param valor O valor a ser formatado.
     * @return Texto como "R$ 3.500,00".
     */
    public static String moeda(double valor) {
        return MOEDA.format(valor).replace(' ', ' ');
    }

    /**
     * Mostra o titulo/abertura do sistema.
     */
    public static void intro() {
        System.out.printf("%s                                         %s%n", CF_AMARELO, RESET);
        System.out.printf("%s%s   ### MEGASTORE - SISTEMA DE ESTOQUE ###  %s%n", CF_AMARELO, CT_PRETO, RESET);
        System.out.printf("%s                                         %s%n", CF_AMARELO, RESET);
    }

    /**
     * Mostra o menu principal.
     */
    public static void menuPrincipal() {
        intro();
        System.out.printf("%n%s------ MENU PRINCIPAL ------%s%n", CT_LARANJA, RESET);
        System.out.println("[ 1 ] - Gerenciar Produtos");
        System.out.println("[ 2 ] - Gerenciar Categorias");
        System.out.println("[ 3 ] - Relatorios");
        System.out.printf("%s[ 0 ] - Sair%s%n", CT_VERMELHO, RESET);
    }

    /**
     * Mostra o sub-menu de produtos (operacoes CRUD).
     */
    public static void menuProdutos() {
        System.out.printf("%s------ GERENCIAR PRODUTOS ------%s%n", CT_LARANJA, RESET);
        System.out.println("[ 1 ] - Inserir Produto");
        System.out.println("[ 2 ] - Listar Produtos");
        System.out.println("[ 3 ] - Alterar Produto");
        System.out.println("[ 4 ] - Excluir Produto");
        System.out.printf("%s[ 0 ] - Voltar%s%n", CT_VERMELHO, RESET);
    }

    /**
     * Mostra o sub-menu de categorias (operacoes CRUD).
     */
    public static void menuCategorias() {
        System.out.printf("%s------ GERENCIAR CATEGORIAS ------%s%n", CT_LARANJA, RESET);
        System.out.println("[ 1 ] - Inserir Categoria");
        System.out.println("[ 2 ] - Listar Categorias");
        System.out.println("[ 3 ] - Alterar Categoria");
        System.out.println("[ 4 ] - Excluir Categoria");
        System.out.printf("%s[ 0 ] - Voltar%s%n", CT_VERMELHO, RESET);
    }

    /**
     * Mostra o sub-menu de relatorios.
     */
    public static void menuRelatorios() {
        System.out.printf("%s------ RELATORIOS ------%s%n", CT_LARANJA, RESET);
        System.out.println("[ 1 ] - Relatorio de Estoque");
        System.out.println("[ 2 ] - Relatorio por Categoria");
        System.out.printf("%s[ 0 ] - Voltar%s%n", CT_VERMELHO, RESET);
    }

    /**
     * Mostra um titulo destacado (fundo cinza, texto branco).
     *
     * @param texto Texto do titulo.
     */
    public static void titulo(String texto) {
        System.out.printf("%s%s   %s   %s%n%n", CT_BRANCO, CF_CINZA, texto.toUpperCase(), RESET);
    }

    /**
     * Mostra a mensagem padrao de opcao invalida.
     */
    public static void opcaoInvalida() {
        System.out.printf("%s%s   OPCAO INVALIDA!   %s%n", CT_BRANCO, CF_VERMELHO, RESET);
    }

    /**
     * Mostra uma mensagem de erro.
     *
     * @param texto Texto do erro.
     */
    public static void erro(String texto) {
        System.out.printf("%s %s %s%n", COR_ERRO, texto, RESET);
    }

    /**
     * Mostra uma mensagem de sucesso.
     *
     * @param texto Texto do sucesso.
     */
    public static void sucesso(String texto) {
        System.out.printf("%s %s %s%n", COR_SUCESSO, texto, RESET);
    }

    /**
     * Mostra uma mensagem de aviso (usada, por exemplo, para estoque baixo).
     *
     * @param texto Texto do aviso.
     */
    public static void aviso(String texto) {
        System.out.printf("%s %s %s%n", COR_AVISO, texto, RESET);
    }
}
