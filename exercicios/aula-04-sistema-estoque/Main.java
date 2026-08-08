import gui.MenuConsole;
import service.EstoqueService;

/**
 * Sistema de Estoque com CRUD (Exercicio 4 - Aula 4 / JAVA-104).
 *
 * Esta classe e apenas o PONTO DE ENTRADA do programa: ela monta as pecas
 * do sistema e manda a tela iniciar. O codigo esta separado em camadas,
 * cada uma em uma pasta (que em Java se chama pacote):
 *
 *   model/   -> os dados          : Produto, Categoria, Estoque
 *   gui/     -> a tela            : Console, Menu, TabelaPrinter, MenuConsole
 *   service/ -> as regras         : EstoqueService
 *   dao/     -> a persistencia    : (ainda vazio - ver dao/README.md)
 *
 * Quem conversa com quem:
 *
 *   Main  ->  GUI  ->  Service  ->  Model
 *
 * A GUI nunca decide nada sozinha e o Service nunca mostra nada na tela.
 */
public class Main {

    public static void main(String[] args) {
        EstoqueService servico = new EstoqueService();

        // Deixa o sistema comecar com alguns dados prontos.
        // Apague esta linha para iniciar com o estoque vazio.
        servico.carregarDadosExemplo();

        MenuConsole tela = new MenuConsole(servico);
        tela.iniciar();
    }
}
