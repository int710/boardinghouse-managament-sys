package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.HoaDon;
import com.ptpmud.quanlynhatro.model.HopDong;
import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.model.TaiKhoan;
import com.ptpmud.quanlynhatro.service.HoaDonService;
import com.ptpmud.quanlynhatro.service.HopDongService;
import com.ptpmud.quanlynhatro.service.KhachHangService;
import com.ptpmud.quanlynhatro.service.PhongService;
import com.ptpmud.quanlynhatro.service.TaiKhoanService;
import com.ptpmud.quanlynhatro.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Giao diện đầy đủ cho người thuê: xem hợp đồng, hóa đơn, thông tin phòng, đổi
 * mật khẩu
 */
public class UserFrame extends JFrame {

    private final TaiKhoan tk;
    private final HoaDonService hoaDonService = new HoaDonService();
    private final HopDongService hopDongService = new HopDongService();
    private final PhongService phongService = new PhongService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final KhachHangService khachHangService = new KhachHangService();

    private Integer idPhongBound = null;
    private Integer idKhachHang = null;
    private HopDong currentContract = null;
    private Phong currentRoom = null;

    private JTabbedPane tabbedPane;
    private DefaultTableModel invoiceModel;
    private JTable invoiceTable;
    private JLabel lblRoomInfo, lblContractInfo, lblUserInfo;

    public UserFrame(TaiKhoan tk) {
        this.tk = tk;
        setTitle("Hệ thống quản lý nhà trọ - Người thuê");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        resolveUserInfo();
        initUI();
        loadAllData();
    }

    private void resolveUserInfo() {
        Integer khId = taiKhoanService.getKhachHangIdFromTaiKhoan(tk);
        
        if (khId == null || khId <= 0) {
            List<KhachHang> allKhachHang = khachHangService.getAll();
            for (KhachHang kh : allKhachHang) {
                if (kh.getEmail() != null && kh.getEmail().equalsIgnoreCase(tk.getTenDangNhap())) {
                    khId = kh.getIdKhachHang();
                    taiKhoanService.updateKhachHangLink(tk.getIdTaiKhoan(), khId);
                    tk.setIdKhachHang(khId);
                    break;
                }
            }
        }
        
        if ((khId == null || khId <= 0) && tk.getHoTen() != null) {
            List<KhachHang> allKhachHang = khachHangService.getAll();
            for (KhachHang kh : allKhachHang) {
                if (kh.getTenKhachHang() != null && kh.getTenKhachHang().equals(tk.getHoTen())) {
                    khId = kh.getIdKhachHang();
                    taiKhoanService.updateKhachHangLink(tk.getIdTaiKhoan(), khId);
                    tk.setIdKhachHang(khId);
                    break;
                }
            }
        }
        
        if (khId != null && khId > 0) {
            idKhachHang = khId;
            currentContract = hopDongService.findActiveByKhachHang(khId);
            if (currentContract != null) {
                idPhongBound = currentContract.getIdPhong();
                currentRoom = phongService.findById(idPhongBound);
            }
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 118, 210));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Xin chào, " + (tk.getHoTen() != null ? tk.getHoTen() : tk.getTenDangNhap()));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Bạn có muốn đăng xuất?", "Xác nhận",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        // Thêm nút Chat
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);
        JButton btnChat = new JButton("💬 Chat");
        btnChat.setBackground(new Color(76, 175, 80));
        btnChat.setForeground(Color.WHITE);
        btnChat.setFocusPainted(false);
        btnChat.setBorderPainted(false);
        btnChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChat.addActionListener(e -> openChatFrame());
        headerRight.add(btnChat);
        headerRight.add(btnLogout);
        header.add(headerRight, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📄 Hợp đồng", createContractPanel());
        tabbedPane.addTab("💰 Hóa đơn", createInvoicePanel());
        tabbedPane.addTab("🏠 Thông tin phòng", createRoomInfoPanel());
        tabbedPane.addTab("⚙️ Tài khoản", createAccountPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createContractPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Contract info
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(new TitledBorder("Thông tin hợp đồng"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;

        lblContractInfo = new JLabel("<html>Đang tải...</html>");
        infoPanel.add(lblContractInfo, c);

        panel.add(infoPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.addActionListener(e -> loadContractInfo());
        btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInvoicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Lọc theo:"));
        JComboBox<String> cbFilter = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Chưa thanh toán"});
        filterPanel.add(cbFilter);
        JButton btnFilter = new JButton("Lọc");
        btnFilter.addActionListener(e -> loadInvoices((String) cbFilter.getSelectedItem()));
        filterPanel.add(btnFilter);
        panel.add(filterPanel, BorderLayout.NORTH);

        // Invoice table
        invoiceModel = new DefaultTableModel(new String[]{
            "Tháng", "Năm", "Tiền phòng", "Điện", "Nước", "Dịch vụ", "Khác", "Tổng", "Trạng thái", "Ngày tạo"
        }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        invoiceTable = new JTable(invoiceModel);
        invoiceTable.setRowHeight(28);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnViewDetail = new JButton("📋 Xem chi tiết");
        btnViewDetail.addActionListener(e -> viewInvoiceDetail());
        JButton btnExportPDF = new JButton("📄 Xuất PDF");
        btnExportPDF.addActionListener(e -> exportInvoicePDF());
        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.addActionListener(e -> loadInvoices("Tất cả"));
        btnPanel.add(btnViewDetail);
        btnPanel.add(btnExportPDF);
        btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRoomInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        lblRoomInfo = new JLabel("<html>Đang tải...</html>");
        lblRoomInfo.setBorder(new TitledBorder("Thông tin phòng"));
        panel.add(lblRoomInfo, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.addActionListener(e -> loadRoomInfo());
        btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // User info
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(new TitledBorder("Thông tin tài khoản"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;

        lblUserInfo = new JLabel("<html>Đang tải...</html>");
        infoPanel.add(lblUserInfo, c);
        panel.add(infoPanel, BorderLayout.NORTH);

        // Change password panel
        JPanel pwdPanel = new JPanel(new GridBagLayout());
        pwdPanel.setBorder(new TitledBorder("Đổi mật khẩu"));
        c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        pwdPanel.add(new JLabel("Mật khẩu cũ:"), c);
        JPasswordField txtOldPwd = new JPasswordField(20);
        c.gridx = 1;
        pwdPanel.add(txtOldPwd, c);

        c.gridx = 0;
        c.gridy++;
        pwdPanel.add(new JLabel("Mật khẩu mới:"), c);
        JPasswordField txtNewPwd = new JPasswordField(20);
        c.gridx = 1;
        pwdPanel.add(txtNewPwd, c);

        c.gridx = 0;
        c.gridy++;
        pwdPanel.add(new JLabel("Xác nhận mật khẩu:"), c);
        JPasswordField txtConfirmPwd = new JPasswordField(20);
        c.gridx = 1;
        pwdPanel.add(txtConfirmPwd, c);

        JButton btnChangePwd = new JButton("Đổi mật khẩu");
        btnChangePwd.addActionListener(e -> {
            String oldPwd = new String(txtOldPwd.getPassword());
            String newPwd = new String(txtNewPwd.getPassword());
            String confirmPwd = new String(txtConfirmPwd.getPassword());

            // Validate UI cơ bản
            if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                JOptionPane.showMessageDialog(
                            this,
                            "Vui lòng điền đầy đủ thông tin!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (newPwd.length() < 6) {
                JOptionPane.showMessageDialog(
                            this,
                            "Mật khẩu mới phải có ít nhất 6 ký tự!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                boolean success = taiKhoanService.changePassword(
                            tk.getIdTaiKhoan(), // user hiện tại
                            oldPwd,
                            newPwd,
                            confirmPwd
                );

                if (success) {
                    JOptionPane.showMessageDialog(
                                this,
                                "Đổi mật khẩu thành công",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE
                    );

                    // Reset form
                    txtOldPwd.setText("");
                    txtNewPwd.setText("");
                    txtConfirmPwd.setText("");
                    txtOldPwd.requestFocus();
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                            this,
                            ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                );
            }
        });

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        pwdPanel.add(btnChangePwd, c);

        panel.add(pwdPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadAllData() {
        loadContractInfo();
        loadRoomInfo();
        loadUserInfo();
        loadInvoices("Tất cả");
    }

    private void loadContractInfo() {
        if (idKhachHang == null) {
            lblContractInfo.setText("<html><font color='red'>Không tìm thấy thông tin khách hàng liên kết với tài khoản này.<br/>" +
                "Vui lòng liên hệ quản trị viên để cập nhật thông tin.</font></html>");
            return;
        }
        
        if (currentContract == null) {
            lblContractInfo.setText("<html><font color='red'>Không có hợp đồng đang hoạt động.<br/>" +
                "Nếu bạn đang thuê phòng, vui lòng liên hệ quản trị viên.</font></html>");
            return;
        }

        KhachHang kh = khachHangService.findById(currentContract.getIdKhachHang());
        LocalDate bd = currentContract.getNgayBatDauAsLocal();
        LocalDate kt = currentContract.getNgayKetThucAsLocal();
        long daysLeft = kt != null ? ChronoUnit.DAYS.between(LocalDate.now(), kt) : -1;

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<table cellpadding='5'>");
        sb.append("<tr><td><b>ID Hợp đồng:</b></td><td>").append(currentContract.getIdHopDong()).append("</td></tr>");
        sb.append("<tr><td><b>Khách hàng:</b></td><td>").append(kh != null ? kh.getTenKhachHang() : "N/A").append("</td></tr>");
        sb.append("<tr><td><b>Phòng:</b></td><td>").append(currentRoom != null ? currentRoom.getTenPhong() : "N/A").append("</td></tr>");
        sb.append("<tr><td><b>Ngày bắt đầu:</b></td><td>").append(bd != null ? bd.toString() : "N/A").append("</td></tr>");
        sb.append("<tr><td><b>Ngày kết thúc:</b></td><td>").append(kt != null ? kt.toString() : "Không xác định").append("</td></tr>");
        if (daysLeft >= 0) {
            sb.append("<tr><td><b>Còn lại:</b></td><td><font color='").append(daysLeft < 30 ? "red" : "green").append("'>")
                        .append(daysLeft).append(" ngày</font></td></tr>");
        }
        sb.append("<tr><td><b>Tiền cọc:</b></td><td>").append(formatMoney(currentContract.getTienCoc())).append(" VNĐ</td></tr>");
        sb.append("<tr><td><b>Trạng thái:</b></td><td>").append(currentContract.getTrangThai()).append("</td></tr>");
        sb.append("</table></html>");

        lblContractInfo.setText(sb.toString());
    }

    private void loadRoomInfo() {
        if (currentRoom == null) {
            lblRoomInfo.setText("<html><font color='red'>Không có thông tin phòng.<br/>" +
                "Nếu bạn đang thuê phòng, vui lòng liên hệ quản trị viên.</font></html>");
            return;
        }

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<table cellpadding='5'>");
        sb.append("<tr><td><b>Tên phòng:</b></td><td>").append(currentRoom.getTenPhong()).append("</td></tr>");
        double dienTich = currentRoom.getDienTich();
        sb.append("<tr><td><b>Diện tích:</b></td><td>").append(dienTich > 0 ? dienTich + " m²" : "N/A").append("</td></tr>");
        sb.append("<tr><td><b>Giá thuê:</b></td><td>").append(formatMoney(currentRoom.getGiaThue())).append(" VNĐ/tháng</td></tr>");
        sb.append("<tr><td><b>Trạng thái:</b></td><td>").append(currentRoom.getTrangThai()).append("</td></tr>");
        if (currentRoom.getMoTa() != null && !currentRoom.getMoTa().isEmpty()) {
            sb.append("<tr><td><b>Mô tả:</b></td><td>").append(currentRoom.getMoTa()).append("</td></tr>");
        }
        sb.append("</table></html>");

        lblRoomInfo.setText(sb.toString());
    }

    private void loadUserInfo() {
        KhachHang kh = idKhachHang != null ? khachHangService.findById(idKhachHang) : null;

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<table cellpadding='5'>");
        sb.append("<tr><td><b>Username:</b></td><td>").append(tk.getTenDangNhap()).append("</td></tr>");
        sb.append("<tr><td><b>Họ tên:</b></td><td>").append(tk.getHoTen() != null ? tk.getHoTen() : "N/A").append("</td></tr>");
        if (kh != null) {
            sb.append("<tr><td><b>Số điện thoại:</b></td><td>").append(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "N/A").append("</td></tr>");
            sb.append("<tr><td><b>CCCD:</b></td><td>").append(kh.getSoCccd() != null ? kh.getSoCccd() : "N/A").append("</td></tr>");
        }
        sb.append("<tr><td><b>Vai trò:</b></td><td>").append(tk.getVaiTro()).append("</td></tr>");
        sb.append("</table></html>");

        lblUserInfo.setText(sb.toString());
    }

    private void loadInvoices(String filter) {
        invoiceModel.setRowCount(0);
        
        if (idPhongBound == null) {
            // Thêm một row thông báo
            invoiceModel.addRow(new Object[]{
                "Không có dữ liệu", "", "", "", "", "", "", "", 
                "Vui lòng liên hệ quản trị viên", ""
            });
            return;
        }

        List<HoaDon> list = hoaDonService.findByPhong(idPhongBound);
        
        if (list == null || list.isEmpty()) {
            invoiceModel.addRow(new Object[]{
                "Chưa có", "hóa đơn", "nào", "", "", "", "", "", "", ""
            });
            return;
        }

        for (HoaDon h : list) {
            if ("Tất cả".equals(filter)
                        || ("Đã thanh toán".equals(filter) && "daThanhToan".equals(h.getTrangThai()))
                        || ("Chưa thanh toán".equals(filter) && "chuaThanhToan".equals(h.getTrangThai()))) {
                invoiceModel.addRow(new Object[]{
                    h.getThang(), h.getNam(),
                    format(h.getTienPhong()), format(h.getTienDien()), format(h.getTienNuoc()),
                    format(h.getTienDichVu()), format(h.getTienKhac()), format(h.getTongTien()),
                    h.getTrangThai(), h.getNgayTao() != null ? h.getNgayTao().toString() : ""
                });
            }
        }
    }

    private void viewInvoiceDetail() {
        int sel = invoiceTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xem chi tiết!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int thang = (Integer) invoiceModel.getValueAt(sel, 0);
        int nam = (Integer) invoiceModel.getValueAt(sel, 1);
        HoaDon hd = hoaDonService.findAll(thang, nam, null).stream()
                    .filter(h -> h.getIdPhong() == idPhongBound && h.getThang() == thang && h.getNam() == nam)
                    .findFirst().orElse(null);

        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show detail dialog
        showInvoiceDetailDialog(hd);
    }

    private void showInvoiceDetailDialog(HoaDon hd) {
        JDialog dialog = new JDialog(this, "Chi tiết hóa đơn", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<table cellpadding='5'>");
        sb.append("<tr><td><b>ID:</b></td><td>").append(hd.getIdHoaDon()).append("</td></tr>");
        sb.append("<tr><td><b>Tháng/Năm:</b></td><td>").append(hd.getThang()).append("/").append(hd.getNam()).append("</td></tr>");
        sb.append("<tr><td><b>Tiền phòng:</b></td><td>").append(format(hd.getTienPhong())).append(" VNĐ</td></tr>");
        sb.append("<tr><td><b>Tiền điện:</b></td><td>").append(format(hd.getTienDien())).append(" VNĐ</td></tr>");
        sb.append("<tr><td><b>Tiền nước:</b></td><td>").append(format(hd.getTienNuoc())).append(" VNĐ</td></tr>");
        sb.append("<tr><td><b>Tiền dịch vụ:</b></td><td>").append(format(hd.getTienDichVu())).append(" VNĐ</td></tr>");
        sb.append("<tr><td><b>Tiền khác:</b></td><td>").append(format(hd.getTienKhac())).append(" VNĐ</td></tr>");
        sb.append("<tr><td><b><font size='+1'>Tổng cộng:</font></b></td><td><b><font size='+1' color='red'>")
                    .append(format(hd.getTongTien())).append(" VNĐ</font></b></td></tr>");
        sb.append("<tr><td><b>Trạng thái:</b></td><td>")
                    .append("daThanhToan".equals(hd.getTrangThai())
                                ? "<font color='green'>Đã thanh toán</font>"
                                : "<font color='red'>Chưa thanh toán</font>")
                    .append("</td></tr>");
        sb.append("</table></html>");

        JLabel lbl = new JLabel(sb.toString());
        panel.add(lbl, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnClose);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void exportInvoicePDF() {
        int sel = invoiceTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xuất PDF!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int thang = (Integer) invoiceModel.getValueAt(sel, 0);
        int nam = (Integer) invoiceModel.getValueAt(sel, 1);
        HoaDon hd = hoaDonService.findAll(thang, nam, null).stream()
                    .filter(h -> h.getIdPhong() == idPhongBound && h.getThang() == thang && h.getNam() == nam)
                    .findFirst().orElse(null);

        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        byte[] pdf = hoaDonService.exportPdf(hd);
        if (pdf == null) {
            JOptionPane.showMessageDialog(this, "Xuất PDF thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("HoaDon-" + hd.getIdPhong() + "-" + hd.getThang() + "-" + hd.getNam() + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(chooser.getSelectedFile())) {
                fos.write(pdf);
                JOptionPane.showMessageDialog(this, "Đã lưu PDF thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lưu thất bại: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Mở/đóng chatbox (singleton)
     */
    private void openChatFrame() {
        String userName = tk.getHoTen() != null ? tk.getHoTen() : tk.getTenDangNhap();
        // Nếu có thông tin phòng, thêm vào tên
        if (currentRoom != null) {
            userName = userName + " (Phòng " + currentRoom.getTenPhong() + ")";
        }
        ChatFrame.toggleChat(userName, false); // false = không phải admin
    }

    private String format(double v) {
        return String.format("%,.0f", v);
    }

    private String formatMoney(double v) {
        return String.format("%,.0f", v);
    }
}
