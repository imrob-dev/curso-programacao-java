package com.oficina.util;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Máscara monetária em tempo real para campos JTextField no formato brasileiro
 * (ex: 0,00 / 250,00 / 1.500,00) enquanto o usuário digita.
 */
public class MascaraMonetaria extends DocumentFilter {

    private static final DecimalFormat FORMATADOR;
    private static final int MAX_DIGITOS = 12; // Suporta valores até R$ 9.999.999.999,99

    static {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        FORMATADOR = new DecimalFormat("#,##0.00", simbolos);
    }

    private final JTextField textField;
    private boolean formatando = false;

    public MascaraMonetaria(JTextField textField) {
        this.textField = textField;
    }

    /**
     * Aplica a máscara monetária ao campo JTextField informado.
     */
    public static void aplicar(JTextField textField) {
        if (textField == null) {
            return;
        }

        textField.setHorizontalAlignment(JTextField.RIGHT);

        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        MascaraMonetaria filtro = new MascaraMonetaria(textField);
        doc.setDocumentFilter(filtro);

        if (textField.getText() == null || textField.getText().trim().isEmpty()) {
            setValor(textField, BigDecimal.ZERO);
        }

        // Posiciona cursor no final ao ganhar foco ou clicar
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() -> textField.setCaretPosition(textField.getText().length()));
            }
        });

        // Garante que a tecla Delete no final ou seleção funcione naturalmente
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    textField.setCaretPosition(textField.getText().length());
                }
            }
        });
    }

    /**
     * Obtém o valor numérico digitado no campo como BigDecimal.
     */
    public static BigDecimal getValor(JTextField textField) {
        if (textField == null || textField.getText() == null) {
            return BigDecimal.ZERO;
        }
        String texto = textField.getText().replaceAll("[^0-9]", "");
        if (texto.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(texto).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Define o valor monetário do campo formatado no padrão pt-BR.
     */
    public static void setValor(JTextField textField, BigDecimal valor) {
        if (textField == null) {
            return;
        }
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
        textField.setText(FORMATADOR.format(valor));
        textField.setCaretPosition(textField.getText().length());
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (formatando) {
            super.insertString(fb, offset, string, attr);
            return;
        }
        processarEntrada(fb, string, false, 0);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (formatando) {
            super.replace(fb, offset, length, text, attrs);
            return;
        }
        processarEntrada(fb, text, false, length);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        if (formatando) {
            super.remove(fb, offset, length);
            return;
        }
        processarEntrada(fb, "", true, length);
    }

    private void processarEntrada(FilterBypass fb, String entrada, boolean isRemocao, int comprimentoSelecao) throws BadLocationException {
        formatando = true;
        try {
            String textoAtual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String apenasDigitos = textoAtual.replaceAll("[^0-9]", "");

            // Se o usuário selecionou todo o texto e apagou/digitou por cima, recomeça
            if (comprimentoSelecao >= fb.getDocument().getLength()) {
                apenasDigitos = "";
            }

            if (isRemocao) {
                if (apenasDigitos.length() > 0) {
                    apenasDigitos = apenasDigitos.substring(0, apenasDigitos.length() - 1);
                }
            } else if (entrada != null) {
                String digitosInseridos = entrada.replaceAll("[^0-9]", "");
                if (!digitosInseridos.isEmpty()) {
                    apenasDigitos += digitosInseridos;
                }
            }

            // Remove zeros iniciais à esquerda
            apenasDigitos = apenasDigitos.replaceFirst("^0+(?!$)", "");

            // Limita a quantidade de dígitos para não estourar o tipo
            if (apenasDigitos.length() > MAX_DIGITOS) {
                apenasDigitos = apenasDigitos.substring(0, MAX_DIGITOS);
            }

            long centavos = apenasDigitos.isEmpty() ? 0 : Long.parseLong(apenasDigitos);
            BigDecimal valor = BigDecimal.valueOf(centavos).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            String formatado = FORMATADOR.format(valor);

            fb.replace(0, fb.getDocument().getLength(), formatado, null);

            if (textField != null) {
                textField.setCaretPosition(formatado.length());
            }
        } finally {
            formatando = false;
        }
    }
}
