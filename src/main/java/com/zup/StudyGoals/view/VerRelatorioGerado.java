package com.zup.StudyGoals.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zup.StudyGoals.domain.Meta;
import com.zup.StudyGoals.domain.Relatorio;
import com.zup.StudyGoals.presentation.apiclient.ApiClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class VerRelatorioGerado extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;

    ApiClient apiClient;
    private ObjectMapper objectMapper;

    public VerRelatorioGerado() {

        this.setTitle("Relatório gerado");
        this.setSize(1000, 120);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);

        this.apiClient = new ApiClient();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setViewportView(table);
        scrollPane.setBounds(10, 10, 960, 50);

        this.add(scrollPane);

        try {
            fazerRequisicaoGET();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        tableModel.fireTableDataChanged();

        this.setVisible(true);

    }

    private void fazerRequisicaoGET() throws IOException {
        String response = null;
        try {
            response = apiClient.getRequest("/relatorios");
            List<Relatorio> relatorios = objectMapper.readValue(response,
                    TypeFactory.defaultInstance().constructCollectionType(List.class, Relatorio.class));

            preencherTabelaComDados(relatorios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void preencherTabelaComDados(List<Relatorio> relatorios) {
        tableModel.addColumn("ID da Meta");
        tableModel.addColumn("Hora do Registro");
        tableModel.addColumn("Tempo total (minutos)");
        tableModel.addColumn("Média de tempo (minutos)");
        tableModel.addColumn("Total de resumos");
        tableModel.addColumn("Categoria mais consumida");
        tableModel.addColumn("Dias para concluir");
        tableModel.addColumn("Meta foi concluída?");

        tableModel.setRowCount(0);

        Relatorio relatorio = relatorios.get(relatorios.size() - 1);

            Object[] rowData = {
                    relatorio.getMetaId(),
                    relatorio.getHoraRegistro(),
                    relatorio.getTempoTotal(),
                    relatorio.getMediaTempo(),
                    relatorio.getTotalResumos(),
                    relatorio.getCategoriaMaisConsumida(),
                    relatorio.getDiasParaConcluir(),
                    relatorio.isMetaConcluida()
            };

            tableModel.addRow(rowData);

    }
}
