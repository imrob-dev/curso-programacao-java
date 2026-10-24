package com.oficina.service;

import com.oficina.dao.ClienteDAO;
import com.oficina.model.Cliente;
import java.sql.SQLException;
import java.util.List;

/**
 * Regras de negócio para gerenciamento de Clientes (RN08 e RN10).
 */
public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public List<Cliente> listar() {
        try {
            return clienteDAO.listar();
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao consultar clientes: " + ex.getMessage(), ex);
        }
    }

    public List<Cliente> buscarPorNome(String termo) {
        try {
            if (termo == null || termo.trim().isEmpty()) {
                return clienteDAO.listar();
            }
            return clienteDAO.buscarPorNome(termo.trim());
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao buscar clientes por nome: " + ex.getMessage(), ex);
        }
    }

    public void salvar(Cliente cliente) {
        // RN08: Nome e telefone do cliente são obrigatórios
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("O nome do cliente é obrigatório.");
        }
        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            throw new RegraNegocioException("O telefone do cliente é obrigatório.");
        }

        try {
            if (cliente.getCpf() != null && !cliente.getCpf().trim().isEmpty()) {
                if (clienteDAO.existeOutroComCpf(cliente.getCpf(), cliente.getId())) {
                    throw new RegraNegocioException("Já existe outro cliente cadastrado com o CPF informado.");
                }
            }

            if (cliente.getId() == null || cliente.getId() == 0) {
                clienteDAO.inserir(cliente);
            } else {
                clienteDAO.atualizar(cliente);
            }
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao salvar cliente no banco: " + ex.getMessage(), ex);
        }
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new RegraNegocioException("Cliente inválido.");
        }

        try {
            // RN10: Não é possível excluir um cliente que tenha veículos
            if (clienteDAO.temVeiculos(id)) {
                throw new RegraNegocioException("Não é possível excluir o cliente pois ele possui veículo(s) cadastrado(s).");
            }
            clienteDAO.excluir(id);
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao excluir cliente: " + ex.getMessage(), ex);
        }
    }
}
