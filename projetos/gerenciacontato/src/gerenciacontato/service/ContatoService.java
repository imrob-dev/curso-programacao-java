package gerenciacontato.service;

import gerenciacontato.dao.ContatoDAO;
import gerenciacontato.model.Contato;
import java.util.List;

public class ContatoService {
    private ContatoDAO dao;

    public ContatoService() {
        this.dao = new ContatoDAO();
    }
    
    public void cadastrarContato(String nome, String telefone, String email) {
        if (nome == null || nome.trim().isEmpty()) {
            System.out.println("Aviso: O nome nao pode ser vazio.");
            return;
        }
        
        if (telefone == null || telefone.trim().isEmpty()) {
            System.err.println("Aviso: O telefone não pode ser vazio.");
        }
        
        if (email == null || !email.contains("@")) {
            System.out.println("Aviso: E-mail inválido");
        }
        
        Contato novoContato = new Contato(nome, telefone, email);
        dao.salvar(novoContato);
        System.out.println("Contato cadastrado com sucesso!\n");
    }
    
    public List<Contato> listarContatos() {
        return dao.listar();
    }
    
    public boolean atualizarContato(int id, String novoTelefone, String novoEmail) {
        return atualizarContato(id, null, novoTelefone, novoEmail);
    }

    public boolean atualizarContato(int id, String novoNome, String novoTelefone, String novoEmail) {
        if (id <= 0) {
            System.err.println("Aviso: ID Inválido");
            return false;
        }
        
        if (novoTelefone == null || novoTelefone.trim().isEmpty()) {
            System.err.println("Aviso: O telefone não pode ser vazio.");
        }
        
        if (novoEmail == null || !novoEmail.contains("@")) {
            System.out.println("Aviso: E-mail inválido");
        }
        
        Contato contatoAtualizado;
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            contatoAtualizado = new Contato(id, novoNome, novoTelefone, novoEmail);
        } else {
            contatoAtualizado = new Contato(id, novoTelefone, novoEmail);
        }
        
        return dao.atualizar(contatoAtualizado);
    }

    public boolean excluirContato(int id) {
        if (id <= 0) {
            System.err.println("Aviso: ID Inválido");
            return false;
        }

        return dao.excluir(id);
    }

    public Contato buscarPorId(int id) {
        if (id <= 0) {
            System.err.println("Aviso: ID Inválido");
            return null;
        }

        return dao.buscarPorId(id);
    }
}
