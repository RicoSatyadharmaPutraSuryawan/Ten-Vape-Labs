package com.javaguides.javaswing.login;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UserLogin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private JPasswordField passwordField;
    private final JButton btnLogin;
    private final JPanel contentPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UserLogin frame = new UserLogin();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public UserLogin() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(450, 190, 700, 500);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 245)); // Light gray
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitle = new JLabel("User Login");
        lblTitle.setForeground(new Color(33, 37, 41));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setBounds(250, 30, 300, 50);
        contentPane.add(lblTitle);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblUsername.setBounds(150, 120, 100, 30);
        contentPane.add(lblUsername);

        textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        textField.setBounds(270, 120, 280, 35);
        textField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        contentPane.add(textField);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblPassword.setBounds(150, 180, 100, 30);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passwordField.setBounds(270, 180, 280, 35);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        contentPane.add(passwordField);

        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(30, 144, 255)); // Dodger blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(270, 250, 280, 45);
        contentPane.add(btnLogin);

        // Action listener login
        btnLogin.addActionListener(e -> {
            String username = textField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/swing_demo", "root", ""
                );

                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    if (role.equalsIgnoreCase("superadmin")) {
                        new SuperAdminDashboard(username).setVisible(true);
                    } else {
                        new MainDashboard(username).setVisible(true);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Login gagal. Username atau password salah.");
                }

                rs.close();
                pst.close();
                conn.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }
}
