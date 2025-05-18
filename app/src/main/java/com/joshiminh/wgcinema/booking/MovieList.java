package com.joshiminh.wgcinema.booking;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.joshiminh.wgcinema.App;
import com.joshiminh.wgcinema.data.AgeRatingColor;
import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.*;

@SuppressWarnings("deprecation")
public class MovieList extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(24, 26, 32);
    private static final Color CARD_COLOR = new Color(36, 38, 46);
    private static final Color BUTTON_COLOR = new Color(0x1DB954);
    private static final Color TOP_BAR_COLOR = new Color(18, 18, 18);
    private static final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final int CARD_WIDTH = 260;
    private static final int CARD_HEIGHT = 520;
    private static final int CARDS_PER_ROW = 4;
    private static final int CARD_RADIUS = 24;

    private final String connectionString;
    private JPanel moviePanel;

    public MovieList(String connectionString) {
        this.connectionString = connectionString;
        initializeFrame();
        setupComponents();
        loadMovieList();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Now Showing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(ResourceUtil.loadAppIcon());
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                new App();
            }
        });
    }

    private void setupComponents() {
        add(createTopBar(), BorderLayout.NORTH);
        add(createMovieScrollPane(), BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(20, 0));
        topBar.setBackground(TOP_BAR_COLOR);
        topBar.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel title = createTitleLabel();
        JLabel logo = createLogoLabel();

        topBar.add(title, BorderLayout.WEST);
        topBar.add(logo, BorderLayout.EAST);
        return topBar;
    }

    private JLabel createTitleLabel() {
        JLabel title = new JLabel("Now Showing");
        title.setForeground(Color.WHITE);
        title.setFont(BASE_FONT.deriveFont(Font.PLAIN, 32f));
        return title;
    }

    private JLabel createLogoLabel() {
        JLabel logo = new JLabel();
        Image image = ResourceUtil.loadImage("/images/icon.png").getScaledInstance(60, 54, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(image));
        return logo;
    }

    private JScrollPane createMovieScrollPane() {
        moviePanel = new JPanel(new GridBagLayout());
        moviePanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(moviePanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        return scrollPane;
    }

    private void loadMovieList() {
        ResultSet resultSet = DAO.fetchUpcomingMovies(connectionString);
        if (resultSet == null) return;

        try {
            displayMovies(resultSet);
            resultSet.getStatement().getConnection().close();
        } catch (SQLException | java.net.MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void displayMovies(ResultSet resultSet) throws SQLException, java.net.MalformedURLException {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(24, 24, 24, 24);
        gbc.gridy = 0;
        gbc.gridx = 0;

        int col = 0;
        while (resultSet.next()) {
            JPanel movie = createMovieCard(
                resultSet.getInt("id"),
                resultSet.getString("poster"),
                resultSet.getString("title"),
                resultSet.getString("age_rating")
            );
            gbc.gridx = col++;
            moviePanel.add(movie, gbc);
            
            if (col == CARDS_PER_ROW) {
                col = 0;
                gbc.gridy++;
            }
        }
    }

    private JPanel createMovieCard(int movieId, String imageLink, String titleText, String rating) 
            throws java.net.MalformedURLException {
        JPanel card = new RoundedPanel(CARD_RADIUS, CARD_COLOR);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

        card.add(createPosterLabel(imageLink), BorderLayout.NORTH);
        card.add(createInfoPanel(titleText, rating), BorderLayout.CENTER);
        card.add(createBookButton(movieId), BorderLayout.SOUTH);

        return card;
    }

    private JLabel createPosterLabel(String imageLink) throws java.net.MalformedURLException {
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = new ImageIcon(new URL(imageLink));
        Image scaled = icon.getImage().getScaledInstance(CARD_WIDTH, (CARD_WIDTH * 3) / 2, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
        return imageLabel;
    }

    private JPanel createInfoPanel(String titleText, String rating) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        MultiLineLabel titleLabel = createTitleLabel(titleText);
        JLabel ratingLabel = createRatingLabel(rating);

        infoPanel.add(titleLabel);
        infoPanel.add(ratingLabel);
        return infoPanel;
    }

    private MultiLineLabel createTitleLabel(String titleText) {
        MultiLineLabel titleLabel = new MultiLineLabel(titleText, BASE_FONT, 2);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        titleLabel.setMaximumSize(new Dimension(CARD_WIDTH - 32, Integer.MAX_VALUE));
        return titleLabel;
    }

    private JLabel createRatingLabel(String rating) {
        JLabel ratingLabel = new JLabel(rating, SwingConstants.CENTER);
        ratingLabel.setForeground(AgeRatingColor.getColorForRating(rating));
        ratingLabel.setFont(BASE_FONT.deriveFont(Font.PLAIN, 16f));
        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ratingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return ratingLabel;
    }

    private JButton createBookButton(int movieId) {
        JButton bookButton = new JButton("Book Ticket");
        bookButton.setFont(BASE_FONT.deriveFont(Font.BOLD, 16f));
        bookButton.setForeground(Color.WHITE);
        bookButton.setBackground(BUTTON_COLOR);
        bookButton.setFocusPainted(false);
        bookButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bookButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookButton.addActionListener(_ -> new Booking(movieId, connectionString).setVisible(true));
        return bookButton;
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class MultiLineLabel extends JLabel {
        private final int maxLines;

        public MultiLineLabel(String text, Font font, int maxLines) {
            super(text);
            setFont(font);
            this.maxLines = maxLines;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont());
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            
            List<String> lines = calculateLines(fm);
            drawLines(g2, fm, lines);
            g2.dispose();
        }

        private List<String> calculateLines(FontMetrics fm) {
            int width = getWidth();
            String[] words = getText().split("\\s+");
            List<String> lines = new ArrayList<>();
            StringBuilder current = new StringBuilder();

            for (String word : words) {
                StringBuilder trial = new StringBuilder(current).append(word).append(" ");
                if (fm.stringWidth(trial.toString()) > width) {
                    lines.add(current.toString());
                    current = new StringBuilder(word).append(" ");
                } else {
                    current = trial;
                }
                if (lines.size() == maxLines) break;
            }

            if (lines.size() < maxLines && current.length() > 0) {
                lines.add(current.toString());
            }

            return finalizeLines(lines, fm);
        }

        private List<String> finalizeLines(List<String> lines, FontMetrics fm) {
            if (lines.size() > maxLines) {
                lines = lines.subList(0, maxLines);
            }
            if (lines.size() == maxLines) {
                String last = lines.get(maxLines - 1).trim();
                while (fm.stringWidth(last + "...") > getWidth() && last.length() > 0) {
                    last = last.substring(0, last.length() - 1);
                }
                lines.set(maxLines - 1, last + "...");
            }
            return lines;
        }

        private void drawLines(Graphics2D g2, FontMetrics fm, List<String> lines) {
            int lineHeight = fm.getHeight();
            int y = fm.getAscent();
            int width = getWidth();

            for (String line : lines) {
                String text = line.trim();
                int textWidth = fm.stringWidth(text);
                int x = (width - textWidth) / 2;
                g2.drawString(text, x, y);
                y += lineHeight;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(super.getPreferredSize().width, fm.getHeight() * maxLines);
        }
    }
}