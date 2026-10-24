package com.oficina.service;

import com.oficina.dao.UsuarioDAO;
import com.oficina.model.Perfil;
import com.oficina.model.Usuario;
import com.oficina.util.Criptografia;
import com.oficina.util.Sessao;
import java.sql.SQLException;
import java.util.List;

/**
 * Regras de negócio para gerenciamento de Usuários (RN06 e RN07).
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    private void validarAcessoAdmin() {
        // RN06: Só usuários com perfil ADMIN acessam o cadastro de usuários
        if (!Sessao.isAdmin()) {
            throw new RegraNegocioException("Acesso negado: apenas administradores podem gerenciar usuários.");
        }
    }

    public List<Usuario> listar() {
        validarAcessoAdmin();
        try {
            return usuarioDAO.listar();
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao listar usuários: " + ex.getMessage(), ex);
        }
    }

    public void salvar(Usuario usuario, String senha, String confirmacaoSenha) {
        validarAcessoAdmin();

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("O nome do usuário é obrigatório.");
        }
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new RegraNegocioException("O login é obrigatório.");
        }
        if (usuario.getPerfil() == null) {
            usuario.setPerfil(Perfil.ATENDENTE);
        }

        try {
            // Valida login único
            if (usuarioDAO.existeOutroComLogin(usuario.getLogin().trim(), usuario.getId())) {
                throw new RegraNegocioException("Já existe outro usuário cadastrado com o login informando.");
            }

            // RN07: O usuário não pode desativar a si mesmo
            Usuario logado = Sessao.getUsuario();
            if (logado != null && logado.getId() != null && logado.getId().equals(usuario.getId())) {
                if (!usuario.isAtivo()) {
                    throw new RegraNegocioException("Não é permitido desativar a sua própria conta.");
                }
            }

            boolean isNovo = (usuario.getId() == null || usuario.getId() == 0);

            if (isNovo) {
                // RN06: Novo usuário exige senha de no mínimo 6 caracteres confirmada
                if (senha == null || senha.length() < 6) {
                    throw new RegraNegocioException("A senha deve conter no mínimo 6 caracteres.");
                }
                if (!senha.equals(confirmacaoSenha)) {
                    throw new RegraNegocioException("A confirmação da senha não confere.");
                }
                usuario.setSenhaHash(Criptografia.sha256(senha));
                usuarioDAO.inserir(usuario);
            } else {
                // Na edição: em branco = mantém a senha atual
                if (senha != null && !senha.isEmpty()) {
                    if (senha.length() < 6) {
                        throw new RegraNegocioException("A nova senha deve conter no mínimo 6 caracteres.");
                    }
                    if (!senha.equals(confirmacaoSenha)) {
                        throw new RegraNegocioException("A confirmação da nova senha não confere.");
                    }
                    usuario.setSenhaHash(Criptografia.sha256(senha));
                } else {
                    usuario.setSenhaHash(null); // indica que mantém a atual
                }
                usuarioDAO.atualizar(usuario);
            }
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao salvar usuário no banco de dados: " + ex.getMessage(), ex);
        }
    }

    public void excluir(Integer id) {
        validarAcessoAdmin();

        if (id == null) {
            throw new RegraNegocioException("ID do usuário inválido.");
        }

        // RN07: O usuário não pode excluir a si mesmo
        Usuario logado = Sessao.getUsuario();
        if (logado != null && id.equals(logado.getId())) {
            throw new RegraNegocioException("Não é permitido excluir o usuário atualmente logado.");
        }

        try {
            usuarioDAO.excluir(id);
        } catch (SQLException ex) {
            throw new RegraNegocioException("Erro ao excluir usuário: " + ex.getMessage(), ex);
        }
    }
}
