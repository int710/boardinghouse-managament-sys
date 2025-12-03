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
        cards.add(new DashboardPanel(), "dashboard");
        cards.add(new com.ptpmud.quanlynhatro.view.PhongPanel(), "phong");
        cards.add(new KhachHangPanel(), "khachhang");
        cards.add(new PhongFrame(), "hopdong");
        cards.add(new DichVuPanel(), "dichvu");
        cards.add(new TaiKhoanPanel(), "taikhoan");
        add(cards, BorderLayout.CENTER);
        // activate first nav if exists
        SwingUtilities.invokeLater(() -> {
            if (!navButtons.isEmpty()) {
                activateNav(navButtons.get(0));
            }
        });
    }
    // ----------------------
    //  HEADER Design ở đây
    // ----------------------
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
        JLabel logo = new JLabel("<html><b style='font-size:18px;color:#1976D2;'>KMARoom</b></html>", SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(240, 72));
        sidebar.add(logo, BorderLayout.NORTH);
        // buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBorder(new EmptyBorder(12, 6, 12, 6));
        navButtons.add(makeNavButton("Dashboard", "dashboard", "📈"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Quản lý Phòng", "phong", "🔑"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Khách thuê", "khachhang", "👥"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Hợp đồng", "hopdong", "📝"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Hóa đơn", "hoadon", "📄"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Dịch vụ", "dichvu", "💡"));
        btnPanel.add(navButtons.get(navButtons.size() - 1));
        btnPanel.add(Box.createVerticalStrut(6));
        navButtons.add(makeNavButton("Tài khoản", "taikhoan", "👤"));
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
    private JButton makeNavButton(String text, String cardName, String icon) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.putClientProperty("card", cardName);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(240, 44));
        btn.setBackground(new Color(250, 252, 255));
        btn.setForeground(new Color(45, 55, 72));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setHorizontalAlignment(SwingConstants.LEFT); // Căn lề trái
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
        cardLayout.show(cards, card);
    }
    // ----------------------
    // Panels: Dashboard, Phong, KhachHang, HopDong, DichVu, TaiKhoan
    // ----------------------
    // --- Dashboard ---
    private class DashboardPanel extends JPanel {
        private final AnimatedLineChart chart1;
        private final AnimatedPieChart pie;
        DashboardPanel() {
            setLayout(new BorderLayout(16, 16));
            setBackground(new Color(244, 246, 249));
            // top cards
            JPanel topCards = new JPanel(new GridLayout(1, 4, 16, 16));
            topCards.setOpaque(false);
            topCards.add(makeCard("Tổng phòng", "120", new Color(23, 162, 184)));
            topCards.add(makeCard("Đang thuê", "84", new Color(40, 167, 69)));
            topCards.add(makeCard("Khách hàng", "92", new Color(255, 193, 7)));
            topCards.add(makeCard("Doanh thu (tháng)", "₫125,000,000", new Color(26, 115, 232)));
            add(topCards, BorderLayout.NORTH);
            // center charts + right summary
            JPanel center = new JPanel(new BorderLayout(16, 16));
            center.setOpaque(false);
            JPanel charts = new JPanel(new GridLayout(2, 1, 12, 12));
            charts.setOpaque(false);
            chart1 = new AnimatedLineChart();
            charts.add(chart1);
            JPanel smallCharts = new JPanel(new GridLayout(1, 2, 12, 12));
            smallCharts.setOpaque(false);
            pie = new AnimatedPieChart();
            smallCharts.add(pie);
            // FIXED: replace missing PlaceholderPanel with makePlaceholder(...)
            smallCharts.add(makePlaceholder("Biểu đồ/Thông tin khác", 200));
            charts.add(smallCharts);
            center.add(charts, BorderLayout.CENTER);
            // right summary
            JPanel right = new JPanel(new BorderLayout(8, 8));
            right.setOpaque(false);
            right.add(new SummaryListPanel(), BorderLayout.CENTER);
            center.add(right, BorderLayout.EAST);
            add(center, BorderLayout.CENTER);
            // start animations
            chart1.start();
            pie.start();
        }
        private JPanel makeCard(String title, String value, Color color) {
            JPanel c = new JPanel(new BorderLayout());
            c.setBackground(Color.WHITE);
            c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230)),
                        new EmptyBorder(12, 12, 12, 12)
            ));
            JLabel t = new JLabel(title);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            t.setForeground(new Color(110, 120, 130));
            JLabel v = new JLabel(value);
            v.setFont(new Font("Segoe UI", Font.BOLD, 22));
            v.setForeground(color);
            c.add(t, BorderLayout.NORTH);
            c.add(v, BorderLayout.CENTER);
            return c;
        }
        private class SummaryListPanel extends JPanel {
            SummaryListPanel() {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setOpaque(false);
                add(makeItem("Văn bản", "9.10 GB", 274, new Color(26, 115, 232)));
                add(Box.createVerticalStrut(10));
                add(makeItem("Video", "12.30 GB", 45, new Color(234, 67, 53)));
                add(Box.createVerticalStrut(10));
                add(makeItem("Ảnh", "3.20 GB", 120, new Color(40, 167, 69)));
            }
            private JPanel makeItem(String name, String size, int count, Color color) {
                JPanel p = new JPanel(new BorderLayout(8, 8));
                p.setBackground(Color.WHITE);
                p.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(230, 230, 230)),
                            new EmptyBorder(10, 10, 10, 10)
                ));
                JLabel left = new JLabel(name);
                left.setFont(new Font("Segoe UI", Font.BOLD, 14));
                left.setForeground(color);
                p.add(left, BorderLayout.WEST);
                JLabel right = new JLabel(size + " • " + count + " mục");
                right.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                right.setForeground(new Color(90, 90, 90));
                p.add(right, BorderLayout.EAST);
                return p;
            }
        }
    }
    // --- Phòng Panel (table + actions) ---
    private class PhongPanel extends JPanel {
        private final DefaultTableModel model;
        private final JTable table;
        PhongPanel() {
            setLayout(new BorderLayout(12, 12));
            setBackground(new Color(244, 246, 249));
            JLabel title = new JLabel("Quản lý Phòng");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);
            model = new DefaultTableModel(new String[]{"ID", "Tên phòng", "Loại", "Diện tích", "Giá thuê", "Trạng thái"}, 0);
            table = new JTable(model);
            styleTable(table);
            // sample data (replace with DAO call)
            model.addRow(new Object[]{1, "P101", "Đơn", 20, "2,000,000", "Trống"});
            model.addRow(new Object[]{2, "P102", "Đôi", 28, "2,800,000", "Đã thuê"});
            add(new JScrollPane(table), BorderLayout.CENTER);
            JPanel actions = actionPanel((e) -> onAdd(), (e) -> onEdit(), (e) -> onDelete());
            add(actions, BorderLayout.SOUTH);
        }
        private void onAdd() {
            // TODO: open dialog to add room and call service (PhongService)
            JOptionPane.showMessageDialog(this, "Add room (tích hợp service ở đây).");
        }
        private void onEdit() {
            JOptionPane.showMessageDialog(this, "Edit room");
        }
        private void onDelete() {
            JOptionPane.showMessageDialog(this, "Delete room");
        }
    }
    // --- KhachHang Panel ---
    private class KhachHangPanel extends JPanel {
        private final DefaultTableModel model;
        KhachHangPanel() {
            setLayout(new BorderLayout(12, 12));
            setBackground(new Color(244, 246, 249));
            JLabel title = new JLabel("Quản lý Khách thuê");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);
            model = new DefaultTableModel(new String[]{"ID", "Họ tên", "SĐT", "CCCD", "Phòng", "Ngày sinh"}, 0);
            JTable table = new JTable(model);
            styleTable(table);
            model.addRow(new Object[]{1, "Nguyễn Văn A", "0909123456", "012345678", "P102", "2000-01-01"});
            model.addRow(new Object[]{2, "Trần Thị B", "0912345678", "987654321", "P101", "1995-05-10"});
            add(new JScrollPane(table), BorderLayout.CENTER);
            add(actionPanel((e) -> addKH(), (e) -> editKH(), (e) -> delKH()), BorderLayout.SOUTH);
        }
        private void addKH() {
            JOptionPane.showMessageDialog(this, "Thêm khách");
        }
        private void editKH() {
            JOptionPane.showMessageDialog(this, "Sửa khách");
        }
        private void delKH() {
            JOptionPane.showMessageDialog(this, "Xóa khách");
        }
    }
    // --- HopDong Panel ---
    private class HopDongPanel extends JPanel {
        private final DefaultTableModel model;
        HopDongPanel() {
            setLayout(new BorderLayout(12, 12));
            setBackground(new Color(244, 246, 249));
            JLabel title = new JLabel("Quản lý Hợp đồng");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);
            model = new DefaultTableModel(new String[]{"ID", "Phòng", "Khách", "Bắt đầu", "Kết thúc", "Tiền cọc", "Trạng thái"}, 0);
            JTable table = new JTable(model);
            styleTable(table);
            model.addRow(new Object[]{1, "P102", "Nguyễn Văn A", "2025-10-01", "", "1,000,000", "Đang thuê"});
            add(new JScrollPane(table), BorderLayout.CENTER);
            add(actionPanel((e) -> createContract(), (e) -> endContract(), (e) -> printContract()), BorderLayout.SOUTH);
        }
        private void createContract() {
            // TODO: implement create contract -> insert HopDongThue -> create user account for tenant (TaiKhoanService.register)
            JOptionPane.showMessageDialog(this, "Tạo hợp đồng -> tự cấp tài khoản user (TODO tích hợp service).");
        }
        private void endContract() {
            JOptionPane.showMessageDialog(this, "Kết thúc hợp đồng");
        }
        private void printContract() {
            JOptionPane.showMessageDialog(this, "In hợp đồng (PDF)");
        }
    }
    // --- DichVu Panel ---
    private class DichVuPanel extends JPanel {
        private final DefaultTableModel model;
        DichVuPanel() {
            setLayout(new BorderLayout(12, 12));
            setBackground(new Color(244, 246, 249));
            JLabel title = new JLabel("Quản lý Dịch vụ");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);
            model = new DefaultTableModel(new String[]{"ID", "Tên dịch vụ", "Đơn giá", "Ghi chú"}, 0);
            JTable table = new JTable(model);
            styleTable(table);
            model.addRow(new Object[]{1, "Wifi", "100,000", "Cố định"});
            model.addRow(new Object[]{2, "Giữ xe", "50,000", "Theo lượt"});
            add(new JScrollPane(table), BorderLayout.CENTER);
            add(actionPanel((e) -> addDV(), (e) -> editDV(), (e) -> delDV()), BorderLayout.SOUTH);
        }
        private void addDV() {
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ");
        }
        private void editDV() {
            JOptionPane.showMessageDialog(this, "Sửa dịch vụ");
        }
        private void delDV() {
            JOptionPane.showMessageDialog(this, "Xóa dịch vụ");
        }
    }
    // --- TaiKhoan Panel ---
    private class TaiKhoanPanel extends JPanel {
        private final DefaultTableModel model;
        TaiKhoanPanel() {
            setLayout(new BorderLayout(12, 12));
            setBackground(new Color(244, 246, 249));
            JLabel title = new JLabel("Quản lý Tài khoản");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);
            model = new DefaultTableModel(new String[]{"ID", "Tên đăng nhập", "Họ tên", "Vai trò", "Ngày tạo"}, 0);
            JTable table = new JTable(model);
            styleTable(table);
            model.addRow(new Object[]{1, "admin", "Quản trị hệ thống", "admin", "2025-11-24"});
            model.addRow(new Object[]{2, "user1", "Người thuê 1", "user", "2025-11-24"});
            add(new JScrollPane(table), BorderLayout.CENTER);
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton create = new JButton("Tạo tài khoản");
            create.addActionListener(e -> showCreateAccountDialog());
            bottom.add(create);
            add(bottom, BorderLayout.SOUTH);
        }
        private void showCreateAccountDialog() {
            JTextField user = new JTextField();
            JPasswordField pass = new JPasswordField();
            JTextField name = new JTextField();
            JComboBox<String> role = new JComboBox<>(new String[]{"admin", "user"});
            Object[] fields = {
                "Tên đăng nhập:", user,
                "Mật khẩu:", pass,
                "Họ tên:", name,
                "Vai trò:", role
            };
            int r = JOptionPane.showConfirmDialog(this, fields, "Tạo tài khoản", JOptionPane.OK_CANCEL_OPTION);
            if (r == JOptionPane.OK_OPTION) {
                // TODO: gọi TaiKhoanService.register(...) -> hash password -> insert
                model.addRow(new Object[]{model.getRowCount() + 1, user.getText(), name.getText(), role.getSelectedItem(), LocalDate.now().toString()});
                JOptionPane.showMessageDialog(this, "Đã tạo tài khoản (demo).");
            }
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
    // Simple animated line chart (demo) using Graphics2D
    // ----------------------
    private class AnimatedLineChart extends JPanel {
        private final int[] data = new int[12];
        private final Timer timer;
        private int step = 0;
        AnimatedLineChart() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230)),
                        new EmptyBorder(12, 12, 12, 12)
            ));
            // random data
            Random r = new Random();
            for (int i = 0; i < 12; i++) {
                data[i] = 20 + r.nextInt(140);
            }
            timer = new Timer(40, e -> {
                step++;
                if (step > 100) {
                    step = 100; // finish
                }
                repaint();
            });
            setPreferredSize(new Dimension(700, 260));
        }
        void start() {
            step = 0;
            timer.start();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            // title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.setColor(new Color(40, 40, 40));
            g2.drawString("Tài liệu cập nhật (demo)", 8, 18);
            // chart area
            int left = 40, top = 40, right = w - 20, bottom = h - 30;
            g2.setColor(new Color(245, 247, 249));
            g2.fillRect(left, top, right - left, bottom - top);
            // grid lines
            g2.setColor(new Color(230, 230, 230));
            for (int i = 0; i <= 5; i++) {
                int yy = top + i * (bottom - top) / 5;
                g2.drawLine(left, yy, right, yy);
            }
            // draw polyline based on data scaled
            int n = data.length;
            int max = 200;
            int[] xs = new int[n];
            int[] ys = new int[n];
            for (int i = 0; i < n; i++) {
                xs[i] = left + i * (right - left) / (n - 1);
                double val = data[i] * (step / 100.0);
                ys[i] = bottom - (int) ((bottom - top) * (val / max));
            }
            // fill under curve
            Path2D area = new Path2D.Double();
            area.moveTo(xs[0], bottom);
            for (int i = 0; i < n; i++) {
                area.lineTo(xs[i], ys[i]);
            }
            area.lineTo(xs[n - 1], bottom);
            area.closePath();
            g2.setColor(new Color(26, 115, 232, 60));
            g2.fill(area);
            // stroke line
            g2.setColor(new Color(26, 115, 232));
            g2.setStroke(new BasicStroke(2.4f));
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
            }
            // small dots
            g2.setColor(new Color(255, 255, 255));
            for (int i = 0; i < n; i++) {
                g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
            }
        }
    }
    // ----------------------
    // Simple animated pie chart
    // ----------------------
    private class AnimatedPieChart extends JPanel {
        private final double[] values = {0.45, 0.25, 0.18, 0.12};
        private final Color[] colors = {new Color(26, 115, 232), new Color(40, 167, 69), new Color(255, 193, 7), new Color(234, 67, 53)};
        private double progress = 0;
        private Timer timer = null;
        AnimatedPieChart() {
            setPreferredSize(new Dimension(300, 200));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230)),
                        new EmptyBorder(12, 12, 12, 12)
            ));
            timer = new Timer(30, e -> {
                progress += 0.03;
                if (progress >= 1) {
                    progress = 1;
                    timer.stop();
                }
                repaint();
            });
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
            int w = getWidth(), h = getHeight();
            int size = Math.min(w, h) - 40;
            int x = (w - size) / 2, y = (h - size) / 2;
            double a = 0;
            for (int i = 0; i < values.length; i++) {
                double ang = values[i] * 360 * progress;
                g2.setColor(colors[i]);
                Arc2D arc = new Arc2D.Double(x, y, size, size, a, ang, Arc2D.PIE);
                g2.fill(arc);
                a += ang;
            }
            // center hole
            g2.setColor(new Color(244, 246, 249));
            int hole = (int) (size * 0.45);
            g2.fillOval(x + (size - hole) / 2, y + (size - hole) / 2, hole, hole);
            // label
            g2.setColor(new Color(70, 70, 70));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString("Dung lượng", 10, 18);
        }
    }
    // ----------------------
    // main quick-run
    // ----------------------
    public static void main(String[] args) {
        // mock TaiKhoan (make sure your model has this constructor)
        TaiKhoan tk = new TaiKhoan(1, "admin", "hashed", "admin", "Quản trị hệ thống");
        SwingUtilities.invokeLater(() -> {
            AdminFrame f = new AdminFrame(tk);
            f.setVisible(true);
        });
    }
}