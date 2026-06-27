/**
 * A classe Estoque guarda os dados do sistema (produtos e categorias) e
 * oferece os metodos para inserir, buscar e remover esses dados.
 *
 * Para manter o codigo no nivel basico, usamos ARRAYS de tamanho fixo
 * (assunto da Aula 3) junto com um contador que diz quantas posicoes
 * estao realmente preenchidas. Ao remover um item, "empurramos" os
 * demais para a esquerda para que a lista fique sempre sem buracos.
 */
public class Estoque {

    /** Capacidade maxima de produtos e de categorias. */
    private static final int MAX = 100;

    /** Quantidade abaixo da qual o produto e considerado "estoque baixo". */
    public static final int ESTOQUE_MINIMO = 10;

    private final Produto[] produtos = new Produto[MAX];
    private int totalProdutos = 0;

    private final Categoria[] categorias = new Categoria[MAX];
    private int totalCategorias = 0;

    /**
     * Adiciona um produto ao estoque.
     *
     * @param produto O produto a ser adicionado.
     * @return true se conseguiu adicionar; false se o estoque estiver cheio.
     */
    public boolean adicionarProduto(Produto produto) {
        if (totalProdutos >= MAX) {
            return false;
        }
        produtos[totalProdutos] = produto;
        totalProdutos++;
        return true;
    }

    /**
     * Devolve o produto que esta em uma posicao da lista.
     *
     * @param indice Posicao do produto (comecando em 0).
     * @return O produto daquela posicao.
     */
    public Produto getProduto(int indice) {
        return produtos[indice];
    }

    /**
     * Remove o produto de uma posicao, empurrando os demais para a esquerda.
     *
     * @param indice Posicao do produto a remover (comecando em 0).
     */
    public void removerProduto(int indice) {
        for (int i = indice; i < totalProdutos - 1; i++) {
            produtos[i] = produtos[i + 1];
        }
        produtos[totalProdutos - 1] = null;
        totalProdutos--;
    }

    public int getTotalProdutos() {
        return totalProdutos;
    }

    /**
     * Adiciona uma categoria ao estoque.
     *
     * @param categoria A categoria a ser adicionada.
     * @return true se conseguiu adicionar; false se atingiu o limite.
     */
    public boolean adicionarCategoria(Categoria categoria) {
        if (totalCategorias >= MAX) {
            return false;
        }
        categorias[totalCategorias] = categoria;
        totalCategorias++;
        return true;
    }

    /**
     * Devolve a categoria que esta em uma posicao da lista.
     *
     * @param indice Posicao da categoria (comecando em 0).
     * @return A categoria daquela posicao.
     */
    public Categoria getCategoria(int indice) {
        return categorias[indice];
    }

    /**
     * Remove a categoria de uma posicao, empurrando as demais para a esquerda.
     *
     * @param indice Posicao da categoria a remover (comecando em 0).
     */
    public void removerCategoria(int indice) {
        for (int i = indice; i < totalCategorias - 1; i++) {
            categorias[i] = categorias[i + 1];
        }
        categorias[totalCategorias - 1] = null;
        totalCategorias--;
    }

    public int getTotalCategorias() {
        return totalCategorias;
    }

    /**
     * Conta quantos produtos estao ligados a uma categoria.
     * Usado nos relatorios e para impedir a exclusao de categoria com produtos.
     *
     * @param categoria A categoria a ser consultada.
     * @return A quantidade de produtos daquela categoria.
     */
    public int contarProdutosDaCategoria(Categoria categoria) {
        int conta = 0;
        for (int i = 0; i < totalProdutos; i++) {
            if (produtos[i].getCategoria() == categoria) {
                conta++;
            }
        }
        return conta;
    }
}
