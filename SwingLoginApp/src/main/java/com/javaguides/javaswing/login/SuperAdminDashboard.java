package com.javaguides.javaswing.login;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SuperAdminDashboard extends JFrame {
    private JTable adminTable;
    private DefaultTableModel model;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private Connection conn;

    public SuperAdminDashboard(String username) {
        setTitle("Super Admin Dashboard - Welcome " + username);
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Username", "Role"}, 0);
        adminTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(adminTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Admin");
        editButton = new JButton("Edit Admin");
        deleteButton = new JButton("Delete Admin");
        refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        connectToDatabase();
        loadAdminData();

        addButton.addActionListener(e -> addAdmin());
        editButton.addActionListener(e -> editAdmin());
        deleteButton.addActionListener(e -> deleteAdmin());
        refreshButton.addActionListener(e -> loadAdminData());
    }

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/swing_demo", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAdminData() {
        model.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, username, role FROM users WHERE role = 'admin'");
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("role")});
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addAdmin() {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        String password = JOptionPane.showInputDialog(this, "Enter password:");
        if (username != null && password != null) {
            try {
                PreparedStatement pst = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'admin')");
                pst.setString(1, username);
                pst.setString(2, password);
                pst.executeUpdate();
                pst.close();
                loadAdminData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void editAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) model.getValueAt(selectedRow, 0);
            String newUsername = JOptionPane.showInputDialog(this, "Enter new username:", model.getValueAt(selectedRow, 1));
            if (newUsername != null) {
                try {
                    PreparedStatement pst = conn.prepareStatement("UPDATE users SET username = ? WHERE id = ?");
                    pst.setString(1, newUsername);
                    pst.setInt(2, id);
                    pst.executeUpdate();
                    pst.close();
                    loadAdminData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an admin to edit.");
        }
    }

    private void deleteAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this admin?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(selectedRow, 0);
                try {
                    PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE id = ?");
                    pst.setInt(1, id);
                    pst.executeUpdate();
                    pst.close();
                    loadAdminData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an admin to delete.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuperAdminDashboard("superadmin").setVisible(true));
    }
}
