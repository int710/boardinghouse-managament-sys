package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.HoaDon;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.service.HoaDonService;
import com.ptpmud.quanlynhatro.service.PhongService;
import com.ptpmud.quanlynhatro.service.DichVuService;
import com.ptpmud.quanlynhatro.model.DichVu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class HoaDonFrame extends JPanel {
    private final HoaDonService hoaDonService = new HoaDonService();
    private final PhongService phongService = new PhongService();
    private final DichVuService dichVuService = new DichVuService();

    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtMonth;
    private final JTextField txtYear;
    private final JComboBox<String> cbStatus;

    public HoaDonFrame() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(248, 249, 250));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        
        // Filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filterRow.setOpaque(false);
        filterRow.add(new JLabel("Tháng:"));
        txtMonth = new JTextField(String.valueOf(LocalDate.now().getMonthValue()), 4);
        filterRow.add(txtMonth);
        filterRow.add(new JLabel("Năm:"));
        txtYear = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
        filterRow.add(txtYear);
        cbStatus = new JComboBox<>(new String[]{"Tất cả", "chuaThanhToan", "daThanhToan"});
        filterRow.add(cbStatus);
        JButton btnFilter = new JButton("Lọc");
        JButton btnReload = new JButton("⟳ Tải lại");
        filterRow.add(btnFilter);
        filterRow.add(btnReload);
        top.add(filterRow, BorderLayout.NORTH);
        
        // Action buttons row 1
        JPanel actionRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionRow1.setOpaque(false);
        JButton btnGenerate = new JButton("➕ Tạo/ghi đè hóa đơn");
        JButton btnView = new JButton("👁 Chi tiết");
        JButton btnEdit = new JButton("✏ Sửa");
        JButton btnDelete = new JButton("🗑 Xóa");
        actionRow1.add(btnGenerate);
        actionRow1.add(btnView);
        actionRow1.add(btnEdit);
        actionRow1.add(btnDelete);
        
        // Action buttons row 2
        JPanel actionRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionRow2.setOpaque(false);
        JButton btnPaid = new JButton("✅ Đã thanh toán");
        JButton btnUnpaid = new JButton("❌ Chưa thanh toán");
        JButton btnReminder = new JButton("📧 Gửi email nhắc");
        actionRow2.add(btnPaid);
        actionRow2.add(btnUnpaid);
        actionRow2.add(btnReminder);
        
        // Action buttons row 3
        JPanel actionRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionRow3.setOpaque(false);
        JButton btnPdf = new JButton("📄 Xuất PDF");
        JButton btnSendPdf = new JButton("📧 Gửi mail kèm PDF");
        actionRow3.add(btnPdf);
        actionRow3.add(btnSendPdf);
        
        JPanel actionPanel = new JPanel(new BorderLayout(0, 4));
        actionPanel.setOpaque(false);
        actionPanel.add(actionRow1, BorderLayout.NORTH);
        actionPanel.add(actionRow2, BorderLayout.CENTER);
        actionPanel.add(actionRow3, BorderLayout.SOUTH);
        top.add(actionPanel, BorderLayout.CENTER);
        
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "ID", "Phòng", "Tháng", "Năm", "Tiền phòng", "Điện", "Nước", "Dịch vụ", "Khác", "Tổng", "Trạng thái", "Ngày tạo"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> { resetFilters(); loadData(); });
        btnGenerate.addActionListener(e -> showManualDialog());
        btnView.addActionListener(e -> showDetail());
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnPaid.addActionListener(e -> updateStatus(true));
        btnUnpaid.addActionListener(e -> updateStatus(false));
        btnReminder.addActionListener(e -> sendReminder());
        btnPdf.addActionListener(e -> exportPdf());
        btnSendPdf.addActionListener(e -> sendPdf());

        loadData();
    }

    private void resetFilters() {
        txtMonth.setText("");
        txtYear.setText("");
        cbStatus.setSelectedIndex(0);
    }

    private void loadData() {
        Integer th = parseIntOrNull(txtMonth.getText().trim());
        Integer nam = parseIntOrNull(txtYear.getText().trim());
        String status = (String) cbStatus.getSelectedItem();
        if ("Tất cả".equals(status)) status = null;

        List<HoaDon> list = hoaDonService.findAll(th, nam, status);
        model.setRowCount(0);
        for (HoaDon h : list) {
            Phong p = phongService.findById(h.getIdPhong());
            String tenPhong = p != null ? p.getTenPhong() : "#" + h.getIdPhong();
            model.addRow(new Object[]{
                    h.getIdHoaDon(),
                    tenPhong,
                    h.getThang(),
                    h.getNam(),
                    format(h.getTienPhong()),
                    format(h.getTienDien()),
                    format(h.getTienNuoc()),
                    format(h.getTienDichVu()),
                    format(h.getTienKhac()),
                    format(h.getTongTien()),
                    h.getTrangThai(),
                    h.getNgayTao()
            });
        }
    }

    private void showManualDialog() {
        var phongList = phongService.getAll();
        JComboBox<String> cbPhong = new JComboBox<>();
        for (var p : phongList) cbPhong.addItem(p.getIdPhong() + " - " + p.getTenPhong());
        JTextField fMonth = new JTextField(String.valueOf(LocalDate.now().getMonthValue()));
        JTextField fYear = new JTextField(String.valueOf(LocalDate.now().getYear()));

        // Điện
        JTextField tDienCu = new JTextField();
        JTextField tDienMoi = new JTextField();
        JTextField tDonGiaDien = new JTextField("3500");
        tDienCu.setEditable(false);
        tDienCu.setBackground(new Color(240, 240, 240));

        // Nước
        JTextField tNuocCu = new JTextField();
        JTextField tNuocMoi = new JTextField();
        JTextField tDonGiaNuoc = new JTextField("15000");
        tNuocCu.setEditable(false);
        tNuocCu.setBackground(new Color(240, 240, 240));

        // Tiền khác
        JTextField tTienKhac = new JTextField("0");

        // Dịch vụ: table với cột số lượng - chỉ chọn dịch vụ nào dùng
        java.util.List<DichVu> dvList = dichVuService.getAll();
        DefaultTableModel dvModel = new DefaultTableModel(new Object[]{"Chọn","Dịch vụ","Đơn giá","Số lượng"},0) {
            @Override public boolean isCellEditable(int r, int c) { return c==0 || c==3; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex==0) return Boolean.class;
                if (columnIndex==2 || columnIndex==3) return Double.class;
                return String.class;
            }
        };
        for (DichVu d : dvList) {
            dvModel.addRow(new Object[]{false, d.getTenDichVu(), d.getDonGia(), 0d});
        }
        JTable dvTable = new JTable(dvModel);
        dvTable.setRowHeight(24);
        JScrollPane dvScroll = new JScrollPane(dvTable);
        dvScroll.setPreferredSize(new Dimension(500, 200));

        // Labels hiển thị điện/nước dùng
        JLabel lblDienUsed = new JLabel("Điện dùng: 0 kWh");
        JLabel lblNuocUsed = new JLabel("Nước dùng: 0 khối");
        lblDienUsed.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNuocUsed.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Listener để tự động load chỉ số cũ khi chọn phòng/tháng/năm
        Runnable loadChiSoCu = () -> {
            try {
                int idPhong = Integer.parseInt(cbPhong.getSelectedItem().toString().split(" - ")[0]);
                int th = Integer.parseInt(fMonth.getText().trim());
                int nam = Integer.parseInt(fYear.getText().trim());
                double dienCu = hoaDonService.getLastDienCu(idPhong, th, nam);
                double nuocCu = hoaDonService.getLastNuocCu(idPhong, th, nam);
                tDienCu.setText(String.format("%.0f", dienCu));
                tNuocCu.setText(String.format("%.0f", nuocCu));
            } catch (Exception ex) {
                // ignore
            }
        };

        // Listener để tính toán điện/nước dùng
        Runnable calcUsed = () -> {
            try {
                double dienCu = parseDoubleOrZero(tDienCu.getText());
                double dienMoi = parseDoubleOrZero(tDienMoi.getText());
                double nuocCu = parseDoubleOrZero(tNuocCu.getText());
                double nuocMoi = parseDoubleOrZero(tNuocMoi.getText());
                double dienUsed = Math.max(0, dienMoi - dienCu);
                double nuocUsed = Math.max(0, nuocMoi - nuocCu);
                lblDienUsed.setText(String.format("Điện dùng: %.0f kWh", dienUsed));
                lblNuocUsed.setText(String.format("Nước dùng: %.0f khối", nuocUsed));
            } catch (Exception ex) {
                // ignore
            }
        };

        cbPhong.addActionListener(e -> loadChiSoCu.run());
        fMonth.addActionListener(e -> { loadChiSoCu.run(); calcUsed.run(); });
        fYear.addActionListener(e -> { loadChiSoCu.run(); calcUsed.run(); });
        tDienMoi.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcUsed.run(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calcUsed.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calcUsed.run(); }
        });
        tNuocMoi.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcUsed.run(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calcUsed.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calcUsed.run(); }
        });

        // Load chỉ số cũ ban đầu
        SwingUtilities.invokeLater(loadChiSoCu);

        // Layout với scroll để không bị ẩn
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: Phòng, tháng, năm
        JPanel topPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        topPanel.add(new JLabel("Chọn phòng:"));
        topPanel.add(cbPhong);
        topPanel.add(new JLabel("Tháng:"));
        topPanel.add(fMonth);
        topPanel.add(new JLabel("Năm:"));
        topPanel.add(fYear);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center: Scroll pane chứa tất cả
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        
        // Điện/Nước
        JPanel dienNuocPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        dienNuocPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Điện"));
        dienNuocPanel.add(new JLabel("Chỉ số cũ (tự động):"));
        dienNuocPanel.add(tDienCu);
        dienNuocPanel.add(new JLabel("Chỉ số mới:"));
        dienNuocPanel.add(tDienMoi);
        dienNuocPanel.add(new JLabel("Đơn giá (VNĐ/kWh):"));
        dienNuocPanel.add(tDonGiaDien);
        dienNuocPanel.add(lblDienUsed);
        dienNuocPanel.add(new JLabel(""));

        JPanel nuocPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        nuocPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Nước"));
        nuocPanel.add(new JLabel("Chỉ số cũ (tự động):"));
        nuocPanel.add(tNuocCu);
        nuocPanel.add(new JLabel("Chỉ số mới:"));
        nuocPanel.add(tNuocMoi);
        nuocPanel.add(new JLabel("Đơn giá (VNĐ/khối):"));
        nuocPanel.add(tDonGiaNuoc);
        nuocPanel.add(lblNuocUsed);
        nuocPanel.add(new JLabel(""));

        JPanel dienNuocContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        dienNuocContainer.add(dienNuocPanel);
        dienNuocContainer.add(nuocPanel);
        centerPanel.add(dienNuocContainer, BorderLayout.NORTH);

        // Dịch vụ
        JPanel dvPanel = new JPanel(new BorderLayout(8, 8));
        dvPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Dịch vụ (chọn dịch vụ đã dùng và nhập số lượng)"));
        dvPanel.add(dvScroll, BorderLayout.CENTER);
        centerPanel.add(dvPanel, BorderLayout.CENTER);

        // Bottom: Tiền khác
        JPanel bottomPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        bottomPanel.add(new JLabel("Tiền khác (VNĐ):"));
        bottomPanel.add(tTienKhac);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        int r = JOptionPane.showConfirmDialog(this, mainPanel, "Tạo/ghi đè hóa đơn thủ công", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        try {
            int idPhong = Integer.parseInt(cbPhong.getSelectedItem().toString().split(" - ")[0]);
            int th = Integer.parseInt(fMonth.getText().trim());
            int nam = Integer.parseInt(fYear.getText().trim());

            double dienCu = parseDoubleOrZero(tDienCu.getText());
            double dienMoi = parseDoubleOrZero(tDienMoi.getText());
            double dgDien = parseDoubleOrZero(tDonGiaDien.getText());
            double nuocCu = parseDoubleOrZero(tNuocCu.getText());
            double nuocMoi = parseDoubleOrZero(tNuocMoi.getText());
            double dgNuoc = parseDoubleOrZero(tDonGiaNuoc.getText());
            double tienKhac = parseDoubleOrZero(tTienKhac.getText());

            // Validation: chỉ số mới phải >= chỉ số cũ
            if (dienMoi < dienCu) {
                JOptionPane.showMessageDialog(this, "Chỉ số điện mới phải lớn hơn hoặc bằng chỉ số cũ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (nuocMoi < nuocCu) {
                JOptionPane.showMessageDialog(this, "Chỉ số nước mới phải lớn hơn hoặc bằng chỉ số cũ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo Map dịch vụ: chỉ các dịch vụ đã chọn và có số lượng > 0
            java.util.Map<Integer, Integer> dichVuMap = new java.util.HashMap<>();
            for (int i=0;i<dvModel.getRowCount();i++) {
                Boolean selected = (Boolean) dvModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    int idDichVu = ((Number)dvModel.getValueAt(i,1)).intValue();
                    int soLuong = ((Number)dvModel.getValueAt(i,3)).intValue();
                    if (soLuong > 0) {
                        dichVuMap.put(idDichVu, soLuong);
                    }
                }
            }

            // Kiểm tra hóa đơn đã tồn tại
            boolean existed = hoaDonService.findAll(th, nam, null)
                    .stream().anyMatch(h -> h.getIdPhong() == idPhong && h.getThang() == th && h.getNam() == nam);
            if (existed) {
                if (JOptionPane.showConfirmDialog(this, "Hóa đơn đã tồn tại, ghi đè?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            }

            HoaDon hd = hoaDonService.createOrUpdateManual(idPhong, th, nam,
                    dienCu, dienMoi, dgDien,
                    nuocCu, nuocMoi, dgNuoc,
                    dichVuMap, tienKhac);
            if (hd != null) {
                JOptionPane.showMessageDialog(this, existed ? "Đã cập nhật hóa đơn." : "Đã tạo hóa đơn.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể tạo/cập nhật hóa đơn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateStatus(boolean paid) {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn hóa đơn."); return; }
        int id = (int) model.getValueAt(sel, 0);
        boolean ok = paid ? hoaDonService.markPaid(id) : hoaDonService.markUnpaid(id);
        if (ok) {
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return null; }
    }

    private String format(double v) {
        return String.format("%,.0f", v);
    }

    private double parseDoubleOrZero(String s) {
        if (s == null || s.isBlank()) return 0;
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException ex) { return 0; }
    }

    private HoaDon getSelectedHoaDon() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hóa đơn.");
            return null;
        }
        int id = (int) model.getValueAt(sel, 0);
        return hoaDonService.findById(id);
    }

    private void showDetail() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) return;
        
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        
        // Thông tin cơ bản
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        infoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(String.valueOf(hd.getIdHoaDon())));
        infoPanel.add(new JLabel("Phòng:"));
        infoPanel.add(new JLabel("#" + hd.getIdPhong()));
        infoPanel.add(new JLabel("Tháng/Năm:"));
        infoPanel.add(new JLabel(hd.getThang() + "/" + hd.getNam()));
        infoPanel.add(new JLabel("Trạng thái:"));
        infoPanel.add(new JLabel(hd.getTrangThai()));
        infoPanel.add(new JLabel("Ngày tạo:"));
        infoPanel.add(new JLabel(hd.getNgayTao() != null ? hd.getNgayTao().toString() : ""));
        
        // Chi tiết tiền
        JPanel moneyPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        moneyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Chi tiết thanh toán"));
        moneyPanel.add(new JLabel("Tiền phòng:"));
        moneyPanel.add(new JLabel(format(hd.getTienPhong()) + " VNĐ"));
        moneyPanel.add(new JLabel("Tiền điện:"));
        moneyPanel.add(new JLabel(format(hd.getTienDien()) + " VNĐ"));
        moneyPanel.add(new JLabel("Tiền nước:"));
        moneyPanel.add(new JLabel(format(hd.getTienNuoc()) + " VNĐ"));
        moneyPanel.add(new JLabel("Tiền dịch vụ:"));
        moneyPanel.add(new JLabel(format(hd.getTienDichVu()) + " VNĐ"));
        moneyPanel.add(new JLabel("Tiền khác:"));
        moneyPanel.add(new JLabel(format(hd.getTienKhac()) + " VNĐ"));
        moneyPanel.add(new JLabel("<html><b>Tổng cộng:</b></html>"));
        moneyPanel.add(new JLabel("<html><b>" + format(hd.getTongTien()) + " VNĐ</b></html>"));
        
        // Chi tiết dịch vụ
        JPanel dvPanel = new JPanel(new BorderLayout(8, 8));
        dvPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Chi tiết dịch vụ"));
        DefaultTableModel dvDetailModel = new DefaultTableModel(
            new String[]{"Tên dịch vụ", "Số lượng", "Đơn giá", "Thành tiền"}, 0
        );
        JTable dvTable = new JTable(dvDetailModel);
        dvTable.setRowHeight(25);
        dvTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        dvTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        dvTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        dvTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        var chiTietDichVu = hoaDonService.getChiTietDichVu(hd.getIdHoaDon());
        if (chiTietDichVu != null && !chiTietDichVu.isEmpty()) {
            for (var hddv : chiTietDichVu) {
                String tenDv = hddv.getDichVu() != null ? hddv.getDichVu().getTenDichVu() : "Dịch vụ #" + hddv.getIdDichVu();
                dvDetailModel.addRow(new Object[]{
                    tenDv,
                    hddv.getSoLuong(),
                    format(hddv.getDonGia()) + " VNĐ",
                    format(hddv.getThanhTien()) + " VNĐ"
                });
            }
        } else {
            dvDetailModel.addRow(new Object[]{"Không có dịch vụ", "", "", ""});
        }
        
        JScrollPane dvScroll = new JScrollPane(dvTable);
        dvScroll.setPreferredSize(new Dimension(500, 150));
        dvPanel.add(dvScroll, BorderLayout.CENTER);
        
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(infoPanel, BorderLayout.NORTH);
        topPanel.add(moneyPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(dvPanel, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(this, panel, "Chi tiết hóa đơn", JOptionPane.PLAIN_MESSAGE);
    }

    private void editSelected() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) return;
        JTextField tTienKhac = new JTextField(String.valueOf((long) hd.getTienKhac()));
        JComboBox<String> st = new JComboBox<>(new String[]{"chuaThanhToan","daThanhToan"});
        st.setSelectedItem(hd.getTrangThai());
        Object[] fields = {
                "Tiền khác:", tTienKhac,
                "Trạng thái:", st
        };
        int r = JOptionPane.showConfirmDialog(this, fields, "Cập nhật hóa đơn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        try {
            double tk = Double.parseDouble(tTienKhac.getText().trim());
            hd.setTienKhac(tk);
            hd.setTrangThai((String) st.getSelectedItem());
            hoaDonService.updateManual(hd);
            loadData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tiền khác không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa hóa đơn ID=" + hd.getIdHoaDon() + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        if (hoaDonService.delete(hd.getIdHoaDon())) {
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendReminder() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) return;
        JTextField tEmail = new JTextField();
        tEmail.setToolTipText("Nhập email người nhận");
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"Email người nhận:", tEmail}, "Gửi nhắc thanh toán", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        boolean ok = hoaDonService.sendReminder(tEmail.getText().trim(), hd);
        JOptionPane.showMessageDialog(this, ok ? "Đã gửi (kiểm tra log SMTP)." : "Gửi thất bại. Kiểm tra cấu hình SMTP/env.");
    }

    private void exportPdf() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) return;
        byte[] pdf = hoaDonService.exportPdf(hd);
        if (pdf == null) { JOptionPane.showMessageDialog(this, "Xuất PDF thất bại."); return; }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("HoaDon-" + hd.getIdPhong() + "-" + hd.getThang() + "-" + hd.getNam() + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(chooser.getSelectedFile())) {
                fos.write(pdf);
                JOptionPane.showMessageDialog(this, "Đã lưu PDF.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lưu thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sendPdf() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) return;
        JTextField tEmail = new JTextField();
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"Email người nhận:", tEmail}, "Gửi hóa đơn PDF", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        boolean ok = hoaDonService.sendReminderWithPdf(tEmail.getText().trim(), hd);
        JOptionPane.showMessageDialog(this, ok ? "Đã gửi PDF." : "Gửi thất bại (kiểm tra SMTP/PDF).");
    }
}

