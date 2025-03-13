import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JPanel mainPanel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel logoLabel;
    private JTextField usernameField;

    public Login() {
        setTitle("Admin Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("icons/appIcon.png").getImage());

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("images/background.jpg").getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        passwordField = new JPasswordField(15);
        passwordField.setEchoChar('•');
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        usernameField = new JTextField("root", 15);

        loginButton = new JButton("Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.DARK_GRAY);
        loginButton.addActionListener(e -> performLogin());

        ImageIcon logoIcon = new ImageIcon("icons/appIcon.png");
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        rectanglePanel.add(logoLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setForeground(Color.WHITE);
        rectanglePanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setForeground(Color.WHITE);
        rectanglePanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rectanglePanel.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rectanglePanel.add(loginButton, gbc);

        mainPanel.add(rectanglePanel, new GridBagConstraints());
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void performLogin() {
        String url = "jdbc:mysql://localhost:3306/cinema_data";
        String dbUsername = usernameField.getText();
        String dbPassword = new String(passwordField.getPassword());

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            System.out.println("Login successful.");
            new MovieList(url + "?user=" + dbUsername + "&password=" + dbPassword).setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}