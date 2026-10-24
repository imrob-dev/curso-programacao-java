package com.oficina.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um Usuário do sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Integer id;
    private String nome;
    private String login;
    private String senhaHash;
    private Perfil perfil;
    private boolean ativo = true;

    public Usuario(Integer id, String nome, String login, Perfil perfil, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.perfil = perfil;
        this.ativo = ativo;
    }
}
