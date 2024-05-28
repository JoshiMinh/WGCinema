import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JPanel mainPanel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel logoLabel;
    private JTextField serverIPField;

    public Login() {
        setTitle("Admin Login");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("Icons/WGLogo.png").getImage());

        // Create main panel with background image
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("background.jpg").getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Initialize password field
        passwordField = new JPasswordField(15);
        passwordField.setForeground(Color.BLACK);
        passwordField.setEchoChar('•');
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // Initialize server IP field
        serverIPField = new JTextField(15);
        serverIPField.setForeground(Color.BLACK);
        serverIPField.setText("JoshiNitro5");

        // Initialize login button
        loginButton = new JButton("Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.DARK_GRAY);
        loginButton.addActionListener(e -> performLogin());

        // Initialize logo label
        ImageIcon logoIcon = new ImageIcon("Icons/WGLogo.png");
        Image logoImage = logoIcon.getImage();
        logoLabel = (logoImage != null) ? new JLabel(new ImageIcon(logoImage.getScaledInstance(60, 55, Image.SCALE_SMOOTH))) : new JLabel("Logo not found");

        // Create rectangle panel with semi-transparent background
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

        // Add components to rectangle panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        rectanglePanel.add(logoLabel, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 0, 0); // Added top inset for gap
        JLabel ipLabel = new JLabel("IP: ");
        ipLabel.setForeground(Color.WHITE);
        rectanglePanel.add(ipLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(10, 0, 0, 10); // Added top inset for gap
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(serverIPField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 0, 0); // Added top inset for gap
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setForeground(Color.WHITE);
        rectanglePanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(10, 0, 0, 10); // Added top inset for gap
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        rectanglePanel.add(loginButton, gbc);

        // Add rectangle panel to main panel
        mainPanel.add(rectanglePanel, new GridBagConstraints());

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void performLogin() {
        String serverIp = serverIPField.getText();
        String instanceName = "MSSQLSERVER02";
        String databaseName = "CinemaData";
        String username = "AdminCinema";
        String password = new String(passwordField.getPassword());

        String url = "jdbc:sqlserver://" + serverIp + "\\" + instanceName + ":1433;database=" + databaseName;
        String connectionString = url + ";user=" + username + ";password=" + password;

        try (Connection connection = DriverManager.getConnection(connectionString)) {
            System.out.println("Connected to the database.");
            new MovieList(connectionString).setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Password incorrect", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}