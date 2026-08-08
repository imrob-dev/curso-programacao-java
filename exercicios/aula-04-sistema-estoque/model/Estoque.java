package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A classe Estoque e o "deposito" dos dados do sistema: ela apenas GUARDA
 * os produtos e as categorias e sabe inserir, buscar e remover.
 *
 * Repare que aqui nao existe nenhuma regra de negocio (isso fica no service)
 * e nada de tela (isso fica na gui). Essa separacao deixa a classe bem
 * simples: ela so cuida da lista.
 *
 * Usamos ARRAYLIST no lugar de arrays de tamanho fixo. As vantagens sao:
 *   - a lista cresce sozinha, entao nao existe mais "estoque cheio";
 *   - o proprio ArrayList sabe quantos itens tem (metodo size()), por isso
 *     nao precisamos de um contador manual;
 *   - ao remover um item com remove(indice), o ArrayList ja empurra os
 *     demais para a esquerda sozinho, sem precisarmos de um for.
 *
 * Declaramos as variaveis como List (a interface) e criamos como ArrayList
 * (a implementacao). Isso e uma boa pratica: o resto do codigo so depende
 * do "contrato" List.
 */
public class Estoque {

    private final List<Produto> produtos = new ArrayList<Produto>();
    private final List<Categoria> categorias = new ArrayList<Categoria>();

    /**
     * Adiciona um produto ao estoque.
     *
     * @param produto O produto a ser adicionado.
     */
    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    /**
     * Devolve o produto que esta em uma posicao da lista.
     *
     * @param indice Posicao do produto (comecando em 0).
     * @return O produto daquela posicao.
     */
    public Produto getProduto(int indice) {
        return produtos.get(indice);
    }

    /**
     * Devolve a lista completa de produtos, util para percorrer com for-each.
     *
     * @return A lista de produtos.
     */
    public List<Produto> getProdutos() {
        return produtos;
    }

    /**
     * Remove o produto de uma posicao. O ArrayList reorganiza a lista sozinho.
     *
     * @param indice Posicao do produto a remover (comecando em 0).
     */
    public void removerProduto(int indice) {
        produtos.remove(indice);
    }

    public int getTotalProdutos() {
        return produtos.size();
    }

    /**
     * Adiciona uma categoria ao estoque.
     *
     * @param categoria A categoria a ser adicionada.
     */
    public void adicionarCategoria(Categoria categoria) {
        categorias.add(categoria);
    }

    /**
     * Devolve a categoria que esta em uma posicao da lista.
     *
     * @param indice Posicao da categoria (comecando em 0).
     * @return A categoria daquela posicao.
     */
    public Categoria getCategoria(int indice) {
        return categorias.get(indice);
    }

    /**
     * Devolve a lista completa de categorias, util para percorrer com for-each.
     *
     * @return A lista de categorias.
     */
    public List<Categoria> getCategorias() {
        return categorias;
    }

    /**
     * Remove a categoria de uma posicao. O ArrayList reorganiza a lista sozinho.
     *
     * @param indice Posicao da categoria a remover (comecando em 0).
     */
    public void removerCategoria(int indice) {
        categorias.remove(indice);
    }

    public int getTotalCategorias() {
        return categorias.size();
    }
}
