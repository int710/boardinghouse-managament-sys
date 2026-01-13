package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.DichVu;
import com.ptpmud.quanlynhatro.model.HoaDon;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.service.DichVuService;
import com.ptpmud.quanlynhatro.service.HoaDonService;
import com.ptpmud.quanlynhatro.service.PhongService;
import com.ptpmud.quanlynhatro.utils.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class HoaDonGD extends javax.swing.JPanel {

    private DefaultTableModel model;
    private final HoaDonService hoaDonService = new HoaDonService();
    private final PhongService phongService = new PhongService();
    private final DichVuService dichVuService = new DichVuService();

    /**
     * Creates new form NewJPanel
     */
    public HoaDonGD() {
        initComponents();
        initTable();
        loadData();

        btnFilter.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> {
            resetFilters();
            loadData();
        });
        btnCreate.addActionListener(e -> showManualDialog());
        btnView.addActionListener(e -> showDetail());
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnPayOK.addActionListener(e -> updateStatus(true));
        btnNoPay.addActionListener(e -> updateStatus(false));
        btnSendMail.addActionListener(e -> sendReminder());
        btnExtractPDF.addActionListener(e -> exportPdf());
        btnMailPDF.addActionListener(e -> sendPdf());
    }

    private void initTable() {
        model = new DefaultTableModel(new String[]{
            "ID", "Phòng", "Tháng", "Năm", "Tiền phòng", "Điện", "Nước", "Dịch vụ", "Khác", "Tổng", "Trạng thái", "Ngày tạo"
        }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table.setModel(model); // Gán model thực sự vào table
        table.setRowHeight(28);
        loadData();
    }

    private void loadData() {
        // Không tự động set tháng/năm hiện tại nữa - giữ nguyên giá trị người dùng đã nhập
        Integer th = null;
        if (cbMonth.getSelectedIndex() > 0) {
            th = cbMonth.getSelectedIndex();
        }
        Integer nam = parseIntOrNull(txtYear.getText().trim());
        String status = (String) cbStatus.getSelectedItem();
        if ("Tất cả".equals(status)) {
            status = null;
        }

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
        for (var p : phongList) {
            cbPhong.addItem(p.getIdPhong() + " - " + p.getTenPhong());
        }
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
        DefaultTableModel dvModel = new DefaultTableModel(new Object[]{"Chọn", "Dịch vụ", "Đơn giá", "Số lượng"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 0 || c == 3;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                if (columnIndex == 2 || columnIndex == 3) {
                    return Double.class;
                }
                return String.class;
            }
        };
        for (DichVu d : dvList) {
            dvModel.addRow(new Object[]{false, d.getTenDichVu(), d.getDonGia(), 0d});
        }
        JTable dvTable = new JTable(dvModel);
        dvTable.setRowHeight(24);
        
        // Add listener to automatically update quantity when checkbox is checked/unchecked
        dvModel.addTableModelListener(e -> {
            if (e.getColumn() == 0) { // Checkbox column
                int row = e.getFirstRow();
                Boolean checked = (Boolean) dvModel.getValueAt(row, 0);
                if (checked != null) {
                    Double currentQty = (Double) dvModel.getValueAt(row, 3);
                    // Only auto-set if quantity is 0
                    if (checked && (currentQty == null || currentQty == 0)) {
                        dvModel.setValueAt(1.0, row, 3);
                    } else if (!checked) {
                        dvModel.setValueAt(0.0, row, 3);
                    }
                }
            }
        });
        
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
        fMonth.addActionListener(e -> {
            loadChiSoCu.run();
            calcUsed.run();
        });
        fYear.addActionListener(e -> {
            loadChiSoCu.run();
            calcUsed.run();
        });
        tDienMoi.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                calcUsed.run();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                calcUsed.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                calcUsed.run();
            }
        });
        tNuocMoi.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                calcUsed.run();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                calcUsed.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                calcUsed.run();
            }
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
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            // VALIDATION: Phòng
            if (cbPhong.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idPhong = Integer.parseInt(cbPhong.getSelectedItem().toString().split(" - ")[0]);
            Phong p = phongService.findById(idPhong);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Phòng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // VALIDATION: Tháng/Năm
            String thStr = fMonth.getText().trim();
            String namStr = fYear.getText().trim();
            if (thStr.isEmpty() || namStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tháng và năm không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int th, nam;
            try {
                th = Integer.parseInt(thStr);
                nam = Integer.parseInt(namStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tháng và năm phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (th < 1 || th > 12) {
                JOptionPane.showMessageDialog(this, "Tháng phải từ 1 đến 12!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (nam < 2000 || nam > 2100) {
                JOptionPane.showMessageDialog(this, "Năm không hợp lệ (2000-2100)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Kiểm tra tháng/năm không được trong tương lai quá xa
            java.time.LocalDate now = java.time.LocalDate.now();
            if (nam > now.getYear() || (nam == now.getYear() && th > now.getMonthValue() + 1)) {
                int opt = JOptionPane.showConfirmDialog(this, 
                    "Tháng/năm này có vẻ trong tương lai. Bạn có chắc chắn?", 
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opt != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // VALIDATION: Chỉ số điện
            double dienCu = parseDoubleOrZero(tDienCu.getText());
            double dienMoi = parseDoubleOrZero(tDienMoi.getText());
            double dgDien = parseDoubleOrZero(tDonGiaDien.getText());
            
            if (dienMoi < 0 || dienCu < 0) {
                JOptionPane.showMessageDialog(this, "Chỉ số điện không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dienMoi < dienCu) {
                JOptionPane.showMessageDialog(this, 
                    "Chỉ số điện mới (" + dienMoi + ") phải lớn hơn hoặc bằng chỉ số cũ (" + dienCu + ")!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dgDien < 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá điện không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dgDien == 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá điện không được bằng 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // VALIDATION: Chỉ số nước
            double nuocCu = parseDoubleOrZero(tNuocCu.getText());
            double nuocMoi = parseDoubleOrZero(tNuocMoi.getText());
            double dgNuoc = parseDoubleOrZero(tDonGiaNuoc.getText());
            
            if (nuocMoi < 0 || nuocCu < 0) {
                JOptionPane.showMessageDialog(this, "Chỉ số nước không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (nuocMoi < nuocCu) {
                JOptionPane.showMessageDialog(this, 
                    "Chỉ số nước mới (" + nuocMoi + ") phải lớn hơn hoặc bằng chỉ số cũ (" + nuocCu + ")!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dgNuoc < 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá nước không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dgNuoc == 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá nước không được bằng 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // VALIDATION: Tiền khác
            double tienKhac = parseDoubleOrZero(tTienKhac.getText());
            if (tienKhac < 0) {
                JOptionPane.showMessageDialog(this, "Tiền khác không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // VALIDATION: Dịch vụ
            java.util.Map<Integer, Integer> dichVuMap = new java.util.HashMap<>();
            for (int i = 0; i < dvModel.getRowCount(); i++) {
                Boolean selected = (Boolean) dvModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    DichVu dichVu = dvList.get(i);
                    int idDichVu = dichVu.getIdDichVu();

                    Double soLuongObj = (Double) dvModel.getValueAt(i, 3);
                    double soLuong = (soLuongObj != null) ? soLuongObj.doubleValue() : 0;

                    if (soLuong < 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Số lượng dịch vụ \"" + dichVu.getTenDichVu() + "\" không được âm!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (soLuong > 0) {
                        dichVuMap.put(idDichVu, (int) soLuong);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Dịch vụ \"" + dichVu.getTenDichVu() + "\" đã chọn nhưng số lượng = 0. Vui lòng nhập số lượng hoặc bỏ chọn!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Kiểm tra hóa đơn đã tồn tại
            boolean existed = hoaDonService.findAll(th, nam, null)
                        .stream().anyMatch(h -> h.getIdPhong() == idPhong && h.getThang() == th && h.getNam() == nam);
            if (existed) {
                if (JOptionPane.showConfirmDialog(this, "Hóa đơn đã tồn tại, ghi đè?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
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

    private void editSelected() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) {
            return;
        }

        // ===== PHÒNG (KHÓA) =====
        Phong phong = phongService.findById(hd.getIdPhong());
        JTextField fPhong = new JTextField(
                    phong != null ? phong.getTenPhong() : ("#" + hd.getIdPhong()));
        fPhong.setEditable(false);

        JTextField fMonth = new JTextField(String.valueOf(hd.getThang()));
        JTextField fYear = new JTextField(String.valueOf(hd.getNam()));
        fMonth.setEditable(false);
        fYear.setEditable(false);

        // ===== ĐIỆN =====
        JTextField tDienCu = new JTextField(String.valueOf(
                    hoaDonService.getLastDienCu(hd.getIdPhong(), hd.getThang(), hd.getNam())));
        JTextField tDienMoi = new JTextField(String.valueOf(
                    hoaDonService.getLastDienCu(hd.getIdPhong(), hd.getThang(), hd.getNam())
                    + hd.getTienDien() / 3500));
        JTextField tDonGiaDien = new JTextField("3500");

        tDienCu.setEditable(false);
        tDienCu.setBackground(new Color(240, 240, 240));

        // ===== NƯỚC =====
        JTextField tNuocCu = new JTextField(String.valueOf(
                    hoaDonService.getLastNuocCu(hd.getIdPhong(), hd.getThang(), hd.getNam())));
        JTextField tNuocMoi = new JTextField(String.valueOf(
                    hoaDonService.getLastNuocCu(hd.getIdPhong(), hd.getThang(), hd.getNam())
                    + hd.getTienNuoc() / 15000));
        JTextField tDonGiaNuoc = new JTextField("15000");

        tNuocCu.setEditable(false);
        tNuocCu.setBackground(new Color(240, 240, 240));

        // ===== TIỀN KHÁC =====
        JTextField tTienKhac = new JTextField(String.valueOf(hd.getTienKhac()));

        // ===== DỊCH VỤ =====
        List<DichVu> dvList = dichVuService.getAll();
        var chiTietDv = hoaDonService.getChiTietDichVu(hd.getIdHoaDon());

        DefaultTableModel dvModel = new DefaultTableModel(
                    new Object[]{"Chọn", "Dịch vụ", "Đơn giá", "Số lượng"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 0 || c == 3;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                if (c == 0) {
                    return Boolean.class;
                }
                if (c == 2 || c == 3) {
                    return Double.class;
                }
                return String.class;
            }
        };

        for (DichVu dv : dvList) {
            boolean checked = false;
            double soLuong = 0;

            if (chiTietDv != null) {
                for (var hddv : chiTietDv) {
                    if (hddv.getIdDichVu() == dv.getIdDichVu()) {
                        checked = true;
                        soLuong = hddv.getSoLuong();
                        break;
                    }
                }
            }

            dvModel.addRow(new Object[]{
                checked,
                dv.getTenDichVu(),
                dv.getDonGia(),
                soLuong
            });
        }

        JTable dvTable = new JTable(dvModel);
        dvTable.setRowHeight(24);
        
        // Add listener to automatically update quantity when checkbox is checked/unchecked
        dvModel.addTableModelListener(e -> {
            if (e.getColumn() == 0) { // Checkbox column
                int row = e.getFirstRow();
                Boolean checked = (Boolean) dvModel.getValueAt(row, 0);
                if (checked != null) {
                    Double currentQty = (Double) dvModel.getValueAt(row, 3);
                    // Only auto-set if quantity is 0
                    if (checked && (currentQty == null || currentQty == 0)) {
                        dvModel.setValueAt(1.0, row, 3);
                    } else if (!checked) {
                        dvModel.setValueAt(0.0, row, 3);
                    }
                }
            }
        });
        
        JScrollPane dvScroll = new JScrollPane(dvTable);
        dvScroll.setPreferredSize(new Dimension(500, 180));

        // ===== LAYOUT =====
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new GridLayout(0, 2, 8, 8));
        top.add(new JLabel("Phòng:"));
        top.add(fPhong);
        top.add(new JLabel("Tháng:"));
        top.add(fMonth);
        top.add(new JLabel("Năm:"));
        top.add(fYear);

        JPanel dien = new JPanel(new GridLayout(0, 2, 8, 8));
        dien.setBorder(javax.swing.BorderFactory.createTitledBorder("Điện"));
        dien.add(new JLabel("Chỉ số cũ:"));
        dien.add(tDienCu);
        dien.add(new JLabel("Chỉ số mới:"));
        dien.add(tDienMoi);
        dien.add(new JLabel("Đơn giá:"));
        dien.add(tDonGiaDien);

        JPanel nuoc = new JPanel(new GridLayout(0, 2, 8, 8));
        nuoc.setBorder(javax.swing.BorderFactory.createTitledBorder("Nước"));
        nuoc.add(new JLabel("Chỉ số cũ:"));
        nuoc.add(tNuocCu);
        nuoc.add(new JLabel("Chỉ số mới:"));
        nuoc.add(tNuocMoi);
        nuoc.add(new JLabel("Đơn giá:"));
        nuoc.add(tDonGiaNuoc);

        JPanel mid = new JPanel(new GridLayout(1, 2, 10, 0));
        mid.add(dien);
        mid.add(nuoc);

        JPanel bottom = new JPanel(new GridLayout(0, 2, 8, 8));
        bottom.add(new JLabel("Tiền khác:"));
        bottom.add(tTienKhac);

        panel.add(top, BorderLayout.NORTH);
        panel.add(mid, BorderLayout.CENTER);

        JPanel dvPanel = new JPanel(new BorderLayout());
        dvPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Dịch vụ"));
        dvPanel.add(dvScroll);
        panel.add(dvPanel, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.add(panel, BorderLayout.NORTH);
        wrapper.add(bottom, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setPreferredSize(new Dimension(620, 500));

        int r = JOptionPane.showConfirmDialog(
                    this, scroll, "Sửa hóa đơn",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double dienCu = parseDoubleOrZero(tDienCu.getText());
            double dienMoi = parseDoubleOrZero(tDienMoi.getText());
            double nuocCu = parseDoubleOrZero(tNuocCu.getText());
            double nuocMoi = parseDoubleOrZero(tNuocMoi.getText());

            if (dienMoi < dienCu || nuocMoi < nuocCu) {
                JOptionPane.showMessageDialog(this,
                            "Chỉ số mới phải >= chỉ số cũ",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double dgDien = parseDoubleOrZero(tDonGiaDien.getText());
            double dgNuoc = parseDoubleOrZero(tDonGiaNuoc.getText());
            double tienKhac = parseDoubleOrZero(tTienKhac.getText());

            java.util.Map<Integer, Integer> dichVuMap = new java.util.HashMap<>();
            for (int i = 0; i < dvModel.getRowCount(); i++) {
                Boolean sel = (Boolean) dvModel.getValueAt(i, 0);
                if (sel != null && sel) {
                    int idDv = dvList.get(i).getIdDichVu();
                    int sl = ((Double) dvModel.getValueAt(i, 3)).intValue();
                    if (sl > 0) {
                        dichVuMap.put(idDv, sl);
                    }
                }
            }

            hoaDonService.createOrUpdateManual(
                        hd.getIdPhong(), hd.getThang(), hd.getNam(),
                        dienCu, dienMoi, dgDien,
                        nuocCu, nuocMoi, dgNuoc,
                        dichVuMap, tienKhac
            );

            JOptionPane.showMessageDialog(this, "Đã cập nhật hóa đơn.");
            loadData();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                        "Cập nhật thất bại: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(boolean paid) {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hóa đơn.");
            return;
        }
        int id = (int) model.getValueAt(sel, 0);
        boolean ok = paid ? hoaDonService.markPaid(id) : hoaDonService.markUnpaid(id);
        if (ok) {
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetail() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) {
            return;
        }

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

    private void deleteSelected() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa hóa đơn ID=" + hd.getIdHoaDon() + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        if (hoaDonService.delete(hd.getIdHoaDon())) {
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendReminder() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) {
            return;
        }
        
        // Kiểm tra trạng thái hóa đơn
        if ("daThanhToan".equals(hd.getTrangThai())) {
            JOptionPane.showMessageDialog(this, 
                "Không thể gửi email nhắc nhở cho hóa đơn đã thanh toán!", 
                "Cảnh báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Tự động lấy email từ khách hàng
        String customerEmail = hoaDonService.getCustomerEmail(hd);
        
        JTextField tEmail = new JTextField();
        if (customerEmail != null && !customerEmail.isEmpty()) {
            tEmail.setText(customerEmail);
            tEmail.setToolTipText("Email đã được lấy từ thông tin khách hàng");
        } else {
            tEmail.setToolTipText("Nhập email người nhận (khách hàng chưa có email)");
        }
        
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"Email người nhận:", tEmail}, "Gửi nhắc thanh toán", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        
        String email = tEmail.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email người nhận.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean ok = hoaDonService.sendReminder(email, hd);
        JOptionPane.showMessageDialog(this, ok ? "Đã gửi (kiểm tra log SMTP)." : "Gửi thất bại. Kiểm tra cấu hình SMTP/env.");
    }

    private void exportPdf() {
        HoaDon hd = getSelectedHoaDon();
        if (hd == null) {
            return;
        }
        byte[] pdf = hoaDonService.exportPdf(hd);
        if (pdf == null) {
            JOptionPane.showMessageDialog(this, "Xuất PDF thất bại.");
            return;
        }
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
        if (hd == null) {
            return;
        }
        
        // Kiểm tra trạng thái hóa đơn
        if ("daThanhToan".equals(hd.getTrangThai())) {
            JOptionPane.showMessageDialog(this, 
                "Không thể gửi email nhắc nhở cho hóa đơn đã thanh toán!", 
                "Cảnh báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Tự động lấy email từ khách hàng
        String customerEmail = hoaDonService.getCustomerEmail(hd);
        
        JTextField tEmail = new JTextField();
        if (customerEmail != null && !customerEmail.isEmpty()) {
            tEmail.setText(customerEmail);
            tEmail.setToolTipText("Email đã được lấy từ thông tin khách hàng");
        } else {
            tEmail.setToolTipText("Nhập email người nhận (khách hàng chưa có email)");
        }
        
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"Email người nhận:", tEmail}, "Gửi hóa đơn PDF", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        
        String email = tEmail.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email người nhận.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean ok = hoaDonService.sendReminderWithPdf(email, hd);
        JOptionPane.showMessageDialog(this, ok ? "Đã gửi PDF." : "Gửi thất bại (kiểm tra SMTP/PDF).");
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

    /**
     * Hàm method
     *
     */
    private void resetFilters() {
        cbMonth.setSelectedIndex(0);
        txtYear.setText("");
        cbStatus.setSelectedIndex(0);
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String format(double v) {
        return String.format("%,.0f", v);
    }

    private double parseDoubleOrZero(String s) {
        if (s == null || s.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        cbStatus = new javax.swing.JComboBox<>();
        btnFilter = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        cbMonth = new javax.swing.JComboBox<>();
        txtYear = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        btnCreate = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnPayOK = new javax.swing.JButton();
        btnNoPay = new javax.swing.JButton();
        btnSendMail = new javax.swing.JButton();
        btnMailPDF = new javax.swing.JButton();
        btnExtractPDF = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        cbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "daThanhToan", "chuaThanhToan" }));
        cbStatus.addActionListener(this::cbStatusActionPerformed);

        btnFilter.setText("🔍 Lọc");
        btnFilter.addActionListener(this::btnFilterActionPerformed);

        btnReload.setText("⟳ Tải lại");

        cbMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Tháng:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Năm:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addComponent(cbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1)
                .addComponent(jLabel2)
                .addComponent(btnFilter)
                .addComponent(btnReload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 33, Short.MAX_VALUE)
        );

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(table);

        btnCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnCreate.setText("Tạo mới hóa đơn");

        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/view.png"))); // NOI18N
        btnView.setText("Chi tiết");

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        btnEdit.setText("Sửa");

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnDelete.setText("Xóa");

        btnPayOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/payment.png"))); // NOI18N
        btnPayOK.setText("Đã thanh toán");

        btnNoPay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel.png"))); // NOI18N
        btnNoPay.setText("Chưa thanh toán");

        btnSendMail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/email.png"))); // NOI18N
        btnSendMail.setText("Gửi email nhắc");

        btnMailPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/email.png"))); // NOI18N
        btnMailPDF.setText("Gửi email + PDF");

        btnExtractPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pdf.png"))); // NOI18N
        btnExtractPDF.setText("Xuất hóa đơn");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnPayOK, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNoPay, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSendMail, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMailPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExtractPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 2, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNoPay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE)
                        .addComponent(btnPayOK, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnMailPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(btnSendMail, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(btnExtractPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnCreate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnView)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEdit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete)))
                        .addGap(0, 27, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(btnView)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbStatusActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFilterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExtractPDF;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnMailPDF;
    private javax.swing.JButton btnNoPay;
    private javax.swing.JButton btnPayOK;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnSendMail;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable table;
    private javax.swing.JComboBox<String> cbMonth;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables
}
