package com.joshiminh.wgcinema;

import javax.swing.*;
import com.joshiminh.wgcinema.ui.dashboard.*;
import com.joshiminh.wgcinema.ui.booking.*;
import com.joshiminh.wgcinema.dao.*;
import com.joshiminh.wgcinema.auth.RegistrationFrame;
import com.joshiminh.wgcinema.util.ResourceManager;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;

import java.util.Properties;
import com.joshiminh.wgcinema.util.SessionManager; // Import SessionManager
import static com.joshiminh.wgcinema.util.AgentStyles.*; // Import AgentStyles

public class App extends JFrame {
    private JPanel mainPanel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel logoLabel;
    private JTextField emailField;
    private JButton dashboardButton;

    private static String DB_HOST, DB_NAME, DB_USERNAME, DB_PASSWORD, DB_URL;
    private static String SMTP_EMAIL, SMTP_APP_PASSWORD, SERVICE_NAME = "WG Cinema";

    static {
        loadEnv();
    }

    public App() {
        setTitle("Login");
        setSize(500, 450); // Keep width 500, height 450
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(ResourceManager.loadAppIcon());

        mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = ResourceManager.loadImage("/images/app_background.jpg");
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        logoLabel = new JLabel(new ImageIcon(
                ResourceManager.loadImage("/images/app_icon.png").getScaledInstance(70, 65, Image.SCALE_SMOOTH)));

        emailField = new JTextField(18); // Narrow email field width
        passwordField = new JPasswordField(18); // Narrow password field width
        passwordField.setEchoChar('•');
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    performLogin();
            }
        });

        preloadUserCredentials();

        registerButton = new JButton("Register");
        loginButton = new JButton("Login");
        dashboardButton = new JButton("Admin Dashboard");

        // Button sizes
        Dimension smallButtonSize = new Dimension(150, 36); // Size for Register and Login
        Dimension largeButtonSize = new Dimension(320, 36); // Size for Admin Dashboard (150 + 20 + 150)

        JButton[] buttons = { registerButton, loginButton, dashboardButton };
        for (JButton btn : buttons) {
            btn.setForeground(TEXT_COLOR);
            btn.setBackground(ACCENT_BLUE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(ACCENT_BLUE.brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(ACCENT_BLUE);
                }
            });
        }
        // Apply specific sizes
        registerButton.setPreferredSize(smallButtonSize);
        loginButton.setPreferredSize(smallButtonSize);
        dashboardButton.setPreferredSize(largeButtonSize);

        registerButton.addActionListener(e -> openRegisterFrame());
        loginButton.addActionListener(e -> performLogin());
        dashboardButton.addActionListener(e -> performLogin());

        JPanel formPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Use SECONDARY_BACKGROUND with transparency
                g.setColor(new Color(SECONDARY_BACKGROUND.getRed(), SECONDARY_BACKGROUND.getGreen(),
                        SECONDARY_BACKGROUND.getBlue(), 220));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            }
        };
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 22, 10, 22);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(logoLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(TEXT_COLOR);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        emailField.setBackground(PRIMARY_BACKGROUND);
        emailField.setForeground(TEXT_COLOR);
        emailField.setBorder(componentBorder());
        formPanel.add(emailField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField.setBackground(PRIMARY_BACKGROUND);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(componentBorder());
        formPanel.add(passwordField, gbc);

        // Adjust button layout to match UI design
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonContainerPanel = new JPanel();
        buttonContainerPanel.setLayout(new BoxLayout(buttonContainerPanel, BoxLayout.Y_AXIS)); // Stack vertically
        buttonContainerPanel.setOpaque(false);

        // Hàng 1: Register và Login
        JPanel topButtonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Horizontal gap of 20
        topButtonRow.setOpaque(false);
        topButtonRow.add(registerButton);
        topButtonRow.add(loginButton);
        topButtonRow.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button row

        // Hàng 2: Admin Dashboard
        JPanel bottomButtonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Centered, no extra padding
        bottomButtonRow.setOpaque(false);
        bottomButtonRow.add(dashboardButton);
        bottomButtonRow.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button row

        buttonContainerPanel.add(topButtonRow);
        buttonContainerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Vertical gap between rows
        buttonContainerPanel.add(bottomButtonRow);

        formPanel.add(buttonContainerPanel, gbc); // Add button container to form

        mainPanel.add(formPanel, new GridBagConstraints());
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        try (ResultSet rs = AccountDAO.fetchAccountByEmail(DB_URL, email)) {
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                int adminValueFromDb = rs.getInt("admin");
                boolean isAdmin = adminValueFromDb == 1;

                System.out.println("Debug: Raw 'admin' value from DB for " + email + ": " + adminValueFromDb);
                System.out.println("Debug: isAdmin (after comparison) for " + email + ": " + isAdmin);

                if (hash.equals(hashPassword(password))) {
                    saveUserCredentials(email, password);
                    if (dashboardButton.getModel().isArmed() || dashboardButton.hasFocus()) {
                        if (isAdmin) {
                            new Dashboard(DB_URL).setVisible(true);
                            dispose();
                        } else {
                            showError("You are not an admin.", "Access Denied");
                        }
                    } else {
                        new MovieGridPanel(DB_URL, email).setVisible(true);
                        dispose();
                    }
                } else {
                    handleWrongPassword(email);
                }
            } else {
                showError("Invalid email or password", "Login Failed");
            }
        } catch (SQLException ex) {
            showError("Database connection error", "Error");
        }
    }

    private void handleWrongPassword(String email) {
        int option = JOptionPane.showConfirmDialog(this,
                "Invalid email or password.\nWould you like to reset your password?",
                "Login Failed", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            String code = generateResetCode();
            if (sendResetEmail(email, code)) {
                String inputCode = JOptionPane.showInputDialog(this, "Enter the code sent to your email:");
                if (inputCode != null && inputCode.equals(code)) {
                    String newPassword = JOptionPane.showInputDialog(this, "Enter your new password:");
                    if (newPassword != null && !newPassword.isEmpty()) {
                        resetPassword(email, newPassword);
                    }
                } else {
                    showError("Incorrect code.", "Reset Failed");
                }
            } else {
                showError("Failed to send reset email.", "Error");
            }
        }
    }

    private String generateResetCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    private boolean sendResetEmail(String toEmail, String code) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            jakarta.mail.Session session = jakarta.mail.Session.getInstance(props, new jakarta.mail.Authenticator() {
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(SMTP_EMAIL, SMTP_APP_PASSWORD);
                }
            });
            jakarta.mail.Message message = new jakarta.mail.internet.MimeMessage(session);
            message.setFrom(new jakarta.mail.internet.InternetAddress(SMTP_EMAIL, SERVICE_NAME));
            message.setRecipients(jakarta.mail.Message.RecipientType.TO,
                    jakarta.mail.internet.InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Code - " + SERVICE_NAME);
            message.setText("Your password reset code is: " + code);
            jakarta.mail.Transport.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void resetPassword(String email, String newPassword) {
        int updated = AccountDAO.updateAccountPassword(DB_URL, email, hashPassword(newPassword));
        if (updated > 0) {
            JOptionPane.showMessageDialog(this, "Password reset successful.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Failed to reset password.", "Error");
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private void openRegisterFrame() {
        JFrame registerFrame = new RegistrationFrame(DB_URL);
        registerFrame.setVisible(true);
        registerFrame.setIconImage(ResourceManager.loadAppIcon());
    }

    private void saveUserCredentials(String email, String password) {
        SessionManager.saveSession(email, password);
    }

    private void preloadUserCredentials() {
        String email = SessionManager.getEmail();
        String password = SessionManager.getPassword();
        if (email != null)
            emailField.setText(email);
        if (password != null)
            passwordField.setText(password);
    }

    private static void loadEnv() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(".env")) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Error loading .env file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load .env file", e);
        }
        DB_HOST = props.getProperty("DB_HOST");
        DB_NAME = props.getProperty("DB_NAME");
        DB_USERNAME = props.getProperty("DB_USERNAME");
        DB_PASSWORD = props.getProperty("DB_PASSWORD");
        if (DB_HOST == null || DB_NAME == null || DB_USERNAME == null || DB_PASSWORD == null)
            throw new RuntimeException("Missing required database environment variables in .env");
        DB_URL = "jdbc:mysql://" + DB_HOST + "/" + DB_NAME + "?user=" + DB_USERNAME + "&password=" + DB_PASSWORD;
        SMTP_EMAIL = props.getProperty("SMTP_EMAIL");
        SMTP_APP_PASSWORD = props.getProperty("SMTP_APP_PASSWORD");
        if (SMTP_EMAIL == null || SMTP_APP_PASSWORD == null)
            throw new RuntimeException("Missing required SMTP or user file environment variables in .env");
    }

    private void showError(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new App();
    }
}