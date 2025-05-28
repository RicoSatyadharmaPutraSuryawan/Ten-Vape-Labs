package com.javaguides.javaswing.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboard extends JFrame {

    private JPanel mainPanel;
    private JLabel userLabel;
    private BroadcastPanel broadcastPanel = new BroadcastPanel();

    public MainDashboard(String userName) {
        setTitle("Tenvapelabs CRM Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // To keep the window size fixed

        // Main Panel Styling
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // User Label Styling
        userLabel = new JLabel("Selamat datang, " + userName + "!", JLabel.CENTER);
        userLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        userLabel.setForeground(new Color(40, 40, 40)); // Dark text color
        userLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Panel Navigasi Styling (Left Side)
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(3, 1, 10, 10));  // Adjusted space between buttons
        navPanel.setBackground(Color.WHITE);
        JButton omniChannelButton = new JButton("Omnichannel");
        JButton customerDataButton = new JButton("Data Pelanggan");
        JButton broadcastButton = new JButton("Broadcast");

        // Button Styling
        StyleButton(omniChannelButton);
        StyleButton(customerDataButton);
        StyleButton(broadcastButton);

        navPanel.add(omniChannelButton);
        navPanel.add(customerDataButton);
        navPanel.add(broadcastButton);

        // ActionListener for Navigation Buttons
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
        add(userLabel, BorderLayout.NORTH);    // Top
        add(navPanel, BorderLayout.WEST);      // Left
        add(mainPanel, BorderLayout.CENTER);   // Center
    }

    private void StyleButton(JButton button) {
        button.setBackground(new Color(24, 144, 255)); // Blue background color
        button.setForeground(Color.WHITE);  // White text
        button.setFont(new Font("Tahoma", Font.BOLD, 14));  // Consistent font
        button.setFocusPainted(false); // Removes the focus border
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Adds padding inside buttons
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Changes cursor to hand on hover
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

    // Main Method to Test the UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainDashboard("User").setVisible(true);
            }
        });
    }
}
