package model;

/**
 * Representa um produto do estoque.
 *
 * Cada produto tem um nome, um preco, uma quantidade em estoque e
 * pertence a uma {@link Categoria}. Guardamos a propria Categoria
 * (e nao apenas o nome dela) para que, ao renomear uma categoria,
 * o produto continue ligado a categoria correta automaticamente.
 */
public class Produto {

    private String nome;
    private double preco;
    private int quantidade;
    private Categoria categoria;

    /**
     * Cria um novo produto.
     *
     * @param nome       Nome do produto.
     * @param preco      Preco unitario (nunca negativo).
     * @param quantidade Quantidade em estoque (nunca negativa).
     * @param categoria  Categoria a qual o produto pertence.
     */
    public Produto(String nome, double preco, int quantidade, Categoria categoria) {
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.categoria = categoria;
    }

    /**
     * Calcula o valor total deste produto em estoque (preco x quantidade).
     *
     * @return O valor total em estoque do produto.
     */
    public double getValorTotal() {
        return preco * quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
