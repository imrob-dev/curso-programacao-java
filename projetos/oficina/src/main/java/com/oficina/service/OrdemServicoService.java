package com.oficina.service;

import com.oficina.dao.OrdemServicoDAO;
import com.oficina.model.OrdemServico;
import com.oficina.model.StatusOS;
import com.oficina.model.Usuario;
import com.oficina.util.Sessao;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negócio para Ordens de Serviço (RN11 a RN14).
 */
public class OrdemServicoService {

    private final OrdemServicoDAO ordemServicoDAO;

    public OrdemServicoService() {
        this.ordemServicoDAO = new OrdemServicoDAO();
    }

    public OrdemServicoService(OrdemServicoDAO ordemServicoDAO) {
        this.ordemServicoDAO = ordemServicoDAO;
    }

    public List<OrdemServico> listar(StatusOS filtroStatus, String filtroPlaca) {
        try {
            return ordemServicoDAO.listar(filtroStatus, filtroPlaca);
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao listar ordens de serviço: " + ex.getMessage(), ex);
        }
    }

    public OrdemServico buscarPorId(Integer id) {
        try {
            return ordemServicoDAO.buscarPorId(id);
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao buscar ordem de serviço: " + ex.getMessage(), ex);
        }
    }

    public void salvar(OrdemServico os) {
        // RN11: A OS só pode ser aberta para um veículo cadastrado
        if (os.getVeiculo() == null || os.getVeiculo().getId() == null) {
            throw new RegraNegocioException("O veículo deve ser selecionado.");
        }
        if (os.getDescricaoProblema() == null || os.getDescricaoProblema().trim().isEmpty()) {
            throw new RegraNegocioException("A descrição do problema é obrigatória.");
        }

        // RN14: O valor da mão de obra não pode ser negativo
        if (os.getValorMaoObra() != null && os.getValorMaoObra().compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O valor da mão de obra não pode ser negativo.");
        }
        if (os.getValorMaoObra() == null) {
            os.setValorMaoObra(BigDecimal.ZERO);
        }

        boolean isNova = (os.getId() == null || os.getId() == 0);

        try {
            if (isNova) {
                // RN11: guarda o usuário logado em id_usuario
                Usuario logado = Sessao.getUsuario();
                if (logado == null) {
                    throw new RegraNegocioException("Nenhum usuário autenticado na sessão.");
                }
                os.setUsuario(logado);
                os.setDataAbertura(LocalDate.now());
                os.setStatus(StatusOS.ABERTA);
                ordemServicoDAO.inserir(os);
            } else {
                // Busca a OS existente no banco para verificar status anterior
                OrdemServico existente = ordemServicoDAO.buscarPorId(os.getId());
                if (existente == null) {
                    throw new RegraNegocioException("Ordem de Serviço não encontrada.");
                }

                // RN13: Uma OS CONCLUIDA ou CANCELADA não pode mais ser editada
                if (existente.getStatus() == StatusOS.CONCLUIDA || existente.getStatus() == StatusOS.CANCELADA) {
                    throw new RegraNegocioException("Uma Ordem de Serviço concluída ou cancelada não pode mais ser alterada.");
                }

                // RN12: Ao mudar para CONCLUIDA, exige serviço realizado, valor da mão de obra e preenche data_conclusao com hoje
                if (os.getStatus() == StatusOS.CONCLUIDA) {
                    if (os.getServicoRealizado() == null || os.getServicoRealizado().trim().isEmpty()) {
                        throw new RegraNegocioException("Para concluir a OS, o serviço realizado deve ser preenchido.");
                    }
                    if (os.getValorMaoObra() == null || os.getValorMaoObra().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new RegraNegocioException("Para concluir a OS, informe um valor de mão de obra válido.");
                    }
                    os.setDataConclusao(LocalDate.now());
                }

                ordemServicoDAO.atualizar(os);
            }
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao salvar ordem de serviço: " + ex.getMessage(), ex);
        }
    }
}
