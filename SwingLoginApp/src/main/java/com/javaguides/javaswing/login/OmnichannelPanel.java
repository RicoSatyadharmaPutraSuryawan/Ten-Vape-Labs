package com.javaguides.javaswing.login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OmnichannelPanel extends JPanel {
    public OmnichannelPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 248, 255)); // Light background color to resemble the blue theme
        
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header Section with Icon
        JLabel header = new JLabel("Omnichannel Overview", JLabel.CENTER);
        header.setFont(new Font("Tahoma", Font.BOLD, 24));
        header.setForeground(new Color(0, 102, 204)); // Dark blue color for the header
        
        // Set an icon on the left of the header
        ImageIcon icon = new ImageIcon("path_to_icon.png"); // Replace with the actual path to your icon file
        header.setIcon(new ImageIcon(icon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)));
        add(header, BorderLayout.NORTH);

        // Create the HTML content for the JTextArea-like component
        String htmlContent = "<html><body style='font-family:Tahom; font-size:16px; color:#333;'>"
            + "<h3>Integrasi berbagai saluran komunikasi:</h3>"
            + "<ul>"
            + "<li><img src='path_to_email_icon.png' width='20' height='20' /> Email</li>" // Email icon
            + "<li><img src='path_to_whatsapp_icon.png' width='20' height='20' /> Whatsapp</li>" // WhatsApp icon
            + "<li><img src='path_to_sms_icon.png' width='20' height='20' /> SMS</li>" // SMS icon
            + "<li><img src='path_to_chat_icon.png' width='20' height='20' /> Chat Live</li>" // Chat Live icon
            + "</ul>"
            + "<p>Semua saluran ini terintegrasi dalam satu sistem untuk memudahkan customer service dan pemasaran.</p>"
            + "</body></html>";

        // Display the content inside JEditorPane
        JEditorPane infoArea = new JEditorPane("text/html", htmlContent);
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(255, 255, 255)); // White background
        infoArea.setForeground(new Color(51, 51, 51)); // Dark gray text for better readability

        // Adding a scroll pane to the JEditorPane
        JScrollPane scrollPane = new JScrollPane(infoArea);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for action buttons (similar to the "Muat File", "Preview", and "Kirim" buttons in the image)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton muatFileButton = new JButton("Muat File");
        muatFileButton.setBackground(new Color(255, 140, 0)); // Orange button
        muatFileButton.setForeground(Color.WHITE);
        bottomPanel.add(muatFileButton);

        JButton previewButton = new JButton("Preview");
        previewButton.setBackground(new Color(0, 153, 51)); // Green button
        previewButton.setForeground(Color.WHITE);
        bottomPanel.add(previewButton);

        JButton kirimButton = new JButton("Kirim");
        kirimButton.setBackground(new Color(0, 204, 102)); // Lighter green button
        kirimButton.setForeground(Color.WHITE);
        bottomPanel.add(kirimButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
