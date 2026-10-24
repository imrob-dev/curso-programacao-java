package com.oficina.util;

import java.math.BigDecimal;
import javax.swing.JTextField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MascaraMonetariaTest {

    @Test
    public void deveIniciarComZeroVirgulaZeroZero() {
        JTextField txt = new JTextField();
        MascaraMonetaria.aplicar(txt);

        Assertions.assertEquals("0,00", txt.getText());
        Assertions.assertEquals(new BigDecimal("0.00"), MascaraMonetaria.getValor(txt));
    }

    @Test
    public void deveFormatarConformeDigitosInseridos() throws Exception {
        JTextField txt = new JTextField();
        MascaraMonetaria.aplicar(txt);

        // Digita 2 -> 0,02
        txt.getDocument().insertString(txt.getText().length(), "2", null);
        Assertions.assertEquals("0,02", txt.getText());
        Assertions.assertEquals(new BigDecimal("0.02"), MascaraMonetaria.getValor(txt));

        // Digita 5 -> 0,25
        txt.getDocument().insertString(txt.getText().length(), "5", null);
        Assertions.assertEquals("0,25", txt.getText());
        Assertions.assertEquals(new BigDecimal("0.25"), MascaraMonetaria.getValor(txt));

        // Digita 0 -> 2,50
        txt.getDocument().insertString(txt.getText().length(), "0", null);
        Assertions.assertEquals("2,50", txt.getText());
        Assertions.assertEquals(new BigDecimal("2.50"), MascaraMonetaria.getValor(txt));

        // Digita 0 -> 25,00
        txt.getDocument().insertString(txt.getText().length(), "0", null);
        Assertions.assertEquals("25,00", txt.getText());
        Assertions.assertEquals(new BigDecimal("25.00"), MascaraMonetaria.getValor(txt));

        // Digita 0 -> 250,00
        txt.getDocument().insertString(txt.getText().length(), "0", null);
        Assertions.assertEquals("250,00", txt.getText());
        Assertions.assertEquals(new BigDecimal("250.00"), MascaraMonetaria.getValor(txt));

        // Digita 0 -> 2.500,00
        txt.getDocument().insertString(txt.getText().length(), "0", null);
        Assertions.assertEquals("2.500,00", txt.getText());
        Assertions.assertEquals(new BigDecimal("2500.00"), MascaraMonetaria.getValor(txt));
    }

    @Test
    public void deveRemoverDigitosAoApagar() throws Exception {
        JTextField txt = new JTextField();
        MascaraMonetaria.aplicar(txt);
        MascaraMonetaria.setValor(txt, new BigDecimal("250.00"));
        Assertions.assertEquals("250,00", txt.getText());

        // Apaga o último caractere
        txt.getDocument().remove(txt.getText().length() - 1, 1);
        Assertions.assertEquals("25,00", txt.getText());
        Assertions.assertEquals(new BigDecimal("25.00"), MascaraMonetaria.getValor(txt));

        // Apaga novamente
        txt.getDocument().remove(txt.getText().length() - 1, 1);
        Assertions.assertEquals("2,50", txt.getText());

        // Apaga novamente
        txt.getDocument().remove(txt.getText().length() - 1, 1);
        Assertions.assertEquals("0,25", txt.getText());

        // Apaga novamente
        txt.getDocument().remove(txt.getText().length() - 1, 1);
        Assertions.assertEquals("0,02", txt.getText());

        // Apaga novamente -> volta para 0,00
        txt.getDocument().remove(txt.getText().length() - 1, 1);
        Assertions.assertEquals("0,00", txt.getText());
        Assertions.assertEquals(new BigDecimal("0.00"), MascaraMonetaria.getValor(txt));
    }

    @Test
    public void deveDefinirEObterValorCorretamente() {
        JTextField txt = new JTextField();
        MascaraMonetaria.aplicar(txt);

        MascaraMonetaria.setValor(txt, new BigDecimal("12345.67"));
        Assertions.assertEquals("12.345,67", txt.getText());
        Assertions.assertEquals(new BigDecimal("12345.67"), MascaraMonetaria.getValor(txt));

        MascaraMonetaria.setValor(txt, BigDecimal.ZERO);
        Assertions.assertEquals("0,00", txt.getText());
        Assertions.assertEquals(new BigDecimal("0.00"), MascaraMonetaria.getValor(txt));
    }
}
