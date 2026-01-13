package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.service.FirebaseService;
import com.ptpmud.quanlynhatro.service.FirebaseService.ChatMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ChatFrame - Giao diện chat realtime với Firebase
 *
 * @author PTPMUD
 */
public class ChatFrame extends JPanel {

    private static ChatFrame instance;
    private static JFrame containerFrame;

    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField inputField;
    private JPanel pinnedMessagePanel;

    private String currentUserName;
    private boolean isAdmin;
    private FirebaseService firebaseService;
    private boolean firebaseInitialized = false;

    // Màu sắc
    private static final Color BG_COLOR = new Color(255, 255, 255);
    private static final Color MY_MSG_BG = new Color(54, 136, 199);
    private static final Color OTHER_MSG_BG = new Color(240, 242, 245);
    private static final Color SYSTEM_MSG_BG = new Color(255, 236, 229);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color PINNED_BG = new Color(255, 251, 235);

    // Font
    private static final Font MSG_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TIME_FONT = new Font("Segoe UI", Font.PLAIN, 11);

    // Kích thước
    private static final int WIDTH = 360;
    private static final int HEIGHT = 480;

    public static ChatFrame getInstance(String userName, boolean isAdmin) {
        if (instance == null) {
            instance = new ChatFrame(userName, isAdmin);
        } else {
            instance.currentUserName = userName;
            instance.isAdmin = isAdmin;
        }
        return instance;
    }

    public static void toggleChat(String userName, boolean isAdmin) {
        ChatFrame chat = getInstance(userName, isAdmin);
        if (containerFrame == null) {
            containerFrame = new JFrame();
            containerFrame.setUndecorated(true);
            containerFrame.setSize(WIDTH, HEIGHT);
            containerFrame.setResizable(false);

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            containerFrame.setLocation(screen.width - WIDTH - 20, screen.height - HEIGHT - 60);
            containerFrame.add(chat);
        }

        containerFrame.setVisible(!containerFrame.isVisible());
        if (containerFrame.isVisible()) {
            containerFrame.toFront();
            chat.inputField.requestFocus();
        }
    }

    private ChatFrame(String userName, boolean isAdmin) {
        this.currentUserName = userName != null ? userName : "User";
        this.isAdmin = isAdmin;
        this.firebaseService = FirebaseService.getInstance();

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Top: Header + Pinned
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(createHeader());
        pinnedMessagePanel = createPinnedPanel();
        topPanel.add(pinnedMessagePanel);
        add(topPanel, BorderLayout.NORTH);

        add(createChatArea(), BorderLayout.CENTER);
        add(createInputArea(), BorderLayout.SOUTH);

        initFirebase();
    }

    /**
     * Header nhỏ gọn
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 132, 255),
                            getWidth(), 0, new Color(0, 100, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 10));
        header.setPreferredSize(new Dimension(WIDTH, 38));

        // Title + Status
        JLabel title = new JLabel("Chatbox Xóm Trọ KMA");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(Color.WHITE);

        JLabel status = new JLabel("  ● Trực tuyến");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setForeground(new Color(100, 255, 100));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(title);
        leftPanel.add(status);

        // Close button with image
        JButton closeBtn = new JButton();
        try {
            ImageIcon closeIcon = new ImageIcon(getClass().getResource("/images/close.png"));
            Image scaled = closeIcon.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH);
            closeBtn.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            closeBtn.setText("✕");
            closeBtn.setForeground(Color.WHITE);
        }
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(24, 24));
        closeBtn.addActionListener(e -> containerFrame.setVisible(false));

        header.add(leftPanel, BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);

        // Drag để di chuyển
        final Point[] drag = {null};
        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                drag[0] = e.getPoint();
            }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (containerFrame != null && drag[0] != null) {
                    Point p = containerFrame.getLocation();
                    containerFrame.setLocation(p.x + e.getX() - drag[0].x, p.y + e.getY() - drag[0].y);
                }
            }
        });

        return header;
    }

    private JPanel createPinnedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PINNED_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        panel.setVisible(false);
        return panel;
    }

    private void showPinnedMessage(String content) {
        if (content == null || content.isEmpty()) {
            pinnedMessagePanel.setVisible(false);
            return;
        }

        pinnedMessagePanel.removeAll();
        pinnedMessagePanel.setVisible(true);

        // Load pin icon
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        contentPanel.setOpaque(false);

        try {
            ImageIcon pinIcon = new ImageIcon(getClass().getResource("/images/pinMessage.png"));
            Image scaled = pinIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaled));
            contentPanel.add(iconLabel);
        } catch (Exception e) {
            contentPanel.add(new JLabel("[Ghim]"));
        }

        JLabel msg = new JLabel(content);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contentPanel.add(msg);
        pinnedMessagePanel.add(contentPanel, BorderLayout.CENTER);

        if (isAdmin) {
            JButton close = new JButton("×");
            close.setFont(new Font("Segoe UI", Font.BOLD, 16));
            close.setBorderPainted(false);
            close.setContentAreaFilled(false);
            close.setCursor(new Cursor(Cursor.HAND_CURSOR));
            close.addActionListener(e -> firebaseService.clearPinnedMessage());
            pinnedMessagePanel.add(close, BorderLayout.EAST);
        }

        pinnedMessagePanel.revalidate();
        pinnedMessagePanel.repaint();
    }

    private JScrollPane createChatArea() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(BG_COLOR);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setBorder(null);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return chatScrollPane;
    }

    private JPanel createInputArea() {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        inputField.addActionListener(e -> sendMessage());

        // Send button with image
        JButton sendBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sendBtn.setText("Gửi");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setBackground(new Color(0, 132, 255));
        sendBtn.setBorderPainted(false);
        sendBtn.setFocusPainted(false);
        sendBtn.setContentAreaFilled(false);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.setPreferredSize(new Dimension(60, 34));
        sendBtn.setMinimumSize(new Dimension(60, 34));
        sendBtn.addActionListener(e -> sendMessage());

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.EAST);

        return panel;
    }

    private void initFirebase() {
        new Thread(() -> {
            try {
                if (!firebaseService.isInitialized()) {
                    firebaseService.initialize(
                "https://chat-qlnt-default-rtdb.asia-southeast1.firebasedatabase.app/"
                    );
                }
                firebaseService.listenForMessages(this::displayMessage);
                firebaseService.listenForPinnedMessage(this::showPinnedMessage);
                firebaseInitialized = true;
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> addSystemMsg("Lỗi kết nối: " + e.getMessage()));
            }
        }).start();
    }

    private void sendMessage() {
        String content = inputField.getText().trim();
        if (content.isEmpty()) {
            return;
        }

        if (isAdmin && content.startsWith("/")) {
            handleCommand(content);
            inputField.setText("");
            return;
        }

        if (!firebaseInitialized) {
            addSystemMsg("Đang kết nối...");
            return;
        }

        firebaseService.sendMessage(currentUserName, content);
        inputField.setText("");
    }

    private void handleCommand(String cmd) {
        String[] parts = cmd.split(" ", 2);
        switch (parts[0].toLowerCase()) {
            case "/pin":
                if (parts.length > 1) {
                    firebaseService.setPinnedMessage(parts[1]);
                    firebaseService.sendMessage("[HỆ THỐNG]", "[Ghim] Admin đã ghim: " + parts[1]);
                }
                break;
            case "/unpin":
                firebaseService.clearPinnedMessage();
                firebaseService.sendMessage("[HỆ THỐNG]", "Đã bỏ ghim tin nhắn");
                break;
        }
    }

    /**
     * Hiển thị tin nhắn từ Firebase với thời gian thật
     */
    private void displayMessage(ChatMessage msg) {
        SwingUtilities.invokeLater(() -> {
            boolean isMe = msg.getUser().equals(currentUserName);
            boolean isSystem = msg.getUser().contains("[HỆ THỐNG]") || msg.getUser().contains("[System]");

            // Wrapper căn lề
            JPanel wrapper = new JPanel(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
            wrapper.setOpaque(false);
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setOpaque(false);

            // Thời gian thật từ Firebase
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(new Date(msg.getTimestamp()));

            // Header: Tên + Thời gian
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            header.setOpaque(false);

            if (!isMe) {
                JLabel name = new JLabel(msg.getUser());
                name.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                name.setForeground(isSystem ? new Color(220, 80, 80) : new Color(0, 120, 215));
                header.add(name);
                header.add(new JLabel(" "));
            }

            JLabel timeLabel = new JLabel(time);
            timeLabel.setFont(TIME_FONT);
            timeLabel.setForeground(new Color(150, 150, 150));
            header.add(timeLabel);

            container.add(header);
            container.add(Box.createRigidArea(new Dimension(0, 2)));

            // Bubble
            Color bg = isSystem ? SYSTEM_MSG_BG : (isMe ? MY_MSG_BG : OTHER_MSG_BG);
            Color fg = isMe && !isSystem ? Color.WHITE : new Color(40, 40, 40);

            JPanel bubble = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.dispose();
                }
            };
            bubble.setOpaque(false);
            bubble.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            JTextArea text = new JTextArea(msg.getContent());
            text.setFont(MSG_FONT);
            text.setForeground(fg);
            text.setOpaque(false);
            text.setEditable(false);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);

            bubble.add(text);
            container.add(bubble);

            wrapper.add(container);
            chatPanel.add(wrapper);
            chatPanel.add(Box.createRigidArea(new Dimension(0, 6)));

            chatPanel.revalidate();
            SwingUtilities.invokeLater(() -> {
                JScrollBar sb = chatScrollPane.getVerticalScrollBar();
                sb.setValue(sb.getMaximum());
            });
        });
    }

    private void addSystemMsg(String text) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            label.setForeground(new Color(140, 140, 140));
            label.setAlignmentX(CENTER_ALIGNMENT);

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.add(label);

            chatPanel.add(panel);
            chatPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            chatPanel.revalidate();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> toggleChat("Test User", false));
    }
}
