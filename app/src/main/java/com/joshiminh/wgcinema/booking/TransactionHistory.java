package com.joshiminh.wgcinema.booking;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.ResourceUtil;

public class TransactionHistory extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);

    public TransactionHistory(String url, String email) {
        setTitle("Your Ticket History");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setIconImage(ResourceUtil.loadAppIcon());

        JPanel transactionsPanel = createTransactionsPanel(url, email);
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
            while (rs != null && rs.next()) {
                hasRows = true;
                int movieId = rs.getInt("movie_id");
                try (ResultSet movieRs = DAO.fetchMovieDetails(url, movieId)) {
                    if (movieRs != null && movieRs.next()) {
                        panel.add(createTransactionEntryPanel(
                            movieRs.getString("title"),
                            movieRs.getString("poster"),
                            movieRs.getString("age_rating"),
                            rs.getString("transaction_date"),
                            rs.getString("amount"),
                            rs.getString("seats_preserved")
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

    private JPanel createTransactionEntryPanel(String movieTitle, String posterUrl, String ageRating, String transactionDate, String amount, String seatsPreserved) {
        JPanel entryPanel = new JPanel(new BorderLayout());
        entryPanel.setBackground(new Color(40, 40, 40));
        entryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        ));
        entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        entryPanel.add(createPosterPanel(movieTitle, posterUrl, ageRating), BorderLayout.WEST);
        entryPanel.add(createDetailsPanel(transactionDate, amount, seatsPreserved), BorderLayout.CENTER);
        return entryPanel;
    }

    private JPanel createPosterPanel(String movieTitle, String posterUrl, String ageRating) {
        JPanel posterPanel = new JPanel();
        posterPanel.setLayout(new BoxLayout(posterPanel, BoxLayout.Y_AXIS));
        posterPanel.setBackground(new Color(40, 40, 40));

        JLabel posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(80, 120));
        posterLabel.setMaximumSize(new Dimension(80, 120));
        if (posterUrl != null && !posterUrl.isEmpty()) {
            try {
                URL imageUrl = new URI(posterUrl).toURL();
                BufferedImage img = ImageIO.read(imageUrl);
                Image scaledImg = img.getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                posterLabel.setIcon(new ImageIcon(scaledImg));
            } catch (IOException | URISyntaxException e) {
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

        JLabel ageRatingLabel = new JLabel("Age Rating: " + ageRating);
        ageRatingLabel.setForeground(Color.LIGHT_GRAY);
        ageRatingLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        posterPanel.add(posterLabel);
        posterPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        posterPanel.add(titleLabel);
        posterPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        posterPanel.add(ageRatingLabel);

        return posterPanel;
    }

    private JPanel createDetailsPanel(String transactionDate, String amount, String seatsPreserved) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(40, 40, 40));

        JLabel dateLabel = new JLabel("Date: " + transactionDate);
        dateLabel.setForeground(Color.LIGHT_GRAY);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel seatsLabel = new JLabel("Seats: " + seatsPreserved);
        seatsLabel.setForeground(Color.LIGHT_GRAY);
        seatsLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel amountLabel = new JLabel("Amount: $" + amount);
        amountLabel.setForeground(new Color(0, 200, 0));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));

        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(seatsLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(amountLabel);

        return detailsPanel;
    }
}