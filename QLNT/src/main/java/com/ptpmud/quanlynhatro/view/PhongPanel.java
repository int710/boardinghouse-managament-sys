package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.controller.PhongController;
import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.service.PhongService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PhongPanel extends JPanel {
    private final PhongController controller = new PhongController();   
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JComboBox<String> cbFilter;
    private final JTextField txtSearch;
    private final JLabel lblCountTrong;
    private final JLabel lblCountDaThue;
    private final DecimalFormat moneyFmt = new DecimalFormat("#,###");

    public PhongPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        JLabel title = new JLabel("Quản lý Phòng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        top.add(title, BorderLayout.WEST);
        // Right controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        cbFilter = new JComboBox<>(new String[]{"Tất cả", "trong", "dangThue", "baoTri"});
        cbFilter.setToolTipText("Lọc theo trạng thái");
        cbFilter.addActionListener(e -> loadData());
        controls.add(cbFilter);
        txtSearch = new JTextField(18);
        txtSearch.setToolTipText("Tìm theo tên phòng");
        controls.add(txtSearch);
        JButton btnSearch = new JButton("🔍 Tìm");
        btnSearch.addActionListener(e -> loadData());
        controls.add(btnSearch);
        JButton btnRefresh = new JButton("⟳");
        btnRefresh.setToolTipText("Làm mới");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbFilter.setSelectedIndex(0);
            loadData();
        });
        controls.add(btnRefresh);
        top.add(controls, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Tên phòng", "Loại", "Diện tích", "Giá thuê", "Trạng thái", "Khách thuê"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: actions + stats
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setOpaque(false);
        JButton btnAdd = new JButton("➕ Thêm");
        btnAdd.addActionListener(e -> showAddDialog());
        JButton btnEdit = new JButton("✏ Sửa");
        btnEdit.addActionListener(e -> showEditDialog());
        JButton btnDelete = new JButton("🗑 Xóa");
        btnDelete.addActionListener(e -> deleteSelected());
        JButton btnAssign = new JButton("🔗 Gán khách");
        btnAssign.addActionListener(e -> showAssignDialog());
        JButton btnViewTenant = new JButton("👁 Xem khách thuê");
        btnViewTenant.addActionListener(e -> viewTenant());
        actions.add(btnAdd); actions.add(btnEdit); actions.add(btnDelete);
        actions.add(btnAssign); actions.add(btnViewTenant);
        bottom.add(actions, BorderLayout.WEST);

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        stats.setOpaque(false);
        lblCountTrong = new JLabel("Trống: 0");
        lblCountDaThue = new JLabel("Đã thuê: 0");
        stats.add(lblCountTrong); stats.add(Box.createHorizontalStrut(12)); stats.add(lblCountDaThue);
        bottom.add(stats, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        // double click to edit / show tenant
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && e.getClickCount() == 2) {
                    String status = (String) tableModel.getValueAt(row, 5);
                    if ("dangThue".equalsIgnoreCase(status)) viewTenant();
                    else showEditDialog();
                }
            }
        });

        loadData();
    }

    private void styleTable(JTable t) {
        t.setRowHeight(32);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setSelectionBackground(new Color(200, 230, 255));
    }

    public void loadData() {
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            List<Phong> list;
            @Override protected Void doInBackground() {
                String filter = (String) cbFilter.getSelectedItem();
                String keyword = txtSearch.getText().trim();
                if (!"Tất cả".equals(filter) && (keyword == null || keyword.isEmpty())) list = controller.loadByStatus(filter);
                else if (keyword != null && !keyword.isEmpty()) list = controller.search(keyword);
                else list = controller.loadAll();
                return null;
            }
            @Override protected void done() {
                tableModel.setRowCount(0);
                for (Phong p : list) {
                    // show tenant placeholder: load tenant info only for dangThue to avoid many DB calls
                    String tenantLabel = "";
                    if ("dangThue".equalsIgnoreCase(p.getTrangThai())) {
                        PhongService.TenantInfo info = controller.getTenantInfoByPhong(p.getIdPhong());
                        if (info != null && info.khachHang != null) tenantLabel = info.khachHang.getTenKhachHang();
                        else tenantLabel = "(đang thuê)";
                    }
                    tableModel.addRow(new Object[]{
                        p.getIdPhong(),
                        p.getTenPhong(),
                        p.getLoaiPhong(),
                        p.getDienTich(),
                        moneyFmt.format(p.getGiaThue()),
                        p.getTrangThai(),
                        tenantLabel
                    });
                }
                updateStats();
            }
        };
        w.execute();
    }

    private void updateStats() {
        int cTrong = controller.countStatus("trong");
        int cDa = controller.countStatus("dangThue");
        lblCountTrong.setText("Trống: " + cTrong);
        lblCountDaThue.setText("Đã thuê: " + cDa);
    }

    private void showAddDialog() {
        JPanel form = makeForm(null);
        int r = JOptionPane.showConfirmDialog(this, form, "Thêm phòng mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            Phong p = readForm(form, null);
            if (p != null) {
                boolean ok = controller.createPhong(p);
                if (ok) { JOptionPane.showMessageDialog(this, "Tạo phòng thành công."); loadData(); }
                else JOptionPane.showMessageDialog(this, "Tạo phòng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn phòng để sửa."); return; }
        int id = (int) tableModel.getValueAt(sel, 0);
        Phong exist = controller.loadAll().stream().filter(p -> p.getIdPhong() == id).findFirst().orElse(null);
        if (exist == null) { JOptionPane.showMessageDialog(this, "Phòng không tồn tại."); loadData(); return; }
        JPanel form = makeForm(exist);
        int r = JOptionPane.showConfirmDialog(this, form, "Sửa phòng", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            Phong updated = readForm(form, exist);
            if (updated != null) {
                boolean ok = controller.updatePhong(updated);
                if (ok) { JOptionPane.showMessageDialog(this, "Cập nhật thành công."); loadData(); }
                else JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn phòng để xóa."); return; }
        int id = (int) tableModel.getValueAt(sel, 0);
        if (JOptionPane.showConfirmDialog(this, "Bạn có muốn xóa phòng ID=" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            boolean ok = controller.removePhong(id);
            if (ok) { JOptionPane.showMessageDialog(this, "Xóa thành công."); loadData(); }
            else JOptionPane.showMessageDialog(this, "Xóa thất bại. Kiểm tra ràng buộc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Dialog chọn Khách và tạo hợp đồng + tùy chọn tạo tài khoản */
    private void showAssignDialog() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn phòng để gán khách."); return; }
        int idPhong = (int) tableModel.getValueAt(sel, 0);
        String status = (String) tableModel.getValueAt(sel, 5);
        if ("baoTri".equalsIgnoreCase(status)) { JOptionPane.showMessageDialog(this, "Phòng đang bảo trì, không thể gán khách."); return; }
        if ("dangThue".equalsIgnoreCase(status)) { JOptionPane.showMessageDialog(this, "Phòng đã đang thuê."); return; }

        // Build dialog fields
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.gridy=0;
        panel.add(new JLabel("ID Phòng:"), c);
        c.gridx=1; panel.add(new JLabel(String.valueOf(idPhong)), c);

        c.gridx=0; c.gridy++; panel.add(new JLabel("ID Khách (existing):"), c);
        JTextField txtKhId = new JTextField(); txtKhId.setToolTipText("Nhập ID KhachHang (hoặc để trống để tạo mới)"); c.gridx=1; panel.add(txtKhId, c);

        c.gridx=0; c.gridy++; panel.add(new JLabel("Ngày bắt đầu (YYYY-MM-DD):"), c);
        JTextField txtNgayBD = new JTextField(LocalDate.now().toString()); c.gridx=1; panel.add(txtNgayBD, c);

        c.gridx=0; c.gridy++; panel.add(new JLabel("Ngày kết thúc (opt):"), c);
        JTextField txtNgayKT = new JTextField(); c.gridx=1; panel.add(txtNgayKT, c);

        c.gridx=0; c.gridy++; panel.add(new JLabel("Tiền cọc:"), c);
        JTextField txtCoc = new JTextField("0"); c.gridx=1; panel.add(txtCoc, c);

        c.gridx=0; c.gridy++; panel.add(new JLabel("Tạo tài khoản cho khách?"), c);
        JCheckBox cbCreateAccount = new JCheckBox(); c.gridx=1; panel.add(cbCreateAccount, c);

        int r = JOptionPane.showConfirmDialog(this, panel, "Gán khách vào phòng", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        try {
            Integer idKh = null;
            String sKh = txtKhId.getText().trim();
            if (!sKh.isEmpty()) idKh = Integer.parseInt(sKh);
            LocalDate bd = LocalDate.parse(txtNgayBD.getText().trim());
            LocalDate kt = null;
            if (!txtNgayKT.getText().trim().isEmpty()) kt = LocalDate.parse(txtNgayKT.getText().trim());
            double coc = Double.parseDouble(txtCoc.getText().trim());
            // Call controller assign
            PhongService.AssignResult res = controller.assignTenantToPhong(idPhong, idKh != null ? idKh : promptCreateKhach(), bd, kt, coc, cbCreateAccount.isSelected());
            if (res == null) {
                JOptionPane.showMessageDialog(this, "Lỗi không xác định.");
            } else if (!res.success) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + res.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                String msg = res.message;
                if (res.username != null) msg += "\nTài khoản đã tạo: " + res.username + " / mật khẩu: " + res.plainPassword + "\n(đổi mật khẩu ngay khi bàn giao)";
                JOptionPane.showMessageDialog(this, msg);
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** If admin didn't provide existing KhachHang ID, open small form to create new KhachHang and return its ID. */
    private int promptCreateKhach() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; p.add(new JLabel("Họ tên:"), c);
        JTextField tName = new JTextField(); c.gridx=1; p.add(tName, c);

        c.gridx=0; c.gridy++; p.add(new JLabel("SĐT:"), c);
        JTextField tPhone = new JTextField(); c.gridx=1; p.add(tPhone, c);

        c.gridx=0; c.gridy++; p.add(new JLabel("CCCD:"), c);
        JTextField tCccd = new JTextField(); c.gridx=1; p.add(tCccd, c);

        int r = JOptionPane.showConfirmDialog(this, p, "Tạo Khách hàng mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) throw new RuntimeException("Hủy tạo khách");
        // insert KhachHang minimal via DAO
        com.ptpmud.quanlynhatro.dao.KhachHangDAO dao = new com.ptpmud.quanlynhatro.dao.KhachHangDAO();
        com.ptpmud.quanlynhatro.model.KhachHang kh = new com.ptpmud.quanlynhatro.model.KhachHang();
        kh.setTenKhachHang(tName.getText().trim());
        kh.setSoDienThoai(tPhone.getText().trim());
        kh.setSoCccd(tCccd.getText().trim());
        boolean ok = dao.insert(kh);
        if (!ok) throw new RuntimeException("Không thể tạo KhachHang mới");
        return kh.getIdKhachHang();
    }

    private void viewTenant() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn phòng để xem khách thuê."); return; }
        int idPhong = (int) tableModel.getValueAt(sel, 0);
        PhongService.TenantInfo info = controller.getTenantInfoByPhong(idPhong);
        if (info == null) { JOptionPane.showMessageDialog(this, "Phòng không có khách thuê hiện tại."); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("Khách: ").append(info.khachHang.getTenKhachHang()).append("\n");
        sb.append("SĐT: ").append(Optional.ofNullable(info.khachHang.getSoDienThoai()).orElse("")).append("\n");
        sb.append("CCCD: ").append(Optional.ofNullable(info.khachHang.getSoCccd()).orElse("")).append("\n\n");
        sb.append("Hợp đồng ID: ").append(info.hopDong.getIdHopDong()).append("\n");
        sb.append("Bắt đầu: ").append(info.hopDong.getNgayBatDau()).append("\n");
        sb.append("Kết thúc: ").append(Optional.ofNullable(info.hopDong.getNgayKetThuc()).orElse(null)).append("\n");
        sb.append("Tiền cọc: ").append(info.hopDong.getTienCoc()).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "Thông tin khách thuê", JOptionPane.INFORMATION_MESSAGE);
    }

    // form builder and readForm (same as before)
    private JPanel makeForm(Phong model) {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        p.add(new JLabel("Tên phòng:"), c);
        JTextField txtTen = new JTextField(); txtTen.setName("ten");
        if (model != null) txtTen.setText(model.getTenPhong());
        c.gridx = 1; c.weightx = 1; p.add(txtTen, c);
        c.gridx = 0; c.gridy++; c.weightx = 0; p.add(new JLabel("Loại phòng:"), c);
        JTextField txtLoai = new JTextField(); txtLoai.setName("loai");
        if (model != null) txtLoai.setText(model.getLoaiPhong());
        c.gridx = 1; c.weightx = 1; p.add(txtLoai, c);
        c.gridx = 0; c.gridy++; p.add(new JLabel("Diện tích (m2):"), c);
        JTextField txtDT = new JTextField(); txtDT.setName("dt");
        if (model != null) txtDT.setText(String.valueOf(model.getDienTich()));
        c.gridx = 1; p.add(txtDT, c);
        c.gridx = 0; c.gridy++; p.add(new JLabel("Giá thuê (VNĐ):"), c);
        JTextField txtGia = new JTextField(); txtGia.setName("gia");
        if (model != null) txtGia.setText(String.valueOf((long) model.getGiaThue()));
        c.gridx = 1; p.add(txtGia, c);
        c.gridx = 0; c.gridy++; p.add(new JLabel("Trạng thái:"), c);
        JComboBox<String> cbTT = new JComboBox<>(new String[]{"trong", "dangThue", "baoTri"}); cbTT.setName("trangthai");
        if (model != null) cbTT.setSelectedItem(model.getTrangThai());
        c.gridx = 1; p.add(cbTT, c);
        c.gridx = 0; c.gridy++; p.add(new JLabel("Ghi chú:"), c);
        JTextArea ta = new JTextArea(4,20); ta.setName("mota");
        if (model != null) ta.setText(model.getMoTa());
        c.gridx = 1; p.add(new JScrollPane(ta), c);
        return p;
    }

    private Phong readForm(JPanel form, Phong original) {
        try {
            JTextField ten = (JTextField) findComponentByName(form, "ten");
            JTextField loai = (JTextField) findComponentByName(form, "loai");
            JTextField dt = (JTextField) findComponentByName(form, "dt");
            JTextField gia = (JTextField) findComponentByName(form, "gia");
            JComboBox cb = (JComboBox) findComponentByName(form, "trangthai");
            JTextArea mota = (JTextArea) findComponentByName(form, "mota");
            String sTen = ten.getText().trim();
            if (sTen.isEmpty()) { JOptionPane.showMessageDialog(this, "Tên phòng không được rỗng."); return null; }
            double dDt = Double.parseDouble(dt.getText().trim());
            double dGia = Double.parseDouble(gia.getText().trim());
            Phong p = (original == null) ? new Phong() : original;
            p.setTenPhong(sTen);
            p.setLoaiPhong(loai.getText().trim());
            p.setDienTich(dDt);
            p.setGiaThue(dGia);
            p.setTrangThai(cb.getSelectedItem().toString());
            p.setMoTa(mota.getText().trim());
            return p;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private Component findComponentByName(Container root, String name) {
        for (Component c : root.getComponents()) {
            if (name.equals(c.getName())) return c;
            if (c instanceof Container) {
                Component r = findComponentByName((Container) c, name);
                if (r != null) return r;
            }
        }
        return null;
    }
}
