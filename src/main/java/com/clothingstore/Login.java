package com.clothingstore;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private final Color red = new Color(220, 25, 45);
    private final Color black = new Color(18, 18, 18);
    private final Color panelColor = new Color(30, 30, 30);

    public Login() {
        setTitle("STREET WEAR - Login");
        setSize(850, 550);
        setMinimumSize(new Dimension(650, 450));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(black);
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(400, 420));
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createLineBorder(red, 2));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("STREET WEAR");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(red);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("CLOTHING STORE MANAGEMENT");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginTitle = new JLabel("LOGIN TO YOUR ACCOUNT");
        loginTitle.setFont(new Font("Arial", Font.BOLD, 17));
        loginTitle.setForeground(Color.WHITE);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        styleField(usernameField);
        styleField(passwordField);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(red);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(300, 42));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel footer = new JLabel("WELCOME TO STREET WEAR");
        footer.setForeground(Color.GRAY);
        footer.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(25));
        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));
        panel.add(loginTitle);
        panel.add(Box.createVerticalStrut(22));

        addLabel(panel, "Username");
        panel.add(Box.createVerticalStrut(5));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(15));

        addLabel(panel, "Password");
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(25));

        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(footer);

        panel.add(Box.createVerticalGlue());
        add(panel);

        loginButton.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
    }

    private void addLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(300, 38));
        field.setPreferredSize(new Dimension(300, 38));
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBackground(new Color(45, 45, 45));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(red),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.");
            return;
        }

        String sql = "SELECT username FROM users "
                + "WHERE username = ? AND password = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this,
                            "Welcome to STREET WEAR!");

                    new Dashboard().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);

                    passwordField.setText("");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failed: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Login().setVisible(true));
    }
}