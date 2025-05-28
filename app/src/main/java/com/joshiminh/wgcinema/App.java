package com.joshiminh.wgcinema;

import javax.swing.*;
import com.joshiminh.wgcinema.booking.*;
import com.joshiminh.wgcinema.dashboard.*;
import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.ResourceUtil;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

@SuppressWarnings("unused")
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
    private static final Path USER_FILE = Paths.get("user.txt");

    static { loadEnv(); }

    public App() {
        setTitle("Login");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(ResourceUtil.loadAppIcon());

        mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = ResourceUtil.loadImage("/images/background.jpg");
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        logoLabel = new JLabel(new ImageIcon(ResourceUtil.loadImage("/images/icon.png").getScaledInstance(70, 65, Image.SCALE_SMOOTH)));

        emailField = new JTextField(36);
        passwordField = new JPasswordField(36);
        passwordField.setEchoChar('•');
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        });

        preloadUserCredentials();

        registerButton = new JButton("Register");
        loginButton = new JButton("Login");
        dashboardButton = new JButton("Admin Dashboard");

        JButton[] buttons = {registerButton, loginButton, dashboardButton};
        for (JButton btn : buttons) {
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(30, 30, 30));
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btn.setPreferredSize(new Dimension(150, 36));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        registerButton.addActionListener(e -> openRegisterFrame());
        loginButton.addActionListener(e -> performLogin());
        dashboardButton.addActionListener(e -> performLogin());

        JPanel formPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(44, 44, 44, 220));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            }
        };
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 22, 10, 22);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(logoLabel, gbc);

        gbc.gridy++; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(dashboardButton);
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, new GridBagConstraints());
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        try (ResultSet rs = DAO.fetchAccountByEmail(DB_URL, email)) {
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                boolean isAdmin = rs.getBoolean("admin");
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
                        new MovieList(DB_URL, email).setVisible(true);
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
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, jakarta.mail.internet.InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Code - " + SERVICE_NAME);
            message.setText("Your password reset code is: " + code);
            jakarta.mail.Transport.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void resetPassword(String email, String newPassword) {
        int updated = DAO.updateAccountPassword(DB_URL, email, hashPassword(newPassword));
        if (updated > 0) {
            JOptionPane.showMessageDialog(this, "Password reset successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private void openRegisterFrame() {
        JFrame registerFrame = new Register(DB_URL);
        registerFrame.setVisible(true);
        registerFrame.setIconImage(ResourceUtil.loadAppIcon());
    }

    private void saveUserCredentials(String email, String password) {
        try (BufferedWriter writer = Files.newBufferedWriter(USER_FILE)) {
            writer.write(email);
            writer.newLine();
            writer.write(password);
        } catch (IOException ignored) {}
    }

    private void preloadUserCredentials() {
        if (Files.exists(USER_FILE)) {
            try (BufferedReader reader = Files.newBufferedReader(USER_FILE)) {
                String email = reader.readLine();
                String password = reader.readLine();
                if (email != null) emailField.setText(email);
                if (password != null) passwordField.setText(password);
            } catch (IOException ignored) {}
        }
        if (!Files.exists(USER_FILE)) {
            try { Files.createFile(USER_FILE); }
            catch (IOException e) { throw new RuntimeException("Failed to create user.txt file", e); }
        }
    }

    private static void loadEnv() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(".env")) {
            props.load(in);
        } catch (IOException e) {
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