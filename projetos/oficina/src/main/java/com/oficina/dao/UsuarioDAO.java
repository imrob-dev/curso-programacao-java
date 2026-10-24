package com.oficina.dao;

import com.oficina.config.Banco;
import com.oficina.model.Perfil;
import com.oficina.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Operações de banco de dados para a entidade Usuario.
 */
public class UsuarioDAO {

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nome, login, senha_hash, perfil, ativo FROM usuario ORDER BY id";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(converter(rs));
            }
        }
        return lista;
    }

    public Usuario buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT id, nome, login, senha_hash, perfil, ativo FROM usuario WHERE id = ?";
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

    public Usuario buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT id, nome, login, senha_hash, perfil, ativo FROM usuario WHERE LOWER(login) = LOWER(?)";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return converter(rs);
                }
            }
        }
        return null;
    }

    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nome, login, senha_hash, perfil, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getLogin());
            ps.setString(3, usuario.getSenhaHash());
            ps.setString(4, usuario.getPerfil().name());
            ps.setBoolean(5, usuario.isAtivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Usuario usuario) throws SQLException {
        boolean atualizaSenha = usuario.getSenhaHash() != null && !usuario.getSenhaHash().trim().isEmpty();
        String sql = atualizaSenha
                ? "UPDATE usuario SET nome = ?, login = ?, senha_hash = ?, perfil = ?, ativo = ? WHERE id = ?"
                : "UPDATE usuario SET nome = ?, login = ?, perfil = ?, ativo = ? WHERE id = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getLogin());

            if (atualizaSenha) {
                ps.setString(3, usuario.getSenhaHash());
                ps.setString(4, usuario.getPerfil().name());
                ps.setBoolean(5, usuario.isAtivo());
                ps.setInt(6, usuario.getId());
            } else {
                ps.setString(3, usuario.getPerfil().name());
                ps.setBoolean(4, usuario.isAtivo());
                ps.setInt(5, usuario.getId());
            }
            ps.executeUpdate();
        }
    }

    public void excluir(Integer id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existeOutroComLogin(String login, Integer idAtual) throws SQLException {
        String sql = (idAtual == null)
                ? "SELECT 1 FROM usuario WHERE LOWER(login) = LOWER(?)"
                : "SELECT 1 FROM usuario WHERE LOWER(login) = LOWER(?) AND id <> ?";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            if (idAtual != null) {
                ps.setInt(2, idAtual);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int contarAdminsAtivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE perfil = 'ADMIN' AND ativo = true";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Usuario converter(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String login = rs.getString("login");
        String senhaHash = rs.getString("senha_hash");
        String perfilStr = rs.getString("perfil");
        Perfil perfil = "ADMIN".equalsIgnoreCase(perfilStr) ? Perfil.ADMIN : Perfil.ATENDENTE;
        boolean ativo = rs.getBoolean("ativo");

        return new Usuario(id, nome, login, senhaHash, perfil, ativo);
    }
}
