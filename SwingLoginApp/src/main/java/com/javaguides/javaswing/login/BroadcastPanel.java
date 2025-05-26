package com.javaguides.javaswing.login;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class BroadcastPanel extends JPanel {
    private JTextField titleField;
    private JTextArea messageArea;
    private JComboBox<String> channelComboBox;
    private JComboBox<String> segmentComboBox;
    private JSpinner scheduleSpinner;
    private JTable historyTable;

    public BroadcastPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Broadcast Panel");
        headerLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 1, 10, 10));

        titleField = new JTextField();
        formPanel.add(labeledComponent("Judul Broadcast:", titleField));

        messageArea = new JTextArea(5, 30);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        formPanel.add(labeledComponent("Isi Pesan:", messageScroll));

        channelComboBox = new JComboBox<>(new String[]{"Email", "SMS", "WhatsApp"});
        formPanel.add(labeledComponent("Saluran:", channelComboBox));

        segmentComboBox = new JComboBox<>(new String[]{"Semua Pelanggan", "Baru", "Aktif", "Pasif"});
        formPanel.add(labeledComponent("Segmentasi Pelanggan:", segmentComboBox));

        SpinnerDateModel model = new SpinnerDateModel();
        scheduleSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(scheduleSpinner, "yyyy-MM-dd HH:mm");
        scheduleSpinner.setEditor(editor);
        formPanel.add(labeledComponent("Jadwalkan Waktu Kirim:", scheduleSpinner));

        JButton uploadMessageBtn = new JButton("Unggah Pesan dari File");
        uploadMessageBtn.addActionListener(e -> loadMessageFromFile());
        formPanel.add(uploadMessageBtn);

        JButton sendButton = new JButton("Jadwalkan / Kirim");
        sendButton.addActionListener(this::sendBroadcast);
        formPanel.add(sendButton);

        add(formPanel, BorderLayout.WEST);

        String[] columns = {"Waktu", "Judul", "Saluran", "Segment", "Status"};
        historyTable = new JTable(new DefaultTableModel(columns, 0));
        JScrollPane tableScroll = new JScrollPane(historyTable);
        add(tableScroll, BorderLayout.CENTER);
    }

    private JPanel labeledComponent(String label, Component component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
        panel.add(jLabel, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void sendBroadcast(ActionEvent e) {
        String title = titleField.getText().trim();
        String message = messageArea.getText().trim();
        String channel = (String) channelComboBox.getSelectedItem();
        String segment = (String) segmentComboBox.getSelectedItem();
        LocalDateTime scheduleTime = LocalDateTime.ofInstant(
                ((java.util.Date) scheduleSpinner.getValue()).toInstant(),
                java.time.ZoneId.systemDefault()
        );

        if (title.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Judul dan pesan wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("WhatsApp".equalsIgnoreCase(channel)) {
            try {
                sendWhatsApp("628123456789", message); // Ganti dengan nomor tujuan nyata
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal kirim WhatsApp: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String status = scheduleTime.isAfter(LocalDateTime.now())
                ? "Dijadwalkan: " + scheduleTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "Terkirim";

        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.addRow(new Object[]{
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                title, channel, segment, status
        });

        titleField.setText("");
        messageArea.setText("");
        channelComboBox.setSelectedIndex(0);
        segmentComboBox.setSelectedIndex(0);

        JOptionPane.showMessageDialog(this, "Broadcast berhasil diproses!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sendWhatsApp(String phone, String message) throws IOException {
        String apiUrl = "https://kirim.pesan.id/api/v2/send-message";
        String token = "API_KEY_ANDA"; // Ganti dengan API key asli Anda

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(true);

        String payload = String.format("{\"phone\":\"%s\",\"message\":\"%s\"}", phone, message);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }

        int responseCode = conn.getResponseCode();
        System.out.println("WhatsApp API response code: " + responseCode);
    }

    private void loadMessageFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".txt")) {
                loadFromTxt(file);
            } else if (fileName.endsWith(".xlsx")) {
                loadFromExcel(file);
            } else {
                JOptionPane.showMessageDialog(this, "Format file tidak dikenali! Gunakan .txt atau .xlsx", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadFromTxt(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            messageArea.setText("");
            String line;
            while ((line = reader.readLine()) != null) {
                messageArea.append(line + "\n");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal membaca file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFromExcel(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            messageArea.setText("");
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) {
                    row.getCell(0).setCellType(CellType.STRING);
                    messageArea.append(row.getCell(0).getStringCellValue() + "\n");
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal membaca file Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}