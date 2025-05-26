package com.javaguides.javaswing.login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OmnichannelPanel extends JPanel {

    public OmnichannelPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Omnichannel Overview", JLabel.CENTER);
        header.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea(
            "Integrasi berbagai saluran komunikasi:\n\n" +
            "- Email\n" +
            "- WhatsApp\n" +
            "- SMS\n" +
            "- Chat Live\n\n" +
            "Semua saluran ini terintegrasi dalam satu sistem untuk memudahkan customer service dan pemasaran."
        );
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(new JScrollPane(infoArea), BorderLayout.CENTER);
    }
}
