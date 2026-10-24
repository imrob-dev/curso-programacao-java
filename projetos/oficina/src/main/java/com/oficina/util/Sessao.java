package com.oficina.util;

import com.oficina.model.Perfil;
import com.oficina.model.Usuario;

/**
 * Controla o usuário atualmente autenticado na sessão da aplicação.
 */
public final class Sessao {

    private static Usuario usuarioLogado;

    private Sessao() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static Usuario getUsuario() {
        return usuarioLogado;
    }

    public static boolean isAdmin() {
        return usuarioLogado != null && usuarioLogado.getPerfil() == Perfil.ADMIN;
    }

    public static void encerrar() {
        usuarioLogado = null;
    }
}
