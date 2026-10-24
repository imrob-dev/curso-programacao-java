/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oficina.gui;

import com.oficina.model.OrdemServico;
import com.oficina.model.StatusOS;
import com.oficina.model.Usuario;
import com.oficina.model.Veiculo;
import com.oficina.service.OrdemServicoService;
import com.oficina.service.RegraNegocioException;
import com.oficina.service.VeiculoService;
import com.oficina.util.Sessao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 * Janela de diálogo para abertura e edição de Ordem de Serviço (T07).
 *
 * @author Rob
 */
public class OrdemServicoDialog extends javax.swing.JDialog {

    private final OrdemServicoService ordemServicoService;
    private final VeiculoService veiculoService;
    private OrdemServico ordemServico;
    private boolean salvo = false;

    /**
     * Construtor para criar nova OS.
     */
    public OrdemServicoDialog(java.awt.Frame parent, boolean modal) {
        this(parent, modal, null);
    }

    /**
     * Construtor para editar OS existente ou abrir nova se os == null.
     */
    public OrdemServicoDialog(java.awt.Frame parent, boolean modal, OrdemServico os) {
        super(parent, modal);
        this.ordemServicoService = new OrdemServicoService();
        this.veiculoService = new VeiculoService();
        initComponents();
        getRootPane().setDefaultButton(btnSalvar);
        carregarVeiculos();
        configurarDados(os);
    }

    public boolean isSalvo() {
        return salvo;
    }

    private void carregarVeiculos() {
        DefaultComboBoxModel<Veiculo> model = new DefaultComboBoxModel<>();
        try {
            List<Veiculo> veiculos = veiculoService.listar();
            for (Veiculo v : veiculos) {
                model.addElement(v);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar veículos: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
        cboVeiculo.setModel(model);
    }

    private void configurarDados(OrdemServico os) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (os == null) {
            this.ordemServico = new OrdemServico();
            setTitle("Nova Ordem de Serviço");
            txtAbertura.setText(LocalDate.now().format(dtf));

            Usuario logado = Sessao.getUsuario();
            txtUsuario.setText(logado != null ? logado.getNome() : "");

            DefaultComboBoxModel<StatusOS> statusModel = new DefaultComboBoxModel<>();
            statusModel.addElement(StatusOS.ABERTA);
            cboStatus.setModel(statusModel);
            cboStatus.setEnabled(false); // Nova OS é fixa em ABERTA (RN11)
            txtValor.setText("0,00");
        } else {
            this.ordemServico = os;
            setTitle("Ordem de Serviço nº " + os.getId());
            txtAbertura.setText(os.getDataAbertura() != null ? os.getDataAbertura().format(dtf) : "");
            txtUsuario.setText(os.getUsuario() != null ? os.getUsuario().getNome() : "");

            // Seleciona veículo correspondente
            if (os.getVeiculo() != null) {
                for (int i = 0; i < cboVeiculo.getItemCount(); i++) {
                    Veiculo v = (Veiculo) cboVeiculo.getItemAt(i);
                    if (v.getId().equals(os.getVeiculo().getId())) {
                        cboVeiculo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            txtProblema.setText(os.getDescricaoProblema() != null ? os.getDescricaoProblema() : "");
            txtServico.setText(os.getServicoRealizado() != null ? os.getServicoRealizado() : "");

            BigDecimal valor = os.getValorMaoObra() != null ? os.getValorMaoObra() : BigDecimal.ZERO;
            txtValor.setText(String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor));

            DefaultComboBoxModel<StatusOS> statusModel = new DefaultComboBoxModel<>(StatusOS.values());
            cboStatus.setModel(statusModel);
            cboStatus.setSelectedItem(os.getStatus());

            // RN13: OS CONCLUIDA ou CANCELADA não pode ser alterada: desabilita campos e esconde botão Salvar
            if (os.getStatus() == StatusOS.CONCLUIDA || os.getStatus() == StatusOS.CANCELADA) {
                cboVeiculo.setEnabled(false);
                txtProblema.setEditable(false);
                txtServico.setEditable(false);
                txtValor.setEditable(false);
                cboStatus.setEnabled(false);
                btnSalvar.setVisible(false);
            }
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

        lblVeiculo = new javax.swing.JLabel();
        cboVeiculo = new javax.swing.JComboBox();
        lblAbertura = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblProblema = new javax.swing.JLabel();
        jScrollPaneProblema = new javax.swing.JScrollPane();
        txtProblema = new javax.swing.JTextArea();
        lblServico = new javax.swing.JLabel();
        jScrollPaneServico = new javax.swing.JScrollPane();
        txtServico = new javax.swing.JTextArea();
        lblValor = new javax.swing.JLabel();
        txtValor = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        cboStatus = new javax.swing.JComboBox();
        btnSalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ordem de Serviço");
        setModal(true);
        setResizable(false);

        lblVeiculo.setText("Veículo:");

        lblAbertura.setText("Abertura:");

        txtAbertura.setEditable(false);

        lblUsuario.setText("Aberta por:");

        txtUsuario.setEditable(false);

        lblProblema.setText("Problema:");

        txtProblema.setColumns(20);
        txtProblema.setLineWrap(true);
        txtProblema.setRows(3);
        txtProblema.setWrapStyleWord(true);
        jScrollPaneProblema.setViewportView(txtProblema);

        lblServico.setText("Serviço realizado:");

        txtServico.setColumns(20);
        txtServico.setLineWrap(true);
        txtServico.setRows(3);
        txtServico.setWrapStyleWord(true);
        jScrollPaneServico.setViewportView(txtServico);

        lblValor.setText("Mão de obra (R$):");

        txtValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValor.setText("0,00");

        lblStatus.setText("Status:");

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblVeiculo)
                            .addComponent(lblAbertura)
                            .addComponent(lblUsuario)
                            .addComponent(lblProblema)
                            .addComponent(lblServico)
                            .addComponent(lblValor)
                            .addComponent(lblStatus))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboVeiculo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPaneProblema, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                            .addComponent(jScrollPaneServico)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVeiculo)
                    .addComponent(cboVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAbertura)
                    .addComponent(txtAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsuario)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProblema)
                    .addComponent(jScrollPaneProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblServico)
                    .addComponent(jScrollPaneServico, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblValor)
                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
            Veiculo veiculo = (Veiculo) cboVeiculo.getSelectedItem();
            if (veiculo == null) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, selecione um veículo.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String problema = txtProblema.getText();
            String servico = txtServico.getText();

            String valorTexto = txtValor.getText().trim()
                    .replace(".", "")
                    .replace(",", ".");
            BigDecimal valor;
            try {
                valor = valorTexto.isEmpty() ? BigDecimal.ZERO : new BigDecimal(valorTexto);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Valor da mão de obra inválido. Informe um número como 150,00.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                txtValor.requestFocus();
                return;
            }

            StatusOS status = (StatusOS) cboStatus.getSelectedItem();

            ordemServico.setVeiculo(veiculo);
            ordemServico.setDescricaoProblema(problema);
            ordemServico.setServicoRealizado(servico);
            ordemServico.setValorMaoObra(valor);
            ordemServico.setStatus(status);

            ordemServicoService.salvar(ordemServico);

            salvo = true;
            JOptionPane.showMessageDialog(this,
                    "Ordem de Serviço salva com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar Ordem de Serviço: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox cboStatus;
    private javax.swing.JComboBox cboVeiculo;
    private javax.swing.JScrollPane jScrollPaneProblema;
    private javax.swing.JScrollPane jScrollPaneServico;
    private javax.swing.JLabel lblAbertura;
    private javax.swing.JLabel lblProblema;
    private javax.swing.JLabel lblServico;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JLabel lblValor;
    private javax.swing.JLabel lblVeiculo;
    private javax.swing.JTextField txtAbertura;
    private javax.swing.JTextArea txtProblema;
    private javax.swing.JTextArea txtServico;
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
