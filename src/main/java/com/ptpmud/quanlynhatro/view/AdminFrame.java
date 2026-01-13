package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.TaiKhoan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
public class AdminFrame extends JFrame {

    private final TaiKhoan currentUser;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private JButton activeNavButton = null;
    private final List<JButton> navButtons = new ArrayList<>();
    private DashboardPanel dashboardPanel;
    private final com.ptpmud.quanlynhatro.service.PhongService phongService = new com.ptpmud.quanlynhatro.service.PhongService();
    private final com.ptpmud.quanlynhatro.service.HopDongService hopDongService = new com.ptpmud.quanlynhatro.service.HopDongService();
    private final com.ptpmud.quanlynhatro.service.HoaDonService hoaDonService = new com.ptpmud.quanlynhatro.service.HoaDonService();
    private final com.ptpmud.quanlynhatro.service.KhachHangService khachHangService = new com.ptpmud.quanlynhatro.service.KhachHangService();

    public AdminFrame(TaiKhoan user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý nhà trọ - Admin");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        // Sidebar menu
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        // Content area (cards)
        cards.setBackground(new Color(244, 246, 249));
        cards.setBorder(new EmptyBorder(16, 16, 16, 16));
        dashboardPanel = new DashboardPanel();
        cards.add(dashboardPanel, "dashboard");
        cards.add(new PhongFrame(), "phong");
        cards.add(new KhachHangFrame(), "khachhang");
        cards.add(new HopDongGD(), "hopdong");
        cards.add(new HoaDonGD(), "hoadon");
        cards.add(new DichVuGD(), "dichvu");
        cards.add(new ThuChiGD(), "thuChi");
        cards.add(new TaiKhoanGD(), "taikhoan");
        add(cards, BorderLayout.CENTER);
        // activate first nav if exists
        SwingUtilities.invokeLater(() -> {
            if (!navButtons.isEmpty()) {
                activateNav(navButtons.get(0));
            }
        });
    }

   
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(getWidth(), 64));
        header.setBackground(new Color(0, 104, 139));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        // Left: Title logo ở đây
        JLabel title = new JLabel("QUẢN LÝ NHÀ TRỌ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(255, 255, 255));
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(title, BorderLayout.WEST);
        // Right: icons + user
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        right.setOpaque(false);
        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        userBox.setOpaque(false);
        JLabel avatar = new JLabel("Xin chào, ");
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel name = new JLabel(currentUser == null ? "Admin" : currentUser.getHoTen());
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userBox.add(avatar);
        userBox.add(name);
        right.add(userBox);
        JButton btnLogout = new JButton("Thoát");
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Bạn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
            }
        });
        right.add(btnLogout);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ----------------------
    // Phần code menu sidebar
    // ----------------------
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(250, 252, 255));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        // logo
        JLabel logo = new JLabel();
        logo.setPreferredSize(new Dimension(240, 72));
        try {
            java.net.URL imgUrl = getClass().getResource("/images/logo.png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image scaledImg = icon.getImage().getScaledInstance(220,76, Image.SCALE_SMOOTH);
                logo.setIcon(new ImageIcon(scaledImg));
            } else {
                logo.setText("KMARoom");
                logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
                logo.setForeground(new Color(25, 118, 210));
            }
        } catch (Exception ex) {
            logo.setText("KMARoom");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            logo.setForeground(new Color(25, 118, 210));
        }
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setVerticalAlignment(SwingConstants.CENTER);
        sidebar.add(logo, BorderLayout.NORTH);
        // buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBorder(new EmptyBorder(12, 6, 12, 6));
        navButtons.add(makeNavButton("Dashboard", "dashboard", "/images/dashboard.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Quản lý Phòng", "phong", "/images/home.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Khách thuê", "khachhang", "/images/usergroup.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Hợp đồng", "hopdong", "/images/contract.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Hóa đơn", "hoadon", "/images/bill.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Dịch vụ", "dichvu", "/images/service.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Thu chi", "thuChi", "/images/thongke.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Tài khoản", "taikhoan", "/images/account.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Chat", "chat", "/images/chatbox.png"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        sidebar.add(new JScrollPane(btnPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        // footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 12, 12, 12));
        JLabel ver = new JLabel("© KMA - PTPMUD. All rights reserved ");
        ver.setForeground(new Color(120, 120, 120));
        footer.add(ver, BorderLayout.WEST);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton makeNavButton(String text, String cardName, String iconPath) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.putClientProperty("card", cardName);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(240, 44));
        btn.setBackground(new Color(250, 252, 255));
        btn.setForeground(new Color(45, 55, 72));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setHorizontalAlignment(SwingConstants.LEFT); // Căn lề trái

        // Thêm icon vào button
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            btn.setIcon(icon);
        } catch (Exception e) {
            // Nếu không tìm thấy icon, bỏ qua (hiển thị text thôi)
            System.out.println("Icon không tồn tại: " + iconPath);
        }

        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 17);
        try {
            Font currentFont = btn.getFont();
            btn.setFont(currentFont.deriveFont(Font.PLAIN, 17f));
        } catch (Exception e) {
            btn.setFont(defaultFont);
        }
        btn.setFocusPainted(false);
        btn.addActionListener(e -> activateNav(btn));
        Color hoverColor = new Color(237, 243, 255); // Màu nền khi rê chuột (nhạt)
        Color defaultColor = new Color(250, 252, 255); // Màu nền mặc định
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeNavButton) {
                    btn.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeNavButton) {
                    btn.setBackground(defaultColor);
                }
            }
        });
        return btn;
    }

    private void activateNav(JButton btn) {
        if (activeNavButton != null) {
            activeNavButton.setBackground(new Color(250, 252, 255));
            activeNavButton.setForeground(new Color(45, 55, 72));
        }
        btn.setBackground(new Color(25, 118, 210));
        btn.setForeground(Color.WHITE);
        activeNavButton = btn;
        String card = (String) btn.getClientProperty("card");
        
        // Xử lý mở Chat Frame thay vì chuyển card
        if ("chat".equals(card)) {
            openChatFrame();
            return;
        }
        
        cardLayout.show(cards, card);
        
        // Refresh dashboard data when dashboard tab is clicked
        if ("dashboard".equals(card) && dashboardPanel != null) {
            dashboardPanel.refreshData();
        }
    }
    
    /**
     * Mở/đóng chatbox (singleton)
     */
    private void openChatFrame() {
        String userName = currentUser.getTenDangNhap() + " [Admin]";
        ChatFrame.toggleChat(userName, true); // true = isAdmin
    }

    // ----------------------
    // Panels: Dashboard, Phong, KhachHang, HopDong, DichVu, TaiKhoan
    // ----------------------
    // --- Dashboard ---
    private class DashboardPanel extends JPanel {

        private final RevenueLineChart revenueChart;
        private final InvoiceStatusPieChart statusPieChart;
        private final RoomStatusBarChart roomBarChart;
        private final JPanel topCards;
        private final JLabel cardPhong, cardThue, cardKhach, cardRevenue;

        DashboardPanel() {
            setLayout(new BorderLayout(16, 16));
            setBackground(new Color(244, 246, 249));
            
            // Top cards với thông tin tổng quan - khởi tạo với placeholder
            topCards = new JPanel(new GridLayout(1, 4, 16, 16));
            topCards.setOpaque(false);
            
            JPanel card1 = makeCard("Tổng phòng", "...", new Color(23, 162, 184), "/images/room.png");
            JPanel card2 = makeCard("Cho thuê", "...", new Color(40, 167, 69), "/images/hd.png");
            JPanel card3 = makeCard("Khách hàng", "...", new Color(255, 193, 7), "/images/customer.png");
            JPanel card4 = makeCard("Doanh thu (tháng)", "...", new Color(26, 115, 232), "/images/dollar.png");
            
            cardPhong = (JLabel) card1.getComponent(1);
            cardThue = (JLabel) card2.getComponent(1);
            cardKhach = (JLabel) card3.getComponent(1);
            cardRevenue = (JLabel) card4.getComponent(1);
            
            topCards.add(card1);
            topCards.add(card2);
            topCards.add(card3);
            topCards.add(card4);
            
            add(topCards, BorderLayout.NORTH);
            
            // Load data asynchronously
            loadDashboardDataAsync();
            
            // Center: Charts
            JPanel center = new JPanel(new BorderLayout(16, 16));
            center.setOpaque(false);
            
            // Left: Revenue line chart
            revenueChart = new RevenueLineChart();
            center.add(revenueChart, BorderLayout.CENTER);
            
            // Right: Summary panel
            JPanel right = new JPanel(new BorderLayout(8, 8));
            right.setOpaque(false);
            right.setPreferredSize(new Dimension(300, 0));
            
            // Room status bar chart
            roomBarChart = new RoomStatusBarChart();
            right.add(roomBarChart, BorderLayout.NORTH);
            
            // Invoice status pie chart
            statusPieChart = new InvoiceStatusPieChart();
            right.add(statusPieChart, BorderLayout.CENTER);
            
            // Summary list
            right.add(new SummaryListPanel(), BorderLayout.SOUTH);
            
            center.add(right, BorderLayout.EAST);
            add(center, BorderLayout.CENTER);
        }
        
        private void loadDashboardDataAsync() {
            SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
                @Override
                protected DashboardData doInBackground() {
                    DashboardData data = new DashboardData();
                    data.totalPhong = phongService.getAll().size();
                    data.phongThue = phongService.countStatus("dangThue");
                    data.khCount = khachHangService.getAll().size();
                    data.revenue = hoaDonService.sumTongTienByMonth(
                        java.time.LocalDate.now().getMonthValue(), 
                        java.time.LocalDate.now().getYear()
                    );
                    return data;
                }
                
                @Override
                protected void done() {
                    try {
                        DashboardData data = get();
                        cardPhong.setText(String.valueOf(data.totalPhong));
                        cardThue.setText(String.valueOf(data.phongThue));
                        cardKhach.setText(String.valueOf(data.khCount));
                        cardRevenue.setText(formatMoney(data.revenue));
                        
                        // Start animations after data is loaded
                        revenueChart.start();
                        statusPieChart.start();
                        roomBarChart.start();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
        
        /**
         * Refresh dashboard data when tab is activated
         */
        public void refreshData() {
            loadDashboardDataAsync();
        }
        
        private static class DashboardData {
            int totalPhong, phongThue, khCount;
            double revenue;
        }


        private JPanel makeCard(String title, String value, Color color, String iconPath) {
            JPanel c = new JPanel(new BorderLayout(8, 8));
            c.setBackground(Color.WHITE);
            c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                        new EmptyBorder(16, 16, 16, 16)
            ));
            
            // Icon and title
            JPanel top = new JPanel(new BorderLayout(8, 4));
            top.setOpaque(false);
            JLabel iconLabel = new JLabel();
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                iconLabel.setText("⚠");
                iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            }
            JLabel t = new JLabel(title);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            t.setForeground(new Color(110, 120, 130));
            top.add(iconLabel, BorderLayout.WEST);
            top.add(t, BorderLayout.CENTER);
            
            // Value
            JLabel v = new JLabel(value);
            v.setFont(new Font("Segoe UI", Font.BOLD, 24));
            v.setForeground(color);
            
            c.add(top, BorderLayout.NORTH);
            c.add(v, BorderLayout.CENTER);
            
            return c;
        }

        private class SummaryListPanel extends JPanel {
            private final JLabel lblTotal, lblActive, lblDone;
            
            SummaryListPanel() {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setOpaque(false);
                setBorder(BorderFactory.createTitledBorder("Thống kê hợp đồng"));
                
                JPanel item1 = makeItem("Tổng hợp đồng", "...", new Color(100, 100, 100));
                JPanel item2 = makeItem("Đang thuê", "...", new Color(26, 115, 232));
                JPanel item3 = makeItem("Đã kết thúc", "...", new Color(234, 67, 53));
                
                lblTotal = (JLabel) item1.getComponent(1);
                lblActive = (JLabel) item2.getComponent(1);
                lblDone = (JLabel) item3.getComponent(1);
                
                add(item1);
                add(Box.createVerticalStrut(8));
                add(item2);
                add(Box.createVerticalStrut(8));
                add(item3);
                
                loadSummaryAsync();
            }
            
            private void loadSummaryAsync() {
                SwingWorker<SummaryData, Void> worker = new SwingWorker<>() {
                    @Override
                    protected SummaryData doInBackground() {
                        SummaryData data = new SummaryData();
                        data.hdActive = hopDongService.countByStatus("dangThue");
                        data.hdDone = hopDongService.countByStatus("daKetThuc");
                        data.totalHD = hopDongService.countAll();
                        return data;
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            SummaryData data = get();
                            lblTotal.setText(String.valueOf(data.totalHD));
                            lblActive.setText(String.valueOf(data.hdActive));
                            lblDone.setText(String.valueOf(data.hdDone));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();
            }
            
            private static class SummaryData {
                long hdActive, hdDone, totalHD;
            }

            private JPanel makeItem(String name, String count, Color color) {
                JPanel p = new JPanel(new BorderLayout(8, 8));
                p.setBackground(Color.WHITE);
                p.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(230, 230, 230)),
                            new EmptyBorder(10, 12, 10, 12)
                ));
                JLabel left = new JLabel(name);
                left.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                left.setForeground(new Color(70, 70, 70));
                p.add(left, BorderLayout.WEST);
                JLabel right = new JLabel(String.valueOf(count));
                right.setFont(new Font("Segoe UI", Font.BOLD, 14));
                right.setForeground(color);
                p.add(right, BorderLayout.EAST);
                return p;
            }
        }

        private String formatMoney(double v) {
            return String.format("₫%,.0f", v);
        }
    }

    
    
    // ----------------------
    // Utility UI pieces
    // ----------------------
    private JPanel actionPanel(ActionListener add, ActionListener edit, ActionListener del) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        left.setOpaque(false);
        JButton btnAdd = new JButton("➕ Thêm");
        btnAdd.addActionListener(add);
        JButton btnEdit = new JButton("✏ Sửa");
        btnEdit.addActionListener(edit);
        JButton btnDel = new JButton("🗑 Xóa");
        btnDel.addActionListener(del);
        left.add(btnAdd);
        left.add(btnEdit);
        left.add(btnDel);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        JTextField search = new JTextField(20);
        JButton btnSearch = new JButton("🔍 Tìm");
        right.add(search);
        right.add(btnSearch);
        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setRowHeight(36);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(new Color(248, 250, 252));
        t.getTableHeader().setForeground(new Color(40, 40, 40));
        t.setGridColor(new Color(230, 230, 230));
    }

    // FIXED: single helper to create placeholder panels
    private JPanel makePlaceholder(String caption, int height) {
        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(200, height));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        JLabel l = new JLabel(caption, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    // ----------------------
    // Revenue Line Chart - Doanh thu theo tháng
    // ----------------------
    private class RevenueLineChart extends JPanel {
        private java.util.Map<String, Double> revenueData = new java.util.LinkedHashMap<>();
        private Timer timer;
        private double progress = 0;

        RevenueLineChart() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230)),
                        new EmptyBorder(16, 16, 16, 16)
            ));
            
            // Load data asynchronously
            loadRevenueDataAsync();
            
            timer = new Timer(20, e -> {
                progress += 0.02;
                if (progress >= 1) {
                    progress = 1;
                    if (timer != null) {
                        timer.stop();
                    }
                }
                repaint();
            });
            setPreferredSize(new Dimension(700, 300));
        }
        
        private void loadRevenueDataAsync() {
            SwingWorker<java.util.Map<String, Double>, Void> worker = new SwingWorker<>() {
                @Override
                protected java.util.Map<String, Double> doInBackground() {
                    return hoaDonService.getRevenueByMonth(12);
                }
                
                @Override
                protected void done() {
                    try {
                        revenueData = get();
                        repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }

        void start() {
            progress = 0;
            if (timer != null) {
                timer.start();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            
            // Title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(new Color(40, 40, 40));
            g2.drawString("Doanh thu 12 tháng gần nhất", 12, 22);
            
            if (revenueData.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(150, 150, 150));
                g2.drawString("Chưa có dữ liệu", w / 2 - 50, h / 2);
                return;
            }
            
            // Chart area
            int left = 60, top = 50, right = w - 30, bottom = h - 50;
            int chartWidth = right - left;
            int chartHeight = bottom - top;
            
            // Background
            g2.setColor(new Color(250, 250, 250));
            g2.fillRect(left, top, chartWidth, chartHeight);
            
            // Find max value
            double maxValue = revenueData.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
            if (maxValue == 0) maxValue = 1;
            
            // Grid lines and labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(200, 200, 200));
            int gridLines = 5;
            for (int i = 0; i <= gridLines; i++) {
                int yy = top + i * chartHeight / gridLines;
                g2.drawLine(left, yy, right, yy);
                // Y-axis labels
                double value = maxValue * (1 - (double) i / gridLines);
                String label = formatMoneyShort(value);
                g2.setColor(new Color(120, 120, 120));
                g2.drawString(label, left - 55, yy + 4);
                g2.setColor(new Color(200, 200, 200));
            }
            
            // Draw data
            java.util.List<String> labels = new java.util.ArrayList<>(revenueData.keySet());
            int n = labels.size();
            if (n == 0) return;
            
            int[] xs = new int[n];
            int[] ys = new int[n];
            for (int i = 0; i < n; i++) {
                xs[i] = left + i * chartWidth / (n - 1);
                double val = revenueData.get(labels.get(i)) * progress;
                ys[i] = bottom - (int) ((bottom - top) * (val / maxValue));
            }
            
            // Fill area under curve
            Path2D area = new Path2D.Double();
            area.moveTo(xs[0], bottom);
            for (int i = 0; i < n; i++) {
                area.lineTo(xs[i], ys[i]);
            }
            area.lineTo(xs[n - 1], bottom);
            area.closePath();
            g2.setColor(new Color(26, 115, 232, 40));
            g2.fill(area);
            
            // Draw line
            g2.setColor(new Color(26, 115, 232));
            g2.setStroke(new BasicStroke(3f));
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
            }
            
            // Draw points
            g2.setColor(new Color(26, 115, 232));
            for (int i = 0; i < n; i++) {
                g2.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
                g2.setColor(Color.WHITE);
                g2.fillOval(xs[i] - 3, ys[i] - 3, 6, 6);
                g2.setColor(new Color(26, 115, 232));
            }
            
            // X-axis labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(new Color(100, 100, 100));
            for (int i = 0; i < n; i += 2) { // Show every other label
                String label = labels.get(i);
                int labelWidth = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, xs[i] - labelWidth / 2, bottom + 18);
            }
        }
        
        private String formatMoneyShort(double v) {
            if (v >= 1000000) {
                return String.format("₫%.1fM", v / 1000000);
            } else if (v >= 1000) {
                return String.format("₫%.0fK", v / 1000);
            }
            return String.format("₫%.0f", v);
        }
    }

    // ----------------------
    // Invoice Status Pie Chart - Trạng thái hóa đơn
    // ----------------------
    private class InvoiceStatusPieChart extends JPanel {
        private java.util.Map<String, Long> statusData = new java.util.LinkedHashMap<>();
        private final Color[] colors = {
            new Color(40, 167, 69),   // Đã thanh toán - xanh lá
            new Color(234, 67, 53),   // Chưa thanh toán - đỏ
            new Color(255, 193, 7)    // Khác - vàng
        };
        private double progress = 0;
        private Timer timer = null;

        InvoiceStatusPieChart() {
            setPreferredSize(new Dimension(260, 180));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230)),
                        new EmptyBorder(12, 12, 12, 12)
            ));
            
            loadStatusDataAsync();
            
            timer = new Timer(25, e -> {
                progress += 0.03;
                if (progress >= 1) {
                    progress = 1;
                    timer.stop();
                }
                repaint();
            });
        }
        
        private void loadStatusDataAsync() {
            SwingWorker<java.util.Map<String, Long>, Void> worker = new SwingWorker<>() {
                @Override
                protected java.util.Map<String, Long> doInBackground() {
                    return hoaDonService.getInvoiceStatusCount();
                }
                
                @Override
                protected void done() {
                    try {
                        statusData = get();
                        repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }

        void start() {
            progress = 0;
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(40, 40, 40));
            g2.drawString("Trạng thái hóa đơn", 8, 18);
            
            if (statusData.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(150, 150, 150));
                g2.drawString("Chưa có dữ liệu", 20, getHeight() / 2);
                return;
            }
            
            long total = statusData.values().stream().mapToLong(Long::longValue).sum();
            if (total == 0) return;
            
            int w = getWidth(), h = getHeight();
            int size = Math.min(w - 40, h - 60);
            int x = (w - size) / 2, y = 35;
            
            java.util.List<java.util.Map.Entry<String, Long>> entries = 
                new java.util.ArrayList<>(statusData.entrySet());
            
            double startAngle = 0;
            int colorIdx = 0;
            
            for (var entry : entries) {
                double percentage = entry.getValue() / (double) total;
                double angle = percentage * 360 * progress;
                
                g2.setColor(colors[colorIdx % colors.length]);
                Arc2D arc = new Arc2D.Double(x, y, size, size, startAngle, angle, Arc2D.PIE);
                g2.fill(arc);
                
                // Draw label
                double midAngle = Math.toRadians(startAngle + angle / 2);
                int labelX = (int) (x + size / 2 + Math.cos(midAngle) * size * 0.35);
                int labelY = (int) (y + size / 2 - Math.sin(midAngle) * size * 0.35);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String percent = String.format("%.0f%%", percentage * 100);
                int labelWidth = g2.getFontMetrics().stringWidth(percent);
                g2.drawString(percent, labelX - labelWidth / 2, labelY);
                
                startAngle += angle;
                colorIdx++;
            }
            
            // Legend
            int legendY = y + size + 15;
            int legendX = 10;
            colorIdx = 0;
            for (var entry : entries) {
                g2.setColor(colors[colorIdx % colors.length]);
                g2.fillRect(legendX, legendY, 12, 12);
                g2.setColor(new Color(70, 70, 70));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String label = entry.getKey().equals("daThanhToan") ? "Đã thanh toán" :
                              entry.getKey().equals("chuaThanhToan") ? "Chưa thanh toán" : entry.getKey();
                g2.drawString(label + " (" + entry.getValue() + ")", legendX + 16, legendY + 10);
                legendY += 18;
                colorIdx++;
            }
        }
    }

    // ----------------------
    // Room Status Bar Chart - Trạng thái phòng
    // ----------------------
    private class RoomStatusBarChart extends JPanel {
        private int trong = 0, dangThue = 0, baoTri = 0;
        private double progress = 0;
        private Timer timer = null;

        RoomStatusBarChart() {
            setPreferredSize(new Dimension(280, 150));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230)),
                        new EmptyBorder(12, 12, 12, 12)
            ));
            
            loadRoomStatusAsync();
            
            timer = new Timer(20, e -> {
                progress += 0.03;
                if (progress >= 1) {
                    progress = 1;
                    timer.stop();
                }
                repaint();
            });
        }
        
        private void loadRoomStatusAsync() {
            SwingWorker<RoomStatusData, Void> worker = new SwingWorker<>() {
                @Override
                protected RoomStatusData doInBackground() {
                    RoomStatusData data = new RoomStatusData();
                    data.trong = phongService.countStatus("trong");
                    data.dangThue = phongService.countStatus("dangThue");
                    data.baoTri = phongService.countStatus("baoTri");
                    return data;
                }
                
                @Override
                protected void done() {
                    try {
                        RoomStatusData data = get();
                        trong = data.trong;
                        dangThue = data.dangThue;
                        baoTri = data.baoTri;
                        repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
        
        private static class RoomStatusData {
            int trong, dangThue, baoTri;
        }

        void start() {
            progress = 0;
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(40, 40, 40));
            g2.drawString("Trạng thái phòng", 8, 18);
            
            int total = trong + dangThue + baoTri;
            if (total == 0) return;
            
            int w = getWidth() - 24;
            int h = getHeight() - 50;
            int barHeight = 25;
            int spacing = 10;
            int startY = 35;
            
            int maxValue = Math.max(Math.max(trong, dangThue), baoTri);
            if (maxValue == 0) maxValue = 1;
            
            // Draw bars
            drawBar(g2, 10, startY, w - 20, barHeight, trong, maxValue, 
                   new Color(23, 162, 184), "Trống", progress);
            drawBar(g2, 10, startY + barHeight + spacing, w - 20, barHeight, dangThue, maxValue,
                   new Color(40, 167, 69), "Đang thuê", progress);
            drawBar(g2, 10, startY + 2 * (barHeight + spacing), w - 20, barHeight, baoTri, maxValue,
                   new Color(255, 193, 7), "Bảo trì", progress);
        }
        
        private void drawBar(Graphics2D g2, int x, int y, int maxWidth, int height, 
                           int value, int maxValue, Color color, String label, double progress) {
            int barWidth = (int) (maxWidth * (value / (double) maxValue) * progress);
            
            // Bar
            g2.setColor(color);
            g2.fillRoundRect(x, y, barWidth, height, 5, 5);
            
            // Border
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, barWidth, height, 5, 5);
            
            // Label and value
            g2.setColor(new Color(70, 70, 70));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString(label, x + 5, y + height / 2 + 4);
            
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String valueStr = String.valueOf(value);
            int valueWidth = g2.getFontMetrics().stringWidth(valueStr);
            g2.drawString(valueStr, x + maxWidth - valueWidth - 5, y + height / 2 + 4);
        }
    }

    
    public static void main(String[] args) {

    }
}
