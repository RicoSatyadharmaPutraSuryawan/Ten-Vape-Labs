/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.javaguides.javaswing.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author faydz
 */
public class SuperAdminLaporan extends javax.swing.JFrame {

    // Mendapatkan model dari JTable untuk manipulasi data
    private final DefaultTableModel model;

    /**
     * Creates new form SuperAdminData
     */
    public SuperAdminLaporan() {
        initComponents();
        // Mengambil model dari JTable setelah initComponents() dipanggil
        this.model = (DefaultTableModel) customerTable.getModel(); // Menggunakan nama variabel yang sudah diperbaiki
        setLocationRelativeTo(null);

        // Menambahkan Action Listeners untuk setiap tombol
        loadExcelBtn.addActionListener(e -> loadExcelData());
        saveExcelBtn.addActionListener(e -> saveExcelData());
        addBtn.addActionListener(e -> addData());
        updateBtn.addActionListener(e -> updateData());
        deleteBtn.addActionListener(e -> deleteData());
    }
    
    private void loadExcelData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ExcelReader excelReader = new ExcelReader(file);
            try {
                Vector<Vector<Object>> data = excelReader.readExcelData();
                
                model.setRowCount(0);
                model.setColumnCount(0);
                
                if (!data.isEmpty()) {
                    Vector<Object> header = data.get(0);
                    for (Object column : header) {
                        model.addColumn(column.toString());
                    }
                    for (int i = 1; i < data.size(); i++) {
                        model.addRow(data.get(i));
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
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getParentFile(), file.getName() + ".xlsx");
            }
            
            try (FileOutputStream fos = new FileOutputStream(file);
                 Workbook workbook = new XSSFWorkbook()) {
                
                Sheet sheet = workbook.createSheet("Customer Data");
                int rowCount = model.getRowCount();
                int colCount = model.getColumnCount();

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < colCount; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(model.getColumnName(i));
                }

                for (int i = 0; i < rowCount; i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < colCount; j++) {
                        Cell cell = row.createCell(j);
                        Object value = model.getValueAt(i, j);
                        cell.setCellValue(String.valueOf(value));
                    }
                }

                workbook.write(fos);
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addData() {
        Vector<String> newRow = new Vector<>();
        int colCount = model.getColumnCount();
        for (int i = 0; i < colCount; i++) {
            String value = JOptionPane.showInputDialog(this, "Masukkan data untuk kolom: " + model.getColumnName(i));
            newRow.add(value);
        }
        model.addRow(newRow);
    }

    private void updateData() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            int colCount = model.getColumnCount();
            for (int i = 0; i < colCount; i++) {
                String updatedValue = JOptionPane.showInputDialog(this, "Masukkan nilai baru untuk " + model.getColumnName(i),
                        model.getValueAt(selectedRow, i));
                if (updatedValue != null) {
                    model.setValueAt(updatedValue, selectedRow, i);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diupdate.");
        }
    }

    private void deleteData() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus baris ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                model.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.");
        }
    }

    // Inner class untuk menangani pembacaan file Excel
    private static class ExcelReader {
        private final File file;
        private final DataFormatter dataFormatter;

        public ExcelReader(File file) {
            this.file = file;
            this.dataFormatter = new DataFormatter();
        }

        public Vector<Vector<Object>> readExcelData() throws IOException {
            Vector<Vector<Object>> data = new Vector<>();
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) throw new IOException("File Excel kosong atau tidak memiliki header.");
                
                Vector<Object> header = new Vector<>();
                for (Cell cell : headerRow) {
                    header.add(getCellValue(cell));
                }
                data.add(header);
                
                int numCols = header.size();
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    
                    Vector<Object> rowData = new Vector<>();
                    for (int j = 0; j < numCols; j++) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        rowData.add(getCellValue(cell));
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
            return dataFormatter.formatCellValue(cell);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        logoutBtn = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        dashboardBtn = new javax.swing.JButton();
        userBtn = new javax.swing.JButton();
        dataBtn = new javax.swing.JButton();
        broadcastBtn = new javax.swing.JButton();
        persetujuanBtn = new javax.swing.JButton();
        laporanBtn = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        loadExcelBtn = new javax.swing.JButton();
        saveExcelBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        customerTableScrollPane = new javax.swing.JScrollPane();
        customerTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jPanel2.setBackground(new java.awt.Color(0, 53, 181));

        jLabel1.setFont(new java.awt.Font("Showcard Gothic", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DATA PELANGGAN");

        logoutBtn.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        logoutBtn.setText("Logout");
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(179, 179, 179)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addComponent(logoutBtn)
                .addGap(17, 17, 17))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(logoutBtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBackground(new java.awt.Color(0, 53, 181));

        dashboardBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        dashboardBtn.setText("Dashboard");
        dashboardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboardBtnActionPerformed(evt);
            }
        });

        userBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        userBtn.setText("User");
        userBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userBtnActionPerformed(evt);
            }
        });

        dataBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        dataBtn.setText("Data Pelanggan");
        dataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataBtnActionPerformed(evt);
            }
        });

        broadcastBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        broadcastBtn.setText("Broadcast");
        broadcastBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                broadcastBtnActionPerformed(evt);
            }
        });

        persetujuanBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        persetujuanBtn.setText("Persetujuan");
        persetujuanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                persetujuanBtnActionPerformed(evt);
            }
        });

        laporanBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        laporanBtn.setText("Laporan");
        laporanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                laporanBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(broadcastBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(persetujuanBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(laporanBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(dashboardBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(broadcastBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(persetujuanBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(userBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(laporanBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        loadExcelBtn.setBackground(new java.awt.Color(0, 53, 181));
        loadExcelBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        loadExcelBtn.setForeground(new java.awt.Color(255, 255, 255));
        loadExcelBtn.setText("Muat Data");

        saveExcelBtn.setBackground(new java.awt.Color(0, 53, 181));
        saveExcelBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        saveExcelBtn.setForeground(new java.awt.Color(255, 255, 255));
        saveExcelBtn.setText("Simpan Data");

        addBtn.setBackground(new java.awt.Color(0, 53, 181));
        addBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addBtn.setForeground(new java.awt.Color(255, 255, 255));
        addBtn.setText("Tambah Data");

        updateBtn.setBackground(new java.awt.Color(0, 53, 181));
        updateBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        updateBtn.setForeground(new java.awt.Color(255, 255, 255));
        updateBtn.setText("Update Data");

        deleteBtn.setBackground(new java.awt.Color(0, 53, 181));
        deleteBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        deleteBtn.setForeground(new java.awt.Color(255, 255, 255));
        deleteBtn.setText("Hapus Data");

        customerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        customerTableScrollPane.setViewportView(customerTable);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(loadExcelBtn)
                .addGap(18, 18, 18)
                .addComponent(saveExcelBtn)
                .addGap(18, 18, 18)
                .addComponent(addBtn)
                .addGap(18, 18, 18)
                .addComponent(updateBtn)
                .addGap(18, 18, 18)
                .addComponent(deleteBtn)
                .addGap(25, 25, 25))
            .addComponent(customerTableScrollPane)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addComponent(customerTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadExcelBtn)
                    .addComponent(saveExcelBtn)
                    .addComponent(addBtn)
                    .addComponent(updateBtn)
                    .addComponent(deleteBtn))
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
        int pilihan = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Logout", JOptionPane.OK_CANCEL_OPTION);
        if (pilihan == JOptionPane.OK_OPTION) {
            new Login().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_logoutBtnActionPerformed

    private void dashboardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dashboardBtnActionPerformed
        // Kembali ke halaman SuperAdminHome
        new SuperAdminHome("superadmin").setVisible(true);
        this.dispose();
    }//GEN-LAST:event_dashboardBtnActionPerformed

    private void userBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userBtnActionPerformed
        // Pindah ke halaman SuperAdminUser
        new SuperAdminUser("superadmin").setVisible(true);
        this.dispose();
    }//GEN-LAST:event_userBtnActionPerformed

    private void dataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataBtnActionPerformed
        // Tidak melakukan apa-apa karena sudah berada di halaman ini
    }//GEN-LAST:event_dataBtnActionPerformed

    private void broadcastBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_broadcastBtnActionPerformed
        // Pindah ke halaman SuperAdminBroadcast
        new SuperAdminBroadcast().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_broadcastBtnActionPerformed

    private void persetujuanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_persetujuanBtnActionPerformed
        // Pindah ke halaman SuperAdminPersetujuan
        new SuperAdminPersetujuan().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_persetujuanBtnActionPerformed

    private void laporanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_laporanBtnActionPerformed
        // Pindah ke halaman SuperAdminLaporan
        new SuperAdminLaporan().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_laporanBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SuperAdminData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SuperAdminData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SuperAdminData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SuperAdminData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SuperAdminData().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton broadcastBtn;
    private javax.swing.JTable customerTable;
    private javax.swing.JScrollPane customerTableScrollPane;
    private javax.swing.JButton dashboardBtn;
    private javax.swing.JButton dataBtn;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton laporanBtn;
    private javax.swing.JButton loadExcelBtn;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JButton persetujuanBtn;
    private javax.swing.JButton saveExcelBtn;
    private javax.swing.JButton updateBtn;
    private javax.swing.JButton userBtn;
    // End of variables declaration//GEN-END:variables
}