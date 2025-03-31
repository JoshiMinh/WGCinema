package app;
import booking.*;
import dashboard.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class App extends JFrame {
    private JPanel mainPanel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel logoLabel;
    private JTextField usernameField;
    private JTextField hostField;
    private JTextField databaseField;

    public App() {
        setTitle("Admin Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("images/icon.png").getImage());

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("images/background.jpg").getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        int fieldColumns = 16;

        passwordField = new JPasswordField(fieldColumns);
        passwordField.setEchoChar('•');
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin(0);
            }
        });

        usernameField = new JTextField("root", fieldColumns);
        hostField = new JTextField("localhost:3306", fieldColumns);
        databaseField = new JTextField("cinema_data", fieldColumns);

        loginButton = new JButton("Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.DARK_GRAY);
        loginButton.addActionListener(e -> performLogin(1));

        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.setForeground(Color.WHITE);
        dashboardButton.setBackground(Color.DARK_GRAY);
        dashboardButton.addActionListener(e -> performLogin(0));

        ImageIcon logoIcon = new ImageIcon("images/icon.png");
        logoLabel = new JLabel(new ImageIcon(logoIcon.getImage().getScaledInstance(60, 55, Image.SCALE_SMOOTH)));

        JPanel rectanglePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(56, 56, 56, 179));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rectanglePanel.setOpaque(false);
        rectanglePanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rectanglePanel.add(logoLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel hostLabel = new JLabel("Host: ");
        hostLabel.setForeground(Color.WHITE);
        rectanglePanel.add(hostLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(hostField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel databaseLabel = new JLabel("Database: ");
        databaseLabel.setForeground(Color.WHITE);
        rectanglePanel.add(databaseLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(databaseField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setForeground(Color.WHITE);
        rectanglePanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(usernameField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setForeground(Color.WHITE);
        rectanglePanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(passwordField, gbc);

        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        rectanglePanel.add(dashboardButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(loginButton, gbc);

        mainPanel.add(rectanglePanel, new GridBagConstraints());
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void performLogin(int mode) {
        String host = hostField.getText();
        String database = databaseField.getText();
        String dbUsername = usernameField.getText();
        String dbPassword = new String(passwordField.getPassword());
        String url = "jdbc:mysql://" + host + "/" + database + "?user=" + dbUsername + "&password=" + dbPassword;

        try (Connection connection = DriverManager.getConnection(url)) {
            System.out.println("Login successful.");
            if (mode == 0) {
                new Dashboard(url).setVisible(true);
            } else if (mode == 1) {
                new MovieList(url).setVisible(true);
            }
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new App();
    }
}