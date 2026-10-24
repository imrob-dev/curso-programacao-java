package com.oficina.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Veículo vinculada a um Cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {

    private Integer id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private Cliente cliente;

    public Veiculo(String placa, String marca, String modelo, Integer ano, Cliente cliente) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        String dono = (cliente != null && cliente.getNome() != null) ? cliente.getNome() : "";
        return placa + " – " + modelo + " (" + dono + ")";
    }
}
