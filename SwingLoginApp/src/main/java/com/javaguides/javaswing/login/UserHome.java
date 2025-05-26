package com.javaguides.javaswing.login;

import javax.swing.*;
import java.awt.*;

public class UserHome extends JFrame {

    private static final long serialVersionUID = 1L;

    public UserHome(String userName) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(450, 190, 1014, 597);
        setTitle("User Home");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + userName + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Tahoma", Font.PLAIN, 32));

        panel.add(welcomeLabel, BorderLayout.CENTER);
        add(panel);
    }
}
