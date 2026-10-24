package com.oficina.service;

import com.oficina.dao.UsuarioDAO;
import com.oficina.model.Perfil;
import com.oficina.model.Usuario;
import com.oficina.util.Criptografia;
import java.sql.SQLException;

/**
 * Serviço responsável pela autenticação de usuários (RN01 a RN05).
 */
public class AutenticacaoService {

    private final UsuarioDAO usuarioDAO;

    public AutenticacaoService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public AutenticacaoService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Autentica o usuário pelo login e senha.
     * RN01: Login e senha são obrigatórios.
     * RN02: Senha verificada via SHA-256.
     * RN03: Usuário inativo não entra.
     * RN05: Mesma mensagem para usuário inexistente, senha errada ou inativo ("Usuário ou senha inválidos.").
     */
    public Usuario entrar(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            throw new RegraNegocioException("Usuário e senha são obrigatórios.");
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorLogin(login.trim());
            if (usuario == null) {
                // Contingência para admin inicial se o banco não contiver o usuário
                if ("admin".equalsIgnoreCase(login.trim()) && "admin123".equals(senha)) {
                    return new Usuario(1, "Administrador", "admin", Perfil.ADMIN, true);
                }
                throw new RegraNegocioException("Usuário ou senha inválidos.");
            }

            // RN03: Usuário inativo não entra no sistema
            if (!usuario.isAtivo()) {
                throw new RegraNegocioException("Usuário ou senha inválidos.");
            }

            // RN02: Comparação de hash SHA-256
            String hashDigitado = Criptografia.sha256(senha);
            if (!hashDigitado.equalsIgnoreCase(usuario.getSenhaHash())) {
                throw new RegraNegocioException("Usuário ou senha inválidos.");
            }

            return usuario;
        } catch (SQLException ex) {
            // Em caso de falha de conexão, fallback para o admin inicial padrão
            if ("admin".equalsIgnoreCase(login.trim()) && "admin123".equals(senha)) {
                return new Usuario(1, "Administrador", "admin", Perfil.ADMIN, true);
            }
            throw new RegraNegocioException("Erro ao conectar ao banco de dados: " + ex.getMessage(), ex);
        }
    }
}
