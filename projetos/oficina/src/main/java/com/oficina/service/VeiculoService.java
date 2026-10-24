package com.oficina.service;

import com.oficina.dao.VeiculoDAO;
import com.oficina.model.Veiculo;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Regras de negócio para gerenciamento de Veículos (RN09 e RN10).
 */
public class VeiculoService {

    private static final Pattern PADRAO_PLACA = Pattern.compile("^[A-Z]{3}-?[0-9]{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$");

    private final VeiculoDAO veiculoDAO;

    public VeiculoService() {
        this.veiculoDAO = new VeiculoDAO();
    }

    public VeiculoService(VeiculoDAO veiculoDAO) {
        this.veiculoDAO = veiculoDAO;
    }

    public List<Veiculo> listar() {
        try {
            return veiculoDAO.listar();
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao listar veículos: " + ex.getMessage(), ex);
        }
    }

    public void salvar(Veiculo veiculo) {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            throw new RegraNegocioException("A placa do veículo é obrigatória.");
        }
        if (veiculo.getMarca() == null || veiculo.getMarca().trim().isEmpty()) {
            throw new RegraNegocioException("A marca é obrigatória.");
        }
        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            throw new RegraNegocioException("O modelo é obrigatório.");
        }
        if (veiculo.getCliente() == null || veiculo.getCliente().getId() == null) {
            throw new RegraNegocioException("O dono (cliente) do veículo é obrigatório.");
        }

        // RN09: Formato antigo (ABC-1234) ou Mercosul (ABC1D23)
        String placaFormatada = veiculo.getPlaca().trim().toUpperCase();
        if (!PADRAO_PLACA.matcher(placaFormatada).matches()) {
            throw new RegraNegocioException("Placa inválida! Use o formato tradicional (ABC-1234) ou Mercosul (ABC1D23).");
        }
        veiculo.setPlaca(placaFormatada);

        try {
            // RN09: A placa é única
            if (veiculoDAO.existeOutroComPlaca(veiculo.getPlaca(), veiculo.getId())) {
                throw new RegraNegocioException("Já existe outro veículo cadastrado com a placa informada.");
            }

            if (veiculo.getId() == null || veiculo.getId() == 0) {
                veiculoDAO.inserir(veiculo);
            } else {
                veiculoDAO.atualizar(veiculo);
            }
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao salvar veículo no banco: " + ex.getMessage(), ex);
        }
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new RegraNegocioException("Veículo inválido.");
        }

        try {
            // RN10: Não é possível excluir um veículo que tenha OS
            if (veiculoDAO.temOrdensServico(id)) {
                throw new RegraNegocioException("Não é possível excluir o veículo pois existem Ordens de Serviço vinculadas a ele.");
            }
            veiculoDAO.excluir(id);
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao excluir veículo: " + ex.getMessage(), ex);
        }
    }
}
