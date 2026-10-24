package com.oficina.dao;

import com.oficina.config.Banco;
import com.oficina.model.Cliente;
import com.oficina.model.OrdemServico;
import com.oficina.model.Perfil;
import com.oficina.model.StatusOS;
import com.oficina.model.Usuario;
import com.oficina.model.Veiculo;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Operações de banco de dados para a entidade OrdemServico com JOINs em Veiculo, Cliente e Usuario.
 */
public class OrdemServicoDAO {

    public List<OrdemServico> listar(StatusOS filtroStatus, String filtroPlaca) throws SQLException {
        List<OrdemServico> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT os.id, os.data_abertura, os.data_conclusao, "
                + "os.descricao_problema, os.servico_realizado, os.valor_mao_obra, os.status, "
                + "v.id AS id_veiculo, v.placa, v.marca, v.modelo, v.ano, "
                + "c.id AS id_cliente, c.nome AS nome_cliente, c.telefone, c.cpf, "
                + "u.id AS id_usuario, u.nome AS nome_usuario, u.login, u.perfil, u.ativo "
                + "FROM ordem_servico os "
                + "JOIN veiculo v ON v.id = os.id_veiculo "
                + "JOIN cliente c ON c.id = v.id_cliente "
                + "JOIN usuario u ON u.id = os.id_usuario "
                + "WHERE 1=1 ");

        if (filtroStatus != null) {
            sql.append("AND os.status = ? ");
        }
        if (filtroPlaca != null && !filtroPlaca.trim().isEmpty()) {
            sql.append("AND UPPER(v.placa) LIKE UPPER(?) ");
        }

        sql.append("ORDER BY os.id DESC");

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (filtroStatus != null) {
                ps.setString(paramIndex++, filtroStatus.name());
            }
            if (filtroPlaca != null && !filtroPlaca.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + filtroPlaca.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(converter(rs));
                }
            }
        }
        return lista;
    }

    public OrdemServico buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT os.id, os.data_abertura, os.data_conclusao, "
                + "os.descricao_problema, os.servico_realizado, os.valor_mao_obra, os.status, "
                + "v.id AS id_veiculo, v.placa, v.marca, v.modelo, v.ano, "
                + "c.id AS id_cliente, c.nome AS nome_cliente, c.telefone, c.cpf, "
                + "u.id AS id_usuario, u.nome AS nome_usuario, u.login, u.perfil, u.ativo "
                + "FROM ordem_servico os "
                + "JOIN veiculo v ON v.id = os.id_veiculo "
                + "JOIN cliente c ON c.id = v.id_cliente "
                + "JOIN usuario u ON u.id = os.id_usuario "
                + "WHERE os.id = ?";

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

    public void inserir(OrdemServico os) throws SQLException {
        String sql = "INSERT INTO ordem_servico (id_veiculo, id_usuario, data_abertura, data_conclusao, "
                + "descricao_problema, servico_realizado, valor_mao_obra, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, os.getVeiculo().getId());
            ps.setInt(2, os.getUsuario().getId());
            ps.setDate(3, Date.valueOf(os.getDataAbertura() != null ? os.getDataAbertura() : LocalDate.now()));
            if (os.getDataConclusao() != null) {
                ps.setDate(4, Date.valueOf(os.getDataConclusao()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }
            ps.setString(5, os.getDescricaoProblema());
            ps.setString(6, os.getServicoRealizado());
            ps.setBigDecimal(7, os.getValorMaoObra() != null ? os.getValorMaoObra() : BigDecimal.ZERO);
            ps.setString(8, os.getStatus().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    os.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(OrdemServico os) throws SQLException {
        String sql = "UPDATE ordem_servico SET id_veiculo = ?, data_conclusao = ?, "
                + "descricao_problema = ?, servico_realizado = ?, valor_mao_obra = ?, status = ? "
                + "WHERE id = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, os.getVeiculo().getId());
            if (os.getDataConclusao() != null) {
                ps.setDate(2, Date.valueOf(os.getDataConclusao()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, os.getDescricaoProblema());
            ps.setString(4, os.getServicoRealizado());
            ps.setBigDecimal(5, os.getValorMaoObra() != null ? os.getValorMaoObra() : BigDecimal.ZERO);
            ps.setString(6, os.getStatus().name());
            ps.setInt(7, os.getId());
            ps.executeUpdate();
        }
    }

    private OrdemServico converter(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Date dtAbertura = rs.getDate("data_abertura");
        Date dtConclusao = rs.getDate("data_conclusao");
        String problema = rs.getString("descricao_problema");
        String servico = rs.getString("servico_realizado");
        BigDecimal valor = rs.getBigDecimal("valor_mao_obra");
        String statusStr = rs.getString("status");
        StatusOS status = StatusOS.valueOf(statusStr);

        Cliente cliente = new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nome_cliente"),
                rs.getString("telefone"),
                rs.getString("cpf")
        );

        int anoVal = rs.getInt("ano");
        Integer ano = rs.wasNull() ? null : anoVal;

        Veiculo veiculo = new Veiculo(
                rs.getInt("id_veiculo"),
                rs.getString("placa"),
                rs.getString("marca"),
                rs.getString("modelo"),
                ano,
                cliente
        );

        Perfil perfil = "ADMIN".equalsIgnoreCase(rs.getString("perfil")) ? Perfil.ADMIN : Perfil.ATENDENTE;
        Usuario usuario = new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nome_usuario"),
                rs.getString("login"),
                null,
                perfil,
                rs.getBoolean("ativo")
        );

        LocalDate localAbertura = dtAbertura != null ? dtAbertura.toLocalDate() : null;
        LocalDate localConclusao = dtConclusao != null ? dtConclusao.toLocalDate() : null;

        return new OrdemServico(id, veiculo, usuario, localAbertura, localConclusao, problema, servico, valor, status);
    }
}
