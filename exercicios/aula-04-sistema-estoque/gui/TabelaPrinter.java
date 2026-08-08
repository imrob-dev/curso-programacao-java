package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Categoria;
import model.Produto;
import service.EstoqueService;

/**
 * A classe TabelaPrinter exibe os dados em forma de tabela colorida no terminal.
 *
 * A tecnica e:
 *   1) descobrir a largura de cada coluna olhando o maior texto daquela coluna;
 *   2) imprimir o cabecalho com fundo cinza e texto branco;
 *   3) imprimir cada linha com fundo claro e texto escuro, tudo alinhado.
 *
 * O alinhamento e feito com "%-Ns" no printf, onde N e a largura da coluna.
 *
 * Aqui tambem usamos ARRAYLIST: o cabecalho e uma lista de textos e a tabela
 * e uma lista de linhas, onde cada linha e outra lista de textos.
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
     * @param linhas    Lista de linhas; cada linha tem o mesmo numero de colunas do cabecalho.
     */
    public static void imprimir(List<String> cabecalho, List<List<String>> linhas) {
        List<Integer> larguras = calcularLarguras(cabecalho, linhas);

        imprimirLinha(cabecalho, larguras, CT_CABECALHO, CF_CABECALHO);
        for (List<String> linha : linhas) {
            imprimirLinha(linha, larguras, CT_LINHA, CF_LINHA);
        }
    }

    /**
     * Descobre a largura de cada coluna: e o tamanho do maior texto da coluna,
     * contando tambem o titulo que esta no cabecalho.
     *
     * @param cabecalho Os titulos das colunas.
     * @param linhas    As linhas da tabela.
     * @return Uma lista com a largura de cada coluna.
     */
    private static List<Integer> calcularLarguras(List<String> cabecalho, List<List<String>> linhas) {
        List<Integer> larguras = new ArrayList<Integer>();
        for (String titulo : cabecalho) {
            larguras.add(titulo.length());
        }
        for (List<String> linha : linhas) {
            for (int i = 0; i < linha.size(); i++) {
                if (linha.get(i).length() > larguras.get(i)) {
                    larguras.set(i, linha.get(i).length());
                }
            }
        }
        return larguras;
    }

    /**
     * Imprime uma unica linha da tabela, ja alinhada e colorida.
     *
     * @param textos   Os textos de cada coluna desta linha.
     * @param larguras A largura de cada coluna.
     * @param corTexto Cor da letra.
     * @param corFundo Cor do fundo.
     */
    private static void imprimirLinha(List<String> textos, List<Integer> larguras,
                                      String corTexto, String corFundo) {
        for (int i = 0; i < textos.size(); i++) {
            System.out.printf("%s%s %-" + larguras.get(i) + "s %s",
                    corTexto, corFundo, textos.get(i), RESET);
        }
        System.out.println();
    }

    /**
     * Exibe a tabela de produtos e, no final, avisa quais estao com estoque baixo.
     *
     * @param servico O service de onde os produtos sao consultados.
     */
    public static void exibirProdutos(EstoqueService servico) {
        if (servico.getTotalProdutos() == 0) {
            Menu.erro("Nenhum produto cadastrado.");
            return;
        }

        List<String> cabecalho =
                Arrays.asList("No", "Nome", "Preco", "Qtde", "Categoria", "Valor Total");
        List<List<String>> linhas = new ArrayList<List<String>>();

        int numero = 1;
        for (Produto p : servico.listarProdutos()) {
            linhas.add(Arrays.asList(
                    String.valueOf(numero),
                    p.getNome(),
                    Menu.moeda(p.getPreco()),
                    String.valueOf(p.getQuantidade()),
                    p.getCategoria().getNome(),
                    Menu.moeda(p.getValorTotal())));
            numero++;
        }

        imprimir(cabecalho, linhas);
        avisarEstoqueBaixo(servico);
    }

    /**
     * Mostra, embaixo da tabela, um aviso com os produtos que estao acabando.
     * Quem sabe se um produto esta com estoque baixo e o service.
     *
     * @param servico O service de onde os produtos sao consultados.
     */
    private static void avisarEstoqueBaixo(EstoqueService servico) {
        String baixos = "";
        for (Produto p : servico.listarProdutos()) {
            if (servico.estoqueBaixo(p)) {
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
     * @param servico O service de onde as categorias sao consultadas.
     */
    public static void exibirCategorias(EstoqueService servico) {
        if (servico.getTotalCategorias() == 0) {
            Menu.erro("Nenhuma categoria cadastrada.");
            return;
        }

        List<String> cabecalho = Arrays.asList("No", "Nome", "Qtde Produtos");
        List<List<String>> linhas = new ArrayList<List<String>>();

        int numero = 1;
        for (Categoria c : servico.listarCategorias()) {
            linhas.add(Arrays.asList(
                    String.valueOf(numero),
                    c.getNome(),
                    String.valueOf(servico.contarProdutosDaCategoria(c))));
            numero++;
        }

        imprimir(cabecalho, linhas);
    }
}
