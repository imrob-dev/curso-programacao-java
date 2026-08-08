package service;

import java.util.ArrayList;
import java.util.List;

import model.Categoria;
import model.Estoque;
import model.Produto;

/**
 * O SERVICE guarda as REGRAS DE NEGOCIO do sistema.
 *
 * Repare no que NAO existe nesta classe:
 *   - nenhum System.out.println
 *   - nenhum Scanner
 *   - nenhuma cor, nenhum menu
 *
 * Isso e proposital e e a parte mais importante da organizacao em camadas:
 * como o service nao sabe se a tela e um terminal ou uma janela, ele continua
 * valendo do jeito que esta quando o sistema virar uma aplicacao desktop com
 * Java Swing. So a view muda; o service e o model ficam iguais.
 *
 * O service tambem e quem vai conversar com o DAO no futuro (Aula 12):
 *
 *   View  ->  Service  ->  Model
 *                      ->  DAO  ->  banco de dados
 */
public class EstoqueService {

    /** Quantidade abaixo da qual o produto e considerado "estoque baixo". */
    public static final int ESTOQUE_MINIMO = 10;

    /**
     * Onde os dados ficam guardados hoje (listas na memoria).
     * Quando o DAO existir, e esta linha que vai mudar.
     */
    private final Estoque estoque = new Estoque();

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /**
     * @return A lista de todos os produtos cadastrados.
     */
    public List<Produto> listarProdutos() {
        return estoque.getProdutos();
    }

    /**
     * @return A lista de todas as categorias cadastradas.
     */
    public List<Categoria> listarCategorias() {
        return estoque.getCategorias();
    }

    /**
     * Busca um produto pela posicao que ele aparece na listagem.
     *
     * @param indice Posicao do produto (comecando em 0).
     * @return O produto daquela posicao.
     */
    public Produto buscarProduto(int indice) {
        return estoque.getProduto(indice);
    }

    /**
     * Busca uma categoria pela posicao que ela aparece na listagem.
     *
     * @param indice Posicao da categoria (comecando em 0).
     * @return A categoria daquela posicao.
     */
    public Categoria buscarCategoria(int indice) {
        return estoque.getCategoria(indice);
    }

    public int getTotalProdutos() {
        return estoque.getTotalProdutos();
    }

    public int getTotalCategorias() {
        return estoque.getTotalCategorias();
    }

    // ------------------------------------------------------------------
    // CRUD de produtos
    // ------------------------------------------------------------------

    /**
     * CREATE - Cadastra um novo produto.
     *
     * @param nome       Nome do produto.
     * @param preco      Preco unitario.
     * @param quantidade Quantidade em estoque.
     * @param categoria  Categoria do produto.
     */
    public void inserirProduto(String nome, double preco, int quantidade, Categoria categoria) {
        estoque.adicionarProduto(new Produto(nome, preco, quantidade, categoria));
    }

    /**
     * UPDATE - Altera o nome de um produto.
     *
     * @param indice Posicao do produto (comecando em 0).
     * @param nome   Novo nome.
     */
    public void alterarNomeProduto(int indice, String nome) {
        estoque.getProduto(indice).setNome(nome);
    }

    /**
     * UPDATE - Altera o preco de um produto.
     *
     * @param indice Posicao do produto (comecando em 0).
     * @param preco  Novo preco.
     */
    public void alterarPrecoProduto(int indice, double preco) {
        estoque.getProduto(indice).setPreco(preco);
    }

    /**
     * UPDATE - Altera a quantidade em estoque de um produto.
     *
     * @param indice     Posicao do produto (comecando em 0).
     * @param quantidade Nova quantidade.
     */
    public void alterarQuantidadeProduto(int indice, int quantidade) {
        estoque.getProduto(indice).setQuantidade(quantidade);
    }

    /**
     * UPDATE - Troca a categoria de um produto.
     *
     * @param indice    Posicao do produto (comecando em 0).
     * @param categoria Nova categoria.
     */
    public void alterarCategoriaProduto(int indice, Categoria categoria) {
        estoque.getProduto(indice).setCategoria(categoria);
    }

    /**
     * DELETE - Exclui um produto.
     *
     * @param indice Posicao do produto (comecando em 0).
     */
    public void excluirProduto(int indice) {
        estoque.removerProduto(indice);
    }

    // ------------------------------------------------------------------
    // CRUD de categorias
    // ------------------------------------------------------------------

    /**
     * CREATE - Cadastra uma nova categoria.
     *
     * @param nome Nome da categoria.
     */
    public void inserirCategoria(String nome) {
        estoque.adicionarCategoria(new Categoria(nome));
    }

    /**
     * UPDATE - Altera o nome de uma categoria.
     *
     * @param indice Posicao da categoria (comecando em 0).
     * @param nome   Novo nome.
     */
    public void alterarNomeCategoria(int indice, String nome) {
        estoque.getCategoria(indice).setNome(nome);
    }

    /**
     * Diz se uma categoria pode ser excluida.
     *
     * Esta e uma REGRA DE NEGOCIO: uma categoria que tem produtos nao pode
     * ser apagada. Por isso ela mora aqui, e nao na tela. Assim a regra vale
     * igual no terminal, no Swing ou em qualquer outra interface.
     *
     * @param indice Posicao da categoria (comecando em 0).
     * @return true se a categoria nao tem nenhum produto.
     */
    public boolean podeExcluirCategoria(int indice) {
        return contarProdutosDaCategoria(estoque.getCategoria(indice)) == 0;
    }

    /**
     * DELETE - Exclui uma categoria.
     * Chame antes o podeExcluirCategoria para saber se a exclusao e permitida.
     *
     * @param indice Posicao da categoria (comecando em 0).
     */
    public void excluirCategoria(int indice) {
        estoque.removerCategoria(indice);
    }

    /**
     * Verifica se ja e possivel cadastrar produtos.
     *
     * Outra regra de negocio: todo produto precisa de uma categoria, entao
     * nao adianta abrir o cadastro de produto sem nenhuma categoria criada.
     *
     * @return true se existe ao menos uma categoria cadastrada.
     */
    public boolean podeCadastrarProduto() {
        return estoque.getTotalCategorias() > 0;
    }

    // ------------------------------------------------------------------
    // Calculos usados nos relatorios
    // ------------------------------------------------------------------

    /**
     * Conta quantos produtos estao ligados a uma categoria.
     *
     * @param categoria A categoria a ser consultada.
     * @return A quantidade de produtos daquela categoria.
     */
    public int contarProdutosDaCategoria(Categoria categoria) {
        return produtosDaCategoria(categoria).size();
    }

    /**
     * Separa apenas os produtos de uma categoria.
     *
     * @param categoria A categoria desejada.
     * @return Uma nova lista somente com os produtos daquela categoria.
     */
    public List<Produto> produtosDaCategoria(Categoria categoria) {
        List<Produto> encontrados = new ArrayList<Produto>();
        for (Produto produto : estoque.getProdutos()) {
            if (produto.getCategoria() == categoria) {
                encontrados.add(produto);
            }
        }
        return encontrados;
    }

    /**
     * Diz se um produto esta com o estoque abaixo do minimo.
     *
     * @param produto O produto a ser verificado.
     * @return true se a quantidade esta abaixo de ESTOQUE_MINIMO.
     */
    public boolean estoqueBaixo(Produto produto) {
        return produto.getQuantidade() < ESTOQUE_MINIMO;
    }

    /**
     * @return A soma das quantidades de todos os produtos.
     */
    public int contarTotalUnidades() {
        int total = 0;
        for (Produto produto : estoque.getProdutos()) {
            total += produto.getQuantidade();
        }
        return total;
    }

    /**
     * @return O valor total (preco x quantidade) de todo o estoque.
     */
    public double calcularValorTotal() {
        double total = 0;
        for (Produto produto : estoque.getProdutos()) {
            total += produto.getValorTotal();
        }
        return total;
    }

    /**
     * @return Quantos produtos estao com estoque baixo.
     */
    public int contarProdutosComEstoqueBaixo() {
        int total = 0;
        for (Produto produto : estoque.getProdutos()) {
            if (estoqueBaixo(produto)) {
                total++;
            }
        }
        return total;
    }

    /**
     * @param categoria A categoria desejada.
     * @return O valor total em estoque somente daquela categoria.
     */
    public double calcularValorDaCategoria(Categoria categoria) {
        double total = 0;
        for (Produto produto : produtosDaCategoria(categoria)) {
            total += produto.getValorTotal();
        }
        return total;
    }

    /**
     * @param categoria A categoria desejada.
     * @return A soma das quantidades dos produtos daquela categoria.
     */
    public int contarUnidadesDaCategoria(Categoria categoria) {
        int total = 0;
        for (Produto produto : produtosDaCategoria(categoria)) {
            total += produto.getQuantidade();
        }
        return total;
    }

    // ------------------------------------------------------------------
    // Dados de exemplo
    // ------------------------------------------------------------------

    /**
     * Cadastra algumas categorias e produtos para o sistema ja iniciar com dados.
     * Pode apagar este metodo (e a chamada no Main) para comecar com o estoque vazio.
     *
     * Quando o DAO for implementado (Aula 12 - JDBC e DAO), este metodo some:
     * os dados passam a vir do banco.
     */
    public void carregarDadosExemplo() {
        Categoria eletronicos = new Categoria("Eletronicos");
        Categoria perifericos = new Categoria("Perifericos");
        estoque.adicionarCategoria(eletronicos);
        estoque.adicionarCategoria(perifericos);

        estoque.adicionarProduto(new Produto("Notebook", 3500.00, 15, eletronicos));
        estoque.adicionarProduto(new Produto("Smartphone", 2000.00, 20, eletronicos));
        estoque.adicionarProduto(new Produto("Mouse", 80.00, 5, perifericos));
        estoque.adicionarProduto(new Produto("Teclado", 120.00, 8, perifericos));
    }
}
