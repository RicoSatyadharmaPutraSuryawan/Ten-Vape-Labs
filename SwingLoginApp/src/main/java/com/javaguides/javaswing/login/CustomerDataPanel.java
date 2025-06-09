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
    private JButton loadExcelBtn, saveExcelBtn, addBtn, updateBtn, deleteBtn;

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

        // Tombol untuk menyimpan data ke Excel
        saveExcelBtn = new JButton("Simpan Data ke Excel");
        saveExcelBtn.addActionListener(e -> saveExcelData());

        // Tombol untuk menambah data
        addBtn = new JButton("Tambah Data");
        addBtn.addActionListener(e -> addData());

        // Tombol untuk mengupdate data
        updateBtn = new JButton("Update Data");
        updateBtn.addActionListener(e -> updateData());

        // Tombol untuk menghapus data
        deleteBtn = new JButton("Hapus Data");
        deleteBtn.addActionListener(e -> deleteData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(loadExcelBtn);
        buttonPanel.add(saveExcelBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadExcelData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ExcelReader excelReader = new ExcelReader(file);
            try {
                Vector<Vector<String>> data = excelReader.readExcelData();
                tableModel.setRowCount(0); // clear existing data
                tableModel.setColumnCount(0); // clear existing columns
                
                // Adding the header
                if (!data.isEmpty()) {
                    Vector<String> header = data.get(0);
                    for (String column : header) {
                        tableModel.addColumn(column);
                    }
                    // Adding the rows
                    for (int i = 1; i < data.size(); i++) {
                        tableModel.addRow(data.get(i));
                    }
                }

                JOptionPane.showMessageDialog(this, "Data berhasil dimuat dari file Excel.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal memuat file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveExcelData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file);
                 Workbook workbook = new XSSFWorkbook()) {
                 
                Sheet sheet = workbook.createSheet("Customer Data");
                int rowCount = tableModel.getRowCount();
                int colCount = tableModel.getColumnCount();

                // Write header
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < colCount; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(tableModel.getColumnName(i));
                }

                // Write data rows
                for (int i = 0; i < rowCount; i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < colCount; j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue((String) tableModel.getValueAt(i, j));
                    }
                }

                workbook.write(fos);
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addData() {
        Vector<String> newRow = new Vector<>();
        int colCount = tableModel.getColumnCount();
        for (int i = 0; i < colCount; i++) {
            newRow.add(JOptionPane.showInputDialog(this, "Masukkan data untuk kolom: " + tableModel.getColumnName(i)));
        }
        tableModel.addRow(newRow);
    }

    private void updateData() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            int colCount = tableModel.getColumnCount();
            for (int i = 0; i < colCount; i++) {
                String updatedValue = JOptionPane.showInputDialog(this, "Masukkan nilai baru untuk " + tableModel.getColumnName(i),
                        tableModel.getValueAt(selectedRow, i));
                if (updatedValue != null) {
                    tableModel.setValueAt(updatedValue, selectedRow, i);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diupdate.");
        }
    }

    private void deleteData() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.");
        }
    }

    // Inner class to handle Excel reading
    private class ExcelReader {
        private File file;

        public ExcelReader(File file) {
            this.file = file;
        }

        public Vector<Vector<String>> readExcelData() throws IOException {
            Vector<Vector<String>> data = new Vector<>();
            
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                
                // Reading the header
                Row headerRow = sheet.getRow(0);
                Vector<String> header = new Vector<>();
                for (Cell cell : headerRow) {
                    header.add(cell.toString());
                }
                data.add(header);
                
                // Reading the data
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    Vector<String> rowData = new Vector<>();
                    for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                        Cell cell = row.getCell(j);
                        String cellValue = getCellValue(cell);
                        rowData.add(cellValue);
                    }
                    data.add(rowData);
                }
            }
            return data;
        }

        private String getCellValue(Cell cell) {
            if (cell == null) {
                return "";
            }
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf(cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                default:
                    return "";
            }
        }
    }
}
