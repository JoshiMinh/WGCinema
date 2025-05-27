package com.joshiminh.wgcinema.booking;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.*;

public class TransactionHistory extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);

    public TransactionHistory(String url, String email) {
        setTitle("Your Ticket History");
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setIconImage(ResourceUtil.loadAppIcon());

        JPanel transactionsPanel = createTransactionsPanel(url, email);
        transactionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        transactionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(transactionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setPreferredSize(new Dimension(470, 570));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        setContentPane(scrollPane);
    }

    private JPanel createTransactionsPanel(String url, String email) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);

        try (ResultSet rs = DAO.fetchTransactionHistory(url, email)) {
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                int movieId = rs.getInt("movie_id");
                try (ResultSet movieRs = DAO.fetchMovieDetails(url, movieId)) {
                    if (movieRs.next()) {
                        panel.add(createTransactionEntryPanel(
                            movieRs.getString("title"),
                            movieRs.getString("poster")
                        ));
                        panel.add(Box.createRigidArea(new Dimension(0, 10)));
                    }
                }
            }
            if (!hasRows) {
                JLabel emptyLabel = new JLabel("No transactions found.");
                emptyLabel.setForeground(Color.LIGHT_GRAY);
                panel.add(emptyLabel);
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading transactions: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            panel.add(errorLabel);
        }
        return panel;
    }

    private JPanel createTransactionEntryPanel(String movieTitle, String posterUrl) {
        JPanel entryPanel = new JPanel(new BorderLayout());
        entryPanel.setBackground(new Color(40, 40, 40));
        entryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        ));
        entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        entryPanel.add(createPosterPanel(movieTitle, posterUrl), BorderLayout.WEST);
        entryPanel.add(createButtonPanel(), BorderLayout.EAST);
        return entryPanel;
    }

    @SuppressWarnings("deprecation")
    private JPanel createPosterPanel(String movieTitle, String posterUrl) {
        JPanel posterPanel = new JPanel();
        posterPanel.setLayout(new BoxLayout(posterPanel, BoxLayout.Y_AXIS));
        posterPanel.setBackground(new Color(40, 40, 40));

        JLabel posterLabel = new JLabel();
        posterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        posterLabel.setPreferredSize(new Dimension(80, 120));
        posterLabel.setMaximumSize(new Dimension(80, 120));
        if (posterUrl != null && !posterUrl.isEmpty()) {
            try {
                BufferedImage img = ImageIO.read(new URL(posterUrl));
                Image scaledImg = img.getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                posterLabel.setIcon(new ImageIcon(scaledImg));
            } catch (IOException e) {
                posterLabel.setText("[No Image]");
                posterLabel.setForeground(Color.LIGHT_GRAY);
            }
        } else {
            posterLabel.setText("[No Image]");
            posterLabel.setForeground(Color.LIGHT_GRAY);
        }

        JLabel titleLabel = new JLabel(movieTitle);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        posterPanel.add(posterLabel);
        posterPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        posterPanel.add(titleLabel);

        return posterPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(40, 40, 40));

        JButton qrButton = new JButton("Download QR");
        qrButton.setBackground(new Color(0, 123, 255));
        qrButton.setForeground(Color.WHITE);
        qrButton.setFont(new Font("Arial", Font.BOLD, 12));
        qrButton.setFocusPainted(false);
        qrButton.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        qrButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonPanel.add(qrButton);

        return buttonPanel;
    }
}