package com.javaguides.javaswing.login;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BroadcastPanel extends JPanel 
{
  
    private JTextField titleField;
    private JTextArea messageArea;
    private JComboBox<String> channelBox;
    private JComboBox<String> segmentBox;
    private JCheckBox scheduleCheck;
    private JSpinner timeSpinner;
    private JList<String> historyList;
    private DefaultListModel<String> historyModel;
    private JLabel statusLabel;

    public BroadcastPanel () {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        
        initComponents();
        layoutComponents();
        addEventHandlers();
    }

    private void initComponents() {
        // Input fields
        titleField = new JTextField(20);
        messageArea = new JTextArea(4, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        
        // Dropdown menus
        channelBox = new JComboBox<>(new String[]{"📧 Email", "📱 SMS", "💬 WhatsApp"});
        segmentBox = new JComboBox<>(new String[]{"👥 Semua", "🆕 Baru", "✅ Aktif", "😴 Pasif"});
        
        // Schedule components
        scheduleCheck = new JCheckBox("Jadwalkan pengiriman");
        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "dd/MM/yyyy HH:mm"));
        timeSpinner.setEnabled(false);
        
        // History
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Status
        statusLabel = new JLabel("Siap mengirim broadcast");
        statusLabel.setForeground(new Color(0, 120, 0));
    }

    private void layoutComponents() {
        // Header
        JLabel header = new JLabel("📢 Broadcast Center", JLabel.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(header, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Left panel - Input form
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel);
        
        // Right panel - History
        JPanel historyPanel = createHistoryPanel();
        mainPanel.add(historyPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status bar
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("✍️ Buat Broadcast"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("📝 Judul:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleField, gbc);

        // Message
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("💬 Pesan:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1; gbc.weighty = 0.3;
        panel.add(new JScrollPane(messageArea), gbc);

        // Channel and Segment
        gbc.gridy = 2; gbc.weighty = 0; gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("📡 Saluran:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(channelBox, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🎯 Target:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(segmentBox, gbc);

        // Schedule
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(scheduleCheck, gbc);
        
        gbc.gridy = 5; gbc.gridx = 1; gbc.gridwidth = 1;
        panel.add(timeSpinner, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loadBtn = new JButton("📁 Muat File");
        JButton previewBtn = new JButton("👁️ Preview");
        JButton sendBtn = new JButton("🚀 Kirim");
        
        loadBtn.setBackground(new Color(100, 149, 237));
        loadBtn.setForeground(Color.WHITE);
        previewBtn.setBackground(new Color(255, 140, 0));
        previewBtn.setForeground(Color.WHITE);
        sendBtn.setBackground(new Color(34, 139, 34));
        sendBtn.setForeground(Color.WHITE);
        
        buttonPanel.add(loadBtn);
        buttonPanel.add(previewBtn);
        buttonPanel.add(sendBtn);
        
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("📋 Riwayat Broadcast"));
        
        historyList.setCellRenderer(new HistoryListRenderer());
        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton clearBtn = new JButton("🗑️ Bersihkan");
        clearBtn.addActionListener(e -> {
            historyModel.clear();
            updateStatus("Riwayat dibersihkan", false);
        });
        panel.add(clearBtn, BorderLayout.SOUTH);
        
        return panel;
    }

    private void addEventHandlers() {
        // Schedule checkbox handler
        scheduleCheck.addActionListener(e -> 
            timeSpinner.setEnabled(scheduleCheck.isSelected())
        );

        // Button handlers
        Component[] buttons = findButtonsInPanel(this);
        for (Component comp : buttons) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                
                if (text.contains("Muat File")) {
                    btn.addActionListener(this::loadFile);
                } else if (text.contains("Preview")) {
                    btn.addActionListener(this::previewMessage);
                } else if (text.contains("Kirim")) {
                    btn.addActionListener(this::sendBroadcast);
                }
            }
        }

        // Real-time validation
        titleField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateForm();
            }
        });
        
        messageArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateForm();
            }
        });
    }

    private Component[] findButtonsInPanel(Container container) {
        java.util.List<Component> buttons = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                buttons.add(comp);
            } else if (comp instanceof Container) {
                Component[] subButtons = findButtonsInPanel((Container) comp);
                for (Component btn : subButtons) {
                    buttons.add(btn);
                }
            }
        }
        return buttons.toArray(new Component[0]);
    }

    private void loadFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Text files (*.txt)", "txt"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                messageArea.setText(content.toString());
                updateStatus("File berhasil dimuat: " + file.getName(), false);
            } catch (IOException ex) {
                updateStatus("Gagal memuat file: " + ex.getMessage(), true);
            }
        }
    }

    private void previewMessage(ActionEvent e) {
        if (!validateForm()) return;
        
        String preview = String.format(
            "📢 PREVIEW BROADCAST\n\n" +
            "Judul: %s\n" +
            "Saluran: %s\n" +
            "Target: %s\n" +
            "Waktu: %s\n\n" +
            "Pesan:\n%s",
            titleField.getText(),
            channelBox.getSelectedItem(),
            segmentBox.getSelectedItem(),
            scheduleCheck.isSelected() ? timeSpinner.getValue().toString() : "Segera",
            messageArea.getText()
        );
        
        JOptionPane.showMessageDialog(this, preview, "Preview Broadcast", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void sendBroadcast(ActionEvent e) {
        if (!validateForm()) return;
        
        // Simulate sending
        Timer timer = new Timer(100, null);
        final int[] progress = {0};
        
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                progress[0] += 10;
                updateStatus("Mengirim broadcast... " + progress[0] + "%", false);
                
                if (progress[0] >= 100) {
                    timer.stop();
                    
                    // Add to history
                    String historyEntry = String.format(
                        "[%s] %s - %s (%s)",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                        titleField.getText(),
                        channelBox.getSelectedItem(),
                        segmentBox.getSelectedItem()
                    );
                    historyModel.addElement(historyEntry);
                    
                    // Clear form
                    clearForm();
                    updateStatus("✅ Broadcast berhasil dikirim!", false);
                    
                    // Auto-clear status after 3 seconds
                    Timer clearTimer = new Timer(3000, evt2 -> 
                        updateStatus("Siap mengirim broadcast", false));
                    clearTimer.setRepeats(false);
                    clearTimer.start();
                }
            }
        });
        timer.start();
    }

    private boolean validateForm() {
        boolean isValid = !titleField.getText().trim().isEmpty() && 
                         !messageArea.getText().trim().isEmpty();
        
        if (!isValid) {
            updateStatus("⚠️ Mohon lengkapi judul dan pesan", true);
        } else {
            updateStatus("✅ Form valid, siap dikirim", false);
        }
        
        return isValid;
    }

    private void clearForm() {
        titleField.setText("");
        messageArea.setText("");
        channelBox.setSelectedIndex(0);
        segmentBox.setSelectedIndex(0);
        scheduleCheck.setSelected(false);
        timeSpinner.setEnabled(false);
    }

    private void updateStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 120, 0));
    }

    // Custom list renderer for better history display
    private class HistoryListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (index % 2 == 0) {
                setBackground(isSelected ? getBackground() : new Color(248, 248, 248));
            }
            
            setFont(new Font("SansSerif", Font.PLAIN, 12));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            return this;
        }
    }
}