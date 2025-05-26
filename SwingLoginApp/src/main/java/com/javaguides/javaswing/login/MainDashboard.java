package com.javaguides.javaswing.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.javaguides.javaswing.login.BroadcastPanel;

public class MainDashboard extends JFrame {

    private JPanel mainPanel;
    private JLabel userLabel;
    private BroadcastPanel broadcastPanel = new BroadcastPanel();


    public MainDashboard(String userName) {
        setTitle("Tenvapelabs CRM Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Navigasi
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(3, 1));
        JButton omniChannelButton = new JButton("Omnichannel");
        JButton customerDataButton = new JButton("Data Pelanggan");
        JButton broadcastButton = new JButton("Broadcast");

        navPanel.add(omniChannelButton);
        navPanel.add(customerDataButton);
        navPanel.add(broadcastButton);

        // Panel User Info (di atas)
        userLabel = new JLabel("Selamat datang, " + userName + "!", JLabel.CENTER);
        userLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Konten Utama
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // ActionListener
        omniChannelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showOmnichannelPanel();
            }
        });

        customerDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCustomerDataPanel();
            }
        });

        broadcastButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showBroadcastPanel();
            }
        });

        // Layout utama
        setLayout(new BorderLayout());
        add(userLabel, BorderLayout.NORTH);     // Atas
        add(navPanel, BorderLayout.WEST);       // Kiri
        add(mainPanel, BorderLayout.CENTER);    // Tengah
    }

    private void showOmnichannelPanel() {
        mainPanel.removeAll();
        mainPanel.add(new OmnichannelPanel(), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showCustomerDataPanel() {
        mainPanel.removeAll();
        mainPanel.add(new CustomerDataPanel(), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showBroadcastPanel() {
        mainPanel.removeAll();
        mainPanel.add(broadcastPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
