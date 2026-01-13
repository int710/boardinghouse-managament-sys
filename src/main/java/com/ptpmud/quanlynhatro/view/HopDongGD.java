package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.HopDong;
import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.service.HopDongService;
import com.ptpmud.quanlynhatro.service.KhachHangService;
import com.ptpmud.quanlynhatro.service.PhongService;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class HopDongGD extends javax.swing.JPanel {
    private final HopDongService hopDongService = new HopDongService();
    private final PhongService phongService = new PhongService();
    private final KhachHangService khachHangService = new KhachHangService();
    private DefaultTableModel tableModel;

    /**
     * Creates new form NewJPanel
     */
    public HopDongGD() {
        initComponents();
        initAction();
        initTable();
    }

    private void initTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "IdPhòng", "Tên phòng", "Khách", "Bắt đầu", "Kết thúc", "Tiền cọc", "Trạng thái", "Còn lại (ngày)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table.setModel(tableModel); // Gán model thực sự vào table
        table.setRowHeight(28);
        loadData();
    }

    private void initAction() {
        btnCreate.addActionListener(e -> showCreateDialog());
        btnView.addActionListener(e -> showDetailDialog());
        btnEnd.addActionListener(e -> endSelectedContract());
        btnRenew.addActionListener(e -> renewSelectedContract());
        btnHistory.addActionListener(e -> showHistoryForSelectedRoom());

        btnSearch.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            cbFilter.setSelectedIndex(0);
            loadData();
        });
    }

    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            String filter = ((String) cbFilter.getSelectedItem());
            String keyword = txtSearch.getText().trim();

            List<HopDong> list;
            if (!"Tất cả".equals(filter) && keyword.isEmpty()) {
                // filter by status
                list = hopDongService.find(filter);
            } else if (!keyword.isEmpty()) {
                list = hopDongService.search(keyword);
            } else {
                list = hopDongService.findAll();
            }

            for (HopDong h : list) {
                Phong p = phongService.findById(h.getIdPhong());
                KhachHang kh = khachHangService.findById(h.getIdKhachHang());
                String roomName = p != null ? p.getTenPhong() : ("#" + h.getIdPhong());
                String khName = kh != null ? kh.getTenKhachHang() : ("#" + h.getIdKhachHang());
                String ketThuc = h.getNgayKetThuc() != null ? h.getNgayKetThuc().toString() : "";
                long daysLeft = calcDaysLeft(h.getNgayKetThucAsLocal());
                tableModel.addRow(new Object[]{
                    h.getIdHopDong(),
                    h.getIdPhong(),
                    roomName,
                    khName,
                    h.getNgayBatDau(),
                    ketThuc,
                    formatMoney(h.getTienCoc()),
                    h.getTrangThai(),
                    daysLeft >= 0 ? daysLeft : ""
                });
            }
            updateInfoBar();
        });
    }

    private long calcDaysLeft(LocalDate end) {
        if (end == null) {
            return -1;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), end);
    }

    private String formatMoney(double v) {
        return String.format("%,.0f", v);
    }

    private String generatePassword(int len) {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void updateInfoBar() {
        long total = hopDongService.countAll();
        long active = hopDongService.countByStatus("dangThue");
        long ended = hopDongService.countByStatus("daKetThuc");
        lblInfor.setText(String.format("Tổng hợp đồng: %d | Đang thuê: %d | Đã kết thúc: %d", total, active, ended));
    }

    // ---- Dialogs & actions ----
    private void showCreateDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        // Phòng - ComboBox
        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Phòng *:"), c);
        JComboBox<String> cbPhong = new JComboBox<>();
        List<Phong> phongList = phongService.getAll();
        for (Phong p : phongList) {
            if (!"daThue".equalsIgnoreCase(p.getTrangThai()) && 
                !"baoTri".equalsIgnoreCase(p.getTrangThai())) {
                cbPhong.addItem(p.getIdPhong() + " - " + p.getTenPhong() + 
                    " (" + p.getTrangThai() + ")");
            }
        }
        c.gridx = 1;
        panel.add(cbPhong, c);

        // Khách hàng - ComboBox
        c.gridx = 0; c.gridy++;
        panel.add(new JLabel("Khách hàng *:"), c);
        JComboBox<String> cbKhach = new JComboBox<>();
        List<KhachHang> khList = khachHangService.getAll();
        for (KhachHang kh : khList) {
            cbKhach.addItem(kh.getIdKhachHang() + " - " + kh.getTenKhachHang());
        }
        c.gridx = 1;
        panel.add(cbKhach, c);

        // Ngày bắt đầu
        c.gridx = 0; c.gridy++;
        panel.add(new JLabel("Ngày bắt đầu * (YYYY-MM-DD):"), c);
        JTextField txtBD = new JTextField(LocalDate.now().toString());
        txtBD.setToolTipText("Định dạng: YYYY-MM-DD");
        c.gridx = 1;
        panel.add(txtBD, c);

        // Ngày kết thúc
        c.gridx = 0; c.gridy++;
        panel.add(new JLabel("Ngày kết thúc (YYYY-MM-DD):"), c);
        JTextField txtKT = new JTextField();
        txtKT.setToolTipText("Để trống nếu không xác định. Phải sau ngày bắt đầu.");
        c.gridx = 1;
        panel.add(txtKT, c);

        // Tiền cọc
        c.gridx = 0; c.gridy++;
        panel.add(new JLabel("Tiền cọc (VNĐ) *:"), c);
        JTextField txtCoc = new JTextField("0");
        txtCoc.setToolTipText("Số tiền cọc, phải >= 0");
        c.gridx = 1;
        panel.add(txtCoc, c);

        // Tạo tài khoản cho người thuê
        c.gridx = 0; c.gridy++;
        JCheckBox cbCreateAccount = new JCheckBox("Tạo tài khoản đăng nhập cho người thuê");
        cbCreateAccount.setToolTipText("Tự động tạo tài khoản với username: kh{idKhachHang}");
        cbCreateAccount.setSelected(true);
        c.gridx = 1;
        panel.add(cbCreateAccount, c);

        // Thông báo validation
        JLabel lblWarning = new JLabel("<html><font color='red'>* Trường bắt buộc</font></html>");
        c.gridx = 0; c.gridy++; c.gridwidth = 2;
        panel.add(lblWarning, c);

        int r = JOptionPane.showConfirmDialog(this, panel, "Tạo hợp đồng mới", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }

        // VALIDATION
        try {
            // Validate phòng
            if (cbPhong.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String phongStr = cbPhong.getSelectedItem().toString();
            int idPhong = Integer.parseInt(phongStr.split(" - ")[0]);
            Phong p = phongService.findById(idPhong);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Phòng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ("baoTri".equalsIgnoreCase(p.getTrangThai())) {
                JOptionPane.showMessageDialog(this, "Phòng đang bảo trì, không thể tạo hợp đồng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ("daThue".equalsIgnoreCase(p.getTrangThai())) {
                JOptionPane.showMessageDialog(this, "Phòng đang được thuê! Vui lòng chọn phòng khác.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Kiểm tra hợp đồng đang hoạt động cho phòng này
            if (hopDongService.findActiveByPhong(idPhong) != null) {
                JOptionPane.showMessageDialog(this, "Phòng này đã có hợp đồng đang hoạt động!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate khách hàng
            if (cbKhach.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String khachStr = cbKhach.getSelectedItem().toString();
            int idKh = Integer.parseInt(khachStr.split(" - ")[0]);
            KhachHang kh = khachHangService.findById(idKh);
            if (kh == null) {
                JOptionPane.showMessageDialog(this, "Khách hàng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Kiểm tra khách hàng đã có hợp đồng đang hoạt động
            if (hopDongService.findActiveByKhachHang(idKh) != null) {
                int opt = JOptionPane.showConfirmDialog(this, 
                    "Khách hàng này đã có hợp đồng đang hoạt động. Bạn có muốn tiếp tục?", 
                    "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opt != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Validate ngày bắt đầu
            String bdStr = txtBD.getText().trim();
            if (bdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được để trống!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate bd;
            try {
                bd = LocalDate.parse(bdStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không đúng định dạng (YYYY-MM-DD)!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (bd.isBefore(LocalDate.now().minusDays(30))) {
                int opt = JOptionPane.showConfirmDialog(this, 
                    "Ngày bắt đầu cách quá xa hiện tại. Bạn có chắc chắn?", 
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opt != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Validate ngày kết thúc
            LocalDate kt = null;
            String ktStr = txtKT.getText().trim();
            if (!ktStr.isEmpty()) {
                try {
                    kt = LocalDate.parse(ktStr);
                    if (kt.isBefore(bd) || kt.isEqual(bd)) {
                        JOptionPane.showMessageDialog(this, 
                            "Ngày kết thúc phải sau ngày bắt đầu!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Ngày kết thúc không đúng định dạng (YYYY-MM-DD)!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Validate tiền cọc
            String cocStr = txtCoc.getText().trim();
            if (cocStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiền cọc không được để trống!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double coc;
            try {
                coc = Double.parseDouble(cocStr);
                if (coc < 0) {
                    JOptionPane.showMessageDialog(this, "Tiền cọc phải >= 0!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tiền cọc phải là số hợp lệ!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo hợp đồng
            HopDong hd = new HopDong();
            hd.setIdPhong(idPhong);
            hd.setIdKhachHang(idKh);
            hd.setNgayBatDau(bd);
            hd.setNgayKetThuc(kt);
            hd.setTienCoc(coc);
            hd.setTrangThai("dangThue");

            boolean ok = hopDongService.create(hd);
            if (ok) {
                String successMsg = "Tạo hợp đồng thành công!\n" +
                    "Phòng: " + p.getTenPhong() + "\n" +
                    "Khách: " + kh.getTenKhachHang();
                
                // Tạo tài khoản nếu được chọn
                if (cbCreateAccount.isSelected()) {
                    com.ptpmud.quanlynhatro.service.TaiKhoanService tkService = 
                        new com.ptpmud.quanlynhatro.service.TaiKhoanService();
                    String username = "kh" + idKh;
                    String password = generatePassword(8);
                    boolean accountCreated = tkService.register(username, password, kh.getTenKhachHang(), "user");
                    
                    if (accountCreated) {
                        successMsg += "\n\n✅ Đã tạo tài khoản đăng nhập:\n" +
                            "Username: " + username + "\n" +
                            "Password: " + password + "\n\n" +
                            "⚠️ Vui lòng ghi lại thông tin này và bàn giao cho khách hàng!";
                    } else {
                        successMsg += "\n\n⚠️ Không thể tạo tài khoản (có thể username đã tồn tại: " + username + ")";
                    }
                }
                
                JOptionPane.showMessageDialog(this, successMsg, 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Tạo hợp đồng thất bại. Có thể phòng đã được thuê hoặc có lỗi hệ thống.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private Optional<Integer> getSelectedContractId() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hợp đồng.");
            return Optional.empty();
        }
        return Optional.of((Integer) tableModel.getValueAt(sel, 0));
    }

    private void showDetailDialog() {
        Optional<Integer> maybe = getSelectedContractId();
        if (maybe.isEmpty()) {
            return;
        }
        int id = maybe.get();
        HopDong h = hopDongService.findById(id);
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng.");
            return;
        }

        Phong p = phongService.findById(h.getIdPhong());
        KhachHang kh = khachHangService.findById(h.getIdKhachHang());

        StringBuilder sb = new StringBuilder();
        sb.append("Hợp đồng ID: ").append(h.getIdHopDong()).append("\n");
        sb.append("Phòng: ").append(p != null ? p.getTenPhong() : ("#" + h.getIdPhong())).append("\n");
        sb.append("Khách: ").append(kh != null ? kh.getTenKhachHang() : ("#" + h.getIdKhachHang())).append("\n\n");
        sb.append("Bắt đầu: ").append(h.getNgayBatDau()).append("\n");
        sb.append("Kết thúc: ").append(h.getNgayKetThuc() != null ? h.getNgayKetThuc() : "(không xác định)").append("\n");
        sb.append("Tiền cọc: ").append(formatMoney(h.getTienCoc())).append("\n");
        sb.append("Trạng thái: ").append(h.getTrangThai()).append("\n\n");

        // thêm các thao tác liên quan: xem hóa đơn (nếu đã có), xem lịch sử phòng
        int opt = JOptionPane.showOptionDialog(this, sb.toString(), "Chi tiết hợp đồng",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, new String[]{"Đóng", "Xem hóa đơn (mẫu)", "Lịch sử phòng"}, "Đóng");

        if (opt == 1) {
            // show invoice mock / hoặc mở HoaDonFrame -> hiển thị cho phòng + tháng hiện tại
            // mở màn hình hóa đơn đã có, lọc theo phòng + tháng hiện tại
            com.ptpmud.quanlynhatro.view.HoaDonGD frame = new com.ptpmud.quanlynhatro.view.HoaDonGD();
            javax.swing.JFrame container = new javax.swing.JFrame("Hóa đơn phòng #" + h.getIdPhong());
            container.setContentPane(frame);
            container.pack();
            container.setSize(1100, 700);
            container.setLocationRelativeTo(this);
            container.setVisible(true);
        } else if (opt == 2) {
            showHistoryByPhong(h.getIdPhong());
        }
    }

    private void endSelectedContract() {
        Optional<Integer> maybe = getSelectedContractId();
        if (maybe.isEmpty()) {
            return;
        }
        int id = maybe.get();
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn kết thúc hợp đồng ID=" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = hopDongService.endContract(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã kết thúc hợp đồng.");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Kết thúc hợp đồng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renewSelectedContract() {
        Optional<Integer> maybe = getSelectedContractId();
        if (maybe.isEmpty()) {
            return;
        }
        int id = maybe.get();
        HopDong h = hopDongService.findById(id);
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng.");
            return;
        }
        String s = JOptionPane.showInputDialog(this, "Nhập ngày kết thúc mới (YYYY-MM-DD):", LocalDate.now().plusMonths(1).toString());
        if (s == null || s.trim().isEmpty()) {
            return;
        }
        try {
            LocalDate newEnd = LocalDate.parse(s.trim());
            boolean ok = hopDongService.renewContract(id, newEnd);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Gia hạn hợp đồng thành công.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gia hạn thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày không đúng định dạng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHistoryForSelectedRoom() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một hợp đồng (hoặc phòng) để xem lịch sử.");
            return;
        }
        int idPhong = (int) tableModel.getValueAt(sel, 1);
        showHistoryByPhong(idPhong);
    }

    private void showHistoryByPhong(int idPhong) {
        List<HopDong> list = hopDongService.findHistoryByPhong(idPhong);
        if (list == null || list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có lịch sử hợp đồng cho phòng #" + idPhong);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Lịch sử hợp đồng phòng #").append(idPhong).append(":\n\n");
        for (HopDong h : list) {
            KhachHang kh = khachHangService.findById(h.getIdKhachHang());
            sb.append("HD#").append(h.getIdHopDong())
                        .append(" | Khách: ").append(kh != null ? kh.getTenKhachHang() : ("#" + h.getIdKhachHang()))
                        .append(" | BĐ: ").append(h.getNgayBatDau())
                        .append(" | KT: ").append(h.getNgayKetThuc() != null ? h.getNgayKetThuc() : "(n/a)")
                        .append(" | Trạng thái: ").append(h.getTrangThai())
                        .append("\n");
        }
        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setRows(12);
        ta.setColumns(60);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Lịch sử hợp đồng", JOptionPane.INFORMATION_MESSAGE);
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
        btnCreate = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        btnRenew = new javax.swing.JButton();
        btnEnd = new javax.swing.JButton();
        btnHistory = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cbFilter = new javax.swing.JComboBox<>();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        lblInfor = new javax.swing.JLabel();

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

        btnCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addv2.png"))); // NOI18N
        btnCreate.setText("Tạo mới");

        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eye.png"))); // NOI18N
        btnView.setText("Xem");

        btnRenew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/renew.png"))); // NOI18N
        btnRenew.setText("Gia hạn");

        btnEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        btnEnd.setText("Kết thúc");

        btnHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/history.png"))); // NOI18N
        btnHistory.setText("Lịch sử phòng");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnView, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRenew, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHistory)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRenew)
                    .addComponent(btnView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHistory))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        cbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "dangThue", "daKetThuc" }));
        cbFilter.addActionListener(this::cbFilterActionPerformed);

        btnSearch.setText("🔍 Tìm");
        btnSearch.addActionListener(this::btnSearchActionPerformed);

        btnReload.setText("Tải lại");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(cbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReload)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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

        lblInfor.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblInfor, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfor)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbFilterActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnEnd;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnRenew;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox<String> cbFilter;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblInfor;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
