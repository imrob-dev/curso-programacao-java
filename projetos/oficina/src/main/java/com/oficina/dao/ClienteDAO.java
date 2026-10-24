package com.oficina.dao;

import com.oficina.config.Banco;
import com.oficina.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Operações de banco de dados para a entidade Cliente.
 */
public class ClienteDAO {

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nome, telefone, cpf FROM cliente ORDER BY nome ASC";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(converter(rs));
            }
        }
        return lista;
    }

    public List<Cliente> buscarPorNome(String termo) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nome, telefone, cpf FROM cliente WHERE LOWER(nome) LIKE LOWER(?) ORDER BY nome ASC";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(converter(rs));
                }
            }
        }
        return lista;
    }

    public Cliente buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT id, nome, telefone, cpf FROM cliente WHERE id = ?";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return converter(rs);
                }
            }
        }
        return null;
    }

    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone, cpf) VALUES (?, ?, ?)";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getTelefone());
            ps.setString(3, (cliente.getCpf() != null && !cliente.getCpf().trim().isEmpty()) ? cliente.getCpf().trim() : null);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, cpf = ? WHERE id = ?";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getTelefone());
            ps.setString(3, (cliente.getCpf() != null && !cliente.getCpf().trim().isEmpty()) ? cliente.getCpf().trim() : null);
            ps.setInt(4, cliente.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(Integer id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean temVeiculos(Integer idCliente) throws SQLException {
        String sql = "SELECT 1 FROM veiculo WHERE id_cliente = ? LIMIT 1";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeOutroComCpf(String cpf, Integer idAtual) throws SQLException {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        String sql = (idAtual == null)
                ? "SELECT 1 FROM cliente WHERE cpf = ?"
                : "SELECT 1 FROM cliente WHERE cpf = ? AND id <> ?";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf.trim());
            if (idAtual != null) {
                ps.setInt(2, idAtual);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Cliente converter(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String telefone = rs.getString("telefone");
        String cpf = rs.getString("cpf");
        return new Cliente(id, nome, telefone, cpf);
    }
}
