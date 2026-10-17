package com.oficina;

import com.formdev.flatlaf.FlatLightLaf;
import com.oficina.gui.LoginGUI;
import com.oficina.gui.TelaPrincipalGUI;
import com.oficina.model.Cliente;
import javax.swing.UIManager;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Verifica e cria o banco e tabelas automaticamente na inicializacao caso nao existam
        com.oficina.config.Banco.inicializarBancoSeNecessario();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginGUI().setVisible(true);
            }
        });
    }
}
