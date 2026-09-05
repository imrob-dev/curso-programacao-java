package gerenciacontato.dao;

import gerenciacontato.config.Banco;
import gerenciacontato.model.Contato;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ContatoDAO {

    private Connection con;

    public ContatoDAO() {
        this.con = Banco.conectar();
    }

    public void salvar(Contato contato) {
        String sql = "INSERT INTO contato (nome, telefone, email) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, contato.getNome());
            ps.setString(2, contato.getTelefone());
            ps.setString(3, contato.getEmail());

            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Erro ao salvar contato: " + ex.getMessage());
        }
    }

    public List<Contato> listar() {
        List<Contato> contatos = new ArrayList<>();
        String sql = "SELECT c.id, c.nome, c.telefone, c.email from contato c;";

        try (Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                
                // Operador Ternário
                String telefone = rs.getString("telefone") == null ? "0" : rs.getString("telefone");
                
//                if (rs.getString("telefone") == null) {
//                    telefone = "0";
//                } else {
//                    telefone = rs.getString("telefone");
//                }
                
                String email = rs.getString("email");
                
                Contato contato = new Contato(id, nome, telefone, email);
                
                contatos.add(contato);
            }
        } catch (SQLException ex) {
            System.err.println("Erro ao listar contatos: " + ex.getMessage());
        }
        
        return contatos;
    }
    
    public boolean atualizar(Contato contato) {
        boolean temNome = contato.getNome() != null && !contato.getNome().trim().isEmpty();
        String sql;

        if (temNome) {
            sql = "UPDATE contato SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        } else {
            sql = "UPDATE contato SET telefone = ?, email = ? WHERE id = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            if (temNome) {
                ps.setString(idx++, contato.getNome());
            }
            ps.setString(idx++, contato.getTelefone());
            ps.setString(idx++, contato.getEmail());
            ps.setInt(idx, contato.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Erro ao atualizar contato: " + ex.getMessage());
        }

        return false;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM contato WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Erro ao excluir contato: " + ex.getMessage());
        }

        return false;
    }

    public Contato buscarPorId(int id) {
        String sql = "SELECT id, nome, telefone, email FROM contato WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String telefone = rs.getString("telefone") == null ? "0" : rs.getString("telefone");
                    String email = rs.getString("email");
                    return new Contato(id, nome, telefone, email);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Erro ao buscar contato por ID: " + ex.getMessage());
        }

        return null;
    }
}
