package com.oficina.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa uma Ordem de Serviço (OS) na oficina mecânica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    private Integer id;
    private Veiculo veiculo;
    private Usuario usuario;
    private LocalDate dataAbertura = LocalDate.now();
    private LocalDate dataConclusao;
    private String descricaoProblema;
    private String servicoRealizado;
    private BigDecimal valorMaoObra = BigDecimal.ZERO;
    private StatusOS status = StatusOS.ABERTA;

    public OrdemServico(Veiculo veiculo, Usuario usuario, String descricaoProblema) {
        this.veiculo = veiculo;
        this.usuario = usuario;
        this.descricaoProblema = descricaoProblema;
        this.dataAbertura = LocalDate.now();
        this.valorMaoObra = BigDecimal.ZERO;
        this.status = StatusOS.ABERTA;
    }
}
