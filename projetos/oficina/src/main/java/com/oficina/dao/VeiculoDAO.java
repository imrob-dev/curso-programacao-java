package com.oficina.dao;

import com.oficina.config.Banco;
import com.oficina.model.Cliente;
import com.oficina.model.Veiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Operações de banco de dados para a entidade Veículo com JOIN em Cliente.
 */
public class VeiculoDAO {

    public List<Veiculo> listar() throws SQLException {
        List<Veiculo> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.placa, v.marca, v.modelo, v.ano, "
                + "c.id AS id_cliente, c.nome AS nome_cliente, c.telefone, c.cpf "
                + "FROM veiculo v "
                + "JOIN cliente c ON c.id = v.id_cliente "
                + "ORDER BY v.placa ASC";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(converter(rs));
            }
        }
        return lista;
    }

    public Veiculo buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT v.id, v.placa, v.marca, v.modelo, v.ano, "
                + "c.id AS id_cliente, c.nome AS nome_cliente, c.telefone, c.cpf "
                + "FROM veiculo v "
                + "JOIN cliente c ON c.id = v.id_cliente "
                + "WHERE v.id = ?";

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

    public Veiculo buscarPorPlaca(String placa) throws SQLException {
        String sql = "SELECT v.id, v.placa, v.marca, v.modelo, v.ano, "
                + "c.id AS id_cliente, c.nome AS nome_cliente, c.telefone, c.cpf "
                + "FROM veiculo v "
                + "JOIN cliente c ON c.id = v.id_cliente "
                + "WHERE UPPER(v.placa) = UPPER(?)";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return converter(rs);
                }
            }
        }
        return null;
    }

    public void inserir(Veiculo veiculo) throws SQLException {
        String sql = "INSERT INTO veiculo (placa, marca, modelo, ano, id_cliente) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, veiculo.getPlaca().toUpperCase().trim());
            ps.setString(2, veiculo.getMarca());
            ps.setString(3, veiculo.getModelo());
            if (veiculo.getAno() != null) {
                ps.setInt(4, veiculo.getAno());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setInt(5, veiculo.getCliente().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    veiculo.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Veiculo veiculo) throws SQLException {
        String sql = "UPDATE veiculo SET placa = ?, marca = ?, modelo = ?, ano = ?, id_cliente = ? WHERE id = ?";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, veiculo.getPlaca().toUpperCase().trim());
            ps.setString(2, veiculo.getMarca());
            ps.setString(3, veiculo.getModelo());
            if (veiculo.getAno() != null) {
                ps.setInt(4, veiculo.getAno());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setInt(5, veiculo.getCliente().getId());
            ps.setInt(6, veiculo.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(Integer id) throws SQLException {
        String sql = "DELETE FROM veiculo WHERE id = ?";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean temOrdensServico(Integer idVeiculo) throws SQLException {
        String sql = "SELECT 1 FROM ordem_servico WHERE id_veiculo = ? LIMIT 1";
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVeiculo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeOutroComPlaca(String placa, Integer idAtual) throws SQLException {
        String sql = (idAtual == null)
                ? "SELECT 1 FROM veiculo WHERE UPPER(placa) = UPPER(?)"
                : "SELECT 1 FROM veiculo WHERE UPPER(placa) = UPPER(?) AND id <> ?";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa.trim());
            if (idAtual != null) {
                ps.setInt(2, idAtual);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Veiculo converter(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String placa = rs.getString("placa");
        String marca = rs.getString("marca");
        String modelo = rs.getString("modelo");
        int anoVal = rs.getInt("ano");
        Integer ano = rs.wasNull() ? null : anoVal;

        Cliente cliente = new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nome_cliente"),
                rs.getString("telefone"),
                rs.getString("cpf")
        );

        return new Veiculo(id, placa, marca, modelo, ano, cliente);
    }
}
