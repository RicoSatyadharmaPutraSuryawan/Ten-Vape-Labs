package com.javaguides.javaswing.login;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.Vector;

public class CustomerDataPanel extends JPanel {

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JButton loadExcelBtn;

    public CustomerDataPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Data Pelanggan", JLabel.CENTER);
        header.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        // Inisialisasi tabel kosong
        tableModel = new DefaultTableModel();
        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Tombol untuk memuat data dari Excel
        loadExcelBtn = new JButton("Muat Data dari Excel");
        loadExcelBtn.addActionListener(e -> loadExcelData());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(loadExcelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadExcelData() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                tableModel.setRowCount(0); // clear existing
                tableModel.setColumnCount(0);

                // Ambil header
                Row headerRow = sheet.getRow(0);
                for (Cell cell : headerRow) {
                    tableModel.addColumn(cell.toString());
                }

                // Ambil isi data
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    Vector<String> rowData = new Vector<>();
                    for (Cell cell : row) {
                        cell.setCellType(CellType.STRING);
                        rowData.add(cell.getStringCellValue());
                    }
                    tableModel.addRow(rowData);
                }

                JOptionPane.showMessageDialog(this, "Data berhasil dimuat dari file Excel.");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal memuat file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
