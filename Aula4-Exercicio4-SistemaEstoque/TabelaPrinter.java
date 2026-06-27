/**
 * A classe TabelaPrinter exibe os dados em forma de tabela colorida no terminal.
 *
 * A tecnica e:
 *   1) descobrir a largura de cada coluna olhando o maior texto daquela coluna;
 *   2) imprimir o cabecalho com fundo cinza e texto branco;
 *   3) imprimir cada linha com fundo claro e texto escuro, tudo alinhado.
 *
 * O alinhamento e feito com "%-Ns" no printf, onde N e a largura da coluna.
 */
public class TabelaPrinter {

    private static final String CT_CABECALHO = "\033[1;38;05;231m";
    private static final String CF_CABECALHO = "\033[48;05;243m";

    private static final String CT_LINHA = "\033[38;05;234m";
    private static final String CF_LINHA = "\033[48;05;15m";

    private static final String RESET = "\033[0m";

    /**
     * Imprime uma tabela generica a partir de um cabecalho e das linhas.
     * Calcula sozinha a largura de cada coluna.
     *
     * @param cabecalho Os titulos das colunas.
     * @param linhas    Uma matriz onde cada linha tem o mesmo numero de colunas do cabecalho.
     */
    public static void imprimir(String[] cabecalho, String[][] linhas) {
        int[] larguras = new int[cabecalho.length];
        for (int i = 0; i < cabecalho.length; i++) {
            larguras[i] = cabecalho[i].length();
        }
        for (String[] linha : linhas) {
            for (int i = 0; i < linha.length; i++) {
                if (linha[i].length() > larguras[i]) {
                    larguras[i] = linha[i].length();
                }
            }
        }

        for (int i = 0; i < cabecalho.length; i++) {
            System.out.printf("%s%s %-" + larguras[i] + "s %s",
                    CT_CABECALHO, CF_CABECALHO, cabecalho[i], RESET);
        }
        System.out.println();

        for (String[] linha : linhas) {
            for (int i = 0; i < linha.length; i++) {
                System.out.printf("%s%s %-" + larguras[i] + "s %s",
                        CT_LINHA, CF_LINHA, linha[i], RESET);
            }
            System.out.println();
        }
    }

    /**
     * Exibe a tabela de produtos e, no final, avisa quais estao com estoque baixo.
     *
     * @param estoque O estoque com os produtos.
     */
    public static void exibirProdutos(Estoque estoque) {
        int total = estoque.getTotalProdutos();
        if (total == 0) {
            Menu.erro("Nenhum produto cadastrado.");
            return;
        }

        String[] cabecalho = {"No", "Nome", "Preco", "Qtde", "Categoria", "Valor Total"};
        String[][] linhas = new String[total][cabecalho.length];

        for (int i = 0; i < total; i++) {
            Produto p = estoque.getProduto(i);
            linhas[i][0] = String.valueOf(i + 1);
            linhas[i][1] = p.getNome();
            linhas[i][2] = Menu.moeda(p.getPreco());
            linhas[i][3] = String.valueOf(p.getQuantidade());
            linhas[i][4] = p.getCategoria().getNome();
            linhas[i][5] = Menu.moeda(p.getValorTotal());
        }

        imprimir(cabecalho, linhas);

        String baixos = "";
        for (int i = 0; i < total; i++) {
            Produto p = estoque.getProduto(i);
            if (p.getQuantidade() < Estoque.ESTOQUE_MINIMO) {
                if (!baixos.isEmpty()) {
                    baixos += ", ";
                }
                baixos += p.getNome() + " (" + p.getQuantidade() + " unidades)";
            }
        }
        if (!baixos.isEmpty()) {
            System.out.println();
            Menu.aviso("Produtos com estoque baixo: " + baixos);
        }
    }

    /**
     * Exibe a tabela de categorias mostrando quantos produtos cada uma possui.
     *
     * @param estoque O estoque com as categorias.
     */
    public static void exibirCategorias(Estoque estoque) {
        int total = estoque.getTotalCategorias();
        if (total == 0) {
            Menu.erro("Nenhuma categoria cadastrada.");
            return;
        }

        String[] cabecalho = {"No", "Nome", "Qtde Produtos"};
        String[][] linhas = new String[total][cabecalho.length];

        for (int i = 0; i < total; i++) {
            Categoria c = estoque.getCategoria(i);
            linhas[i][0] = String.valueOf(i + 1);
            linhas[i][1] = c.getNome();
            linhas[i][2] = String.valueOf(estoque.contarProdutosDaCategoria(c));
        }

        imprimir(cabecalho, linhas);
    }
}
