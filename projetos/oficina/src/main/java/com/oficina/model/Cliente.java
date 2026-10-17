package com.oficina.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

@Data
@AllArgsConstructor
public class Cliente {
    private Integer id;
    private String nome;
    private String telefone;
    
    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "O CPF informado não é válido")
    private String cpf;
}
