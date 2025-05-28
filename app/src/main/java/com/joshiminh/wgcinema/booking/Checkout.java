package com.joshiminh.wgcinema.booking;

import com.joshiminh.wgcinema.data.AgeRatingColor;
import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.ResourceUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

public class Checkout extends JFrame {
    private static final int WIDTH = 420, HEIGHT = 740;
    private static final int REGULAR_SEAT_PRICE = 80000, VIP_SEAT_PRICE = 85000;
    private final JLabel selectedSeatsLabel;
    private int showtimeID, showroomID, movieId;
    private Showrooms showroomsFrame;
    private boolean bookingSuccessful = false;
    private String connectionString;

    private static final String SMTP_EMAIL;
    private static final String SMTP_APP_PASSWORD;
    private static final String SERVICE_NAME;
    private static final Path USER_FILE_PATH;

    static {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(".env")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env file", e);
        }
        SMTP_EMAIL = props.getProperty("SMTP_EMAIL");
        SMTP_APP_PASSWORD = props.getProperty("SMTP_APP_PASSWORD");
        SERVICE_NAME = props.getProperty("SERVICE_NAME");
        USER_FILE_PATH = Paths.get(props.getProperty("USER_FILE", ""));
        if (SMTP_EMAIL == null || SMTP_APP_PASSWORD == null || SERVICE_NAME == null || USER_FILE_PATH == null) {
            throw new RuntimeException("Missing required SMTP or user file environment variables in .env");
        }
    }

    public Checkout(String connectionString, int showroomID, Time time, int movieId, Date date,
                    String movieTitle, String movieRating, String movieLink, int showtimeID,
                    String selectedSeats, Showrooms showroomsFrame) {
        this.showtimeID = showtimeID;
        this.showroomsFrame = showroomsFrame;
        this.showroomID = showroomID;
        this.movieId = movieId;
        this.connectionString = connectionString;
        setTitle("Checkout");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setBackground(new Color(24, 26, 32));
        setIconImage(ResourceUtil.loadAppIcon());

        JPanel northPanel = new JPanel(new GridBagLayout());
        northPanel.setBackground(new Color(24, 26, 32));
        northPanel.setPreferredSize(new Dimension(WIDTH, 150));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel moviePosterLabel = new JLabel();
        try {
            URL moviePosterUrl = new URL(movieLink);
            ImageIcon moviePosterIcon = new ImageIcon(moviePosterUrl);
            Image scaledMoviePosterImage = moviePosterIcon.getImage().getScaledInstance(90, 130, Image.SCALE_SMOOTH);
            moviePosterLabel.setIcon(new ImageIcon(scaledMoviePosterImage));
            moviePosterLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2, true));
        } catch (Exception ignored) {}
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 5; gbc.anchor = GridBagConstraints.NORTHWEST;
        northPanel.add(moviePosterLabel, gbc);
        gbc.gridheight = 1;

        JLabel movieLabel = new JLabel(movieTitle);
        movieLabel.setForeground(new Color(220, 220, 220));
        movieLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 1;
        northPanel.add(movieLabel, gbc);

        Color ratingColor = AgeRatingColor.getColorForRating(movieRating);
        JLabel ratingLabel = new JLabel(movieRating);
        ratingLabel.setForeground(ratingColor);
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridwidth = 1;
        northPanel.add(ratingLabel, gbc);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        JLabel dateLabel = new JLabel("Date: " + dateFormat.format(date));
        dateLabel.setForeground(new Color(180, 180, 180));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        northPanel.add(dateLabel, gbc);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        JLabel timeLabel = new JLabel("Time: " + timeFormat.format(time));
        timeLabel.setForeground(new Color(180, 180, 180));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        northPanel.add(timeLabel, gbc);

        JLabel showroomLabel = new JLabel("Showroom " + showroomID);
        showroomLabel.setForeground(new Color(180, 180, 180));
        showroomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        northPanel.add(showroomLabel, gbc);

        selectedSeatsLabel = new JLabel("Selected Seats: " + selectedSeats);
        selectedSeatsLabel.setForeground(new Color(220, 220, 220));
        selectedSeatsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
        northPanel.add(selectedSeatsLabel, gbc);

        add(northPanel, BorderLayout.NORTH);

        JButton bookButton = new JButton("CONFIRM");
        bookButton.setForeground(Color.WHITE);
        bookButton.setBackground(new Color(0, 120, 215));
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 26));
        bookButton.setFocusPainted(false);
        bookButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        bookButton.addActionListener(e -> book());

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(24, 26, 32));
        southPanel.setPreferredSize(new Dimension(50, 70));
        southPanel.add(bookButton, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setOpaque(false);

        JPanel northCenterPanel = new JPanel();
        northCenterPanel.setBackground(new Color(34, 36, 42));
        northCenterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 18));

        JLabel priceLabel = new JLabel("Total Price: " + calculateTotalPrice(selectedSeats));
        priceLabel.setForeground(new Color(0, 220, 120));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        northCenterPanel.add(priceLabel);
        innerPanel.add(northCenterPanel, BorderLayout.NORTH);

        ImageIcon qrIcon = new ImageIcon(ResourceUtil.loadImage("/images/QR.jpg"));
        JLabel qrLabel = new JLabel(qrIcon);
        qrLabel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        innerPanel.add(qrLabel, BorderLayout.CENTER);

        JPanel southCenterPanel = new JPanel();
        southCenterPanel.setBackground(new Color(34, 36, 42));
        southCenterPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel scanLabel = new JLabel("SCAN TO PAY");
        scanLabel.setForeground(new Color(0, 220, 120));
        scanLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        southCenterPanel.add(scanLabel);
        innerPanel.add(southCenterPanel, BorderLayout.SOUTH);

        add(innerPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (bookingSuccessful) {
                    showroomsFrame.dispose();
                    Showrooms newShowroomsFrame = new Showrooms(connectionString, showtimeID);
                    newShowroomsFrame.setVisible(true);
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static boolean checkBooked(String chairsBooked, String selectedSeats) {
        String[] bookedSeats = chairsBooked.split(" ");
        String[] selectedSeatArray = selectedSeats.split(" ");
        for (String selectedSeat : selectedSeatArray) {
            for (String bookedSeat : bookedSeats) {
                if (selectedSeat.equals(bookedSeat)) return true;
            }
        }
        return false;
    }

    public static String calculateTotalPrice(String selectedSeats) {
        int totalPrice = 0;
        String[] seats = selectedSeats.split(", ");
        for (String seat : seats) {
            char row = seat.charAt(0);
            if (row >= 'A' && row <= 'F') totalPrice += REGULAR_SEAT_PRICE;
            else if (row >= 'G' && row <= 'L') totalPrice += VIP_SEAT_PRICE;
        }
        return formatPrice(totalPrice);
    }

    private static String formatPrice(int price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return numberFormat.format(price) + " vnđ";
    }

    private void book() {
        try {
            String selectedSeats = selectedSeatsLabel.getText()
                .substring("Selected Seats: ".length()).replaceAll(",", "");

            ResultSet chairsResult = DAO.fetchShowtimeDetails(connectionString, showtimeID);
            if (chairsResult != null && chairsResult.next()) {
                String chairsBooked = chairsResult.getString("chairs_booked");
                if (checkBooked(chairsBooked, selectedSeats)) {
                    JOptionPane.showMessageDialog(this, "Some selected seats are already booked!", "Error", JOptionPane.ERROR_MESSAGE);
                    chairsResult.close();
                    return;
                }
            }
            if (chairsResult != null) chairsResult.close();

            int reservedCount = selectedSeats.split(" ").length;
            int updatedRows = DAO.updateShowtimeSeats(connectionString, reservedCount, selectedSeats, showtimeID);

            if (updatedRows > 0) {
                String userEmail = "";
                if (Files.exists(USER_FILE_PATH)) {
                    java.util.List<String> lines = Files.readAllLines(USER_FILE_PATH);
                    if (!lines.isEmpty()) userEmail = lines.get(0).trim();
                }

                int transactionId = DAO.insertTransactionReturnId(
                    connectionString, movieId, calculateTotalPrice(selectedSeats), selectedSeats,
                    showroomID, userEmail, showtimeID
                );

                String hashed = hashTransactionId(transactionId);

                BufferedImage qrImage = generateQrCode(hashed, 250, 250);

                sendEmailWithQr(userEmail, qrImage);

                JOptionPane.showMessageDialog(this, "Booking Successful! QR code sent to your email.", "Success", JOptionPane.INFORMATION_MESSAGE);
                showSuccessImage();
                bookingSuccessful = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String hashTransactionId(int id) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(String.valueOf(id).getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash transaction ID", e);
        }
    }

    private static BufferedImage generateQrCode(String data, int width, int height) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    private void sendEmailWithQr(String toEmail, BufferedImage qrImage) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_EMAIL, SMTP_APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_EMAIL, SERVICE_NAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Your Booking QR Code");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("Thank you for your booking! Please find your QR code attached.");

        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.attachFile(createTempImageFile(imageBytes));
        imagePart.setHeader("Content-ID", "<qr>");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(imagePart);
        message.setContent(multipart);

        Transport.send(message);
    }

    private File createTempImageFile(byte[] data) throws IOException {
        File temp = File.createTempFile("qr", ".png");
        try (FileOutputStream fos = new FileOutputStream(temp)) {
            fos.write(data);
        }
        return temp;
    }

    private void showSuccessImage() {
        getContentPane().removeAll();
        revalidate();
        repaint();
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(24, 26, 32));
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon(ResourceUtil.loadImage("/images/TicketBooked.png"));
        Image scaledImage = imageIcon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));
        centerPanel.add(imageLabel, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(24, 26, 32));
        revalidate();
        repaint();
    }
}