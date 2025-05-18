package com.joshiminh.wgcinema.booking;

import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.ResourceUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Booking extends JFrame {
    private static final Color BG_COLOR = new Color(34, 40, 49);
    private static final Color ACCENT_COLOR = new Color(57, 162, 219);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font HEADER_FONT = BASE_FONT.deriveFont(Font.BOLD, 24f);
    private static final Font TAB_FONT = BASE_FONT.deriveFont(Font.BOLD, 16f);
    private static final Font BUTTON_FONT = BASE_FONT.deriveFont(Font.BOLD, 16f);
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("HH:mm");
    private static final Dimension BUTTON_SIZE = new Dimension(90, 40);
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 420;
    private static final int FLOW_GAP_H = 15;
    private static final int FLOW_GAP_V = 30;

    private final int movieId;
    private final String connStr;

    public Booking(int movieId, String connStr) {
        this.movieId = movieId;
        this.connStr = connStr;
        initFrame();
    }

    private void initFrame() {
        setTitle("Book Tickets");
        setIconImage(ResourceUtil.loadAppIcon());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setContentPane(buildMainPanel());
        setVisible(true);
    }

    private JPanel buildMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.add(createHeader(), BorderLayout.NORTH);
        panel.add(buildTabs(), BorderLayout.CENTER);
        return panel;
    }

    private JLabel createHeader() {
        JLabel header = new JLabel("Select Showtime", SwingConstants.CENTER);
        header.setFont(HEADER_FONT);
        header.setForeground(ACCENT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_COLOR);
        tabs.setForeground(ACCENT_COLOR);
        tabs.setFont(TAB_FONT);

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            Date date = cal.getTime();
            String title = i == 0 ? "Today" : cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            tabs.addTab(title, buildDayPanel(date, i == 0));
            cal.add(Calendar.DATE, 1);
        }
        return tabs;
    }

    private JPanel buildDayPanel(Date date, boolean isToday) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, FLOW_GAP_H, FLOW_GAP_V));
        panel.setBackground(BG_COLOR);
        loadShowtimes(panel, date, isToday);
        return panel;
    }

    private void loadShowtimes(JPanel container, Date date, boolean isToday) {
        try (ResultSet rs = DAO.fetchMovieShowtimes(connStr, movieId, DATE_FMT.format(date))) {
            Date now = new Date();
            while (rs != null && rs.next()) {
                String time = rs.getString("Time (HH:mm)");
                if (!isToday || TIME_FMT.parse(time).after(now)) {
                    container.add(createButton(time, rs.getInt("showtime_id")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JButton createButton(String text, int showtimeId) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setFont(BUTTON_FONT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        styleButton(btn);
        btn.addActionListener(e -> SwingUtilities.invokeLater(() -> new Showrooms(connStr, showtimeId)));
        return btn;
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(ACCENT_COLOR);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_COLOR);
            }
        });
    }
}