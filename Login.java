import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JPanel mainPanel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel logoLabel;

    public Login() {
        setTitle("Admin Login");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("Icons/WGLogo.png").getImage());

        // Main panel with background image
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("background.jpg").getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Password field
        passwordField = new JPasswordField(22);
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

        // Login button
        loginButton = new JButton("Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.DARK_GRAY);
        loginButton.addActionListener(e -> performLogin());

        // Logo label
        ImageIcon logoIcon = new ImageIcon("Icons/WGLogo.png");
        Image logoImage = logoIcon.getImage();
        logoLabel = (logoImage != null) 
                    ? new JLabel(new ImageIcon(logoImage.getScaledInstance(60, 55, Image.SCALE_SMOOTH))) 
                    : new JLabel("Logo not found");

        // Rectangle panel with semi-transparent background
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
        gbc.insets = new Insets(10, 10, 10, 10);
        rectanglePanel.add(logoLabel, gbc);
        gbc.gridy = 1;
        rectanglePanel.add(passwordField, gbc);
        gbc.gridy = 2;
        rectanglePanel.add(loginButton, gbc);

        // Add rectangle panel to main panel
        mainPanel.add(rectanglePanel, new GridBagConstraints());

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void performLogin() {
        String url = "jdbc:sqlserver://JoshiNitro5\\MSSQLSERVER02:1433;database=CinemaData";
        String username = "AdminCinema";
        String password = new String(passwordField.getPassword());

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to the database.");
            new MovieList(url, username, password).setVisible(true);
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