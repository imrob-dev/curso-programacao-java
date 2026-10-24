/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oficina.gui;

import com.oficina.model.OrdemServico;
import com.oficina.model.StatusOS;
import com.oficina.service.OrdemServicoService;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Tela de listagem e gerenciamento de Ordens de Serviço (T06).
 *
 * @author Rob
 */
public class OrdemServicoGUI extends javax.swing.JInternalFrame {

    private final OrdemServicoService ordemServicoService;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<OrdemServico> listaOrdens = new ArrayList<>();

    /**
     * Creates new form OrdemServicoGUI
     */
    public OrdemServicoGUI() {
        this.ordemServicoService = new OrdemServicoService();
        initComponents();
        configurarRenderers();
        carregarTabela();
    }

    private void configurarRenderers() {
        // Alinhamento à direita para Nº e Valor
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        tblOrdens.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
        tblOrdens.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        // Alinhamento central para data de abertura
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        tblOrdens.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Destaque visual por status (Extra conforme especificação)
        tblOrdens.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !isSelected) {
                    String status = value.toString();
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                    switch (status) {
                        case "CONCLUIDA":
                            c.setForeground(new java.awt.Color(46, 125, 50)); // Verde
                            break;
                        case "EM_ANDAMENTO":
                            c.setForeground(new java.awt.Color(25, 118, 210)); // Azul
                            break;
                        case "ABERTA":
                            c.setForeground(new java.awt.Color(230, 81, 0)); // Laranja
                            break;
                        case "CANCELADA":
                            c.setForeground(new java.awt.Color(198, 40, 40)); // Vermelho
                            break;
                        default:
                            c.setForeground(table.getForeground());
                            break;
                    }
                }
                return c;
            }
        });
    }

    private void carregarTabela() {
        String statusStr = (String) cboFiltroStatus.getSelectedItem();
        StatusOS filtroStatus = null;
        if (statusStr != null && !"Todas".equalsIgnoreCase(statusStr)) {
            try {
                filtroStatus = StatusOS.valueOf(statusStr);
            } catch (IllegalArgumentException ignored) {
            }
        }

        String filtroPlaca = txtFiltroPlaca.getText().trim();

        DefaultTableModel modelo = (DefaultTableModel) tblOrdens.getModel();
        modelo.setRowCount(0);

        try {
            listaOrdens = ordemServicoService.listar(filtroStatus, filtroPlaca);
            for (OrdemServico os : listaOrdens) {
                String abertura = os.getDataAbertura() != null ? os.getDataAbertura().format(dtf) : "";
                String veiculo = (os.getVeiculo() != null)
                        ? (os.getVeiculo().getPlaca() + " " + os.getVeiculo().getModelo()) : "";
                String cliente = (os.getVeiculo() != null && os.getVeiculo().getCliente() != null)
                        ? os.getVeiculo().getCliente().getNome() : "";
                String status = os.getStatus() != null ? os.getStatus().name() : "";
                BigDecimal valor = os.getValorMaoObra() != null ? os.getValorMaoObra() : BigDecimal.ZERO;
                String valorStr = String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor);
                String abertaPor = os.getUsuario() != null ? os.getUsuario().getLogin() : "";

                modelo.addRow(new Object[]{
                    os.getId(),
                    abertura,
                    veiculo,
                    cliente,
                    status,
                    valorStr,
                    abertaPor
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar Ordens de Serviço: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFiltro = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        cboFiltroStatus = new javax.swing.JComboBox();
        lblPlaca = new javax.swing.JLabel();
        txtFiltroPlaca = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrdens = new javax.swing.JTable();
        btnNova = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnFechar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Ordens de Serviço");

        pnlFiltro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblStatus.setText("Status:");

        cboFiltroStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Todas", "ABERTA", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA" }));

        lblPlaca.setText("Placa:");

        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFiltroLayout = new javax.swing.GroupLayout(pnlFiltro);
        pnlFiltro.setLayout(pnlFiltroLayout);
        pnlFiltroLayout.setHorizontalGroup(
            pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltroLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboFiltroStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPlaca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFiltroPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(180, Short.MAX_VALUE))
        );
        pnlFiltroLayout.setVerticalGroup(
            pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltroLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(cboFiltroStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPlaca)
                    .addComponent(txtFiltroPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        tblOrdens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nº", "Abertura", "Veículo", "Cliente", "Status", "Valor (R$)", "Aberta por"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOrdens.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblOrdens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrdensMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOrdens);

        btnNova.setText("Nova OS");
        btnNova.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovaActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnNova, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNova, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        carregarTabela();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnNovaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovaActionPerformed
        java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(this);
        OrdemServicoDialog dialog = new OrdemServicoDialog(parent, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isSalvo()) {
            carregarTabela();
        }
    }//GEN-LAST:event_btnNovaActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        int linha = tblOrdens.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma Ordem de Serviço na tabela para editar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        OrdemServico os = listaOrdens.get(linha);
        java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(this);
        OrdemServicoDialog dialog = new OrdemServicoDialog(parent, true, os);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isSalvo()) {
            carregarTabela();
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        dispose();
    }//GEN-LAST:event_btnFecharActionPerformed

    private void tblOrdensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrdensMouseClicked
        if (evt.getClickCount() == 2 && tblOrdens.getSelectedRow() != -1) {
            btnEditarActionPerformed(null);
        }
    }//GEN-LAST:event_tblOrdensMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnNova;
    private javax.swing.JComboBox cboFiltroStatus;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPlaca;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlFiltro;
    private javax.swing.JTable tblOrdens;
    private javax.swing.JTextField txtFiltroPlaca;
    // End of variables declaration//GEN-END:variables
}
