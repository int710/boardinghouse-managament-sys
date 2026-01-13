package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.controller.PhongController;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.service.PhongService;
import com.ptpmud.quanlynhatro.utils.*;
import static com.ptpmud.quanlynhatro.utils.Utils.findComponentByName;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class PhongFrame extends javax.swing.JPanel {
    private final PhongController controller = new PhongController();
    private final NumberFormat moneyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private DefaultTableModel tableModel;

    public PhongFrame() {
        initComponents();
        setupTable();
        setupActions();
        loadData(); // Tải dữ liệu lần đầu
    }

    private void setupTable() {
        String[] cols = {"ID", "Tên phòng", "Loại", "Diện tích", "Giá thuê", "Trạng thái", "Khách thuê"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        table.setModel(tableModel); // Gán model thực sự vào table
        table.setRowHeight(28);  
    }

    private void setupActions() {
        btnAdd.addActionListener(e -> showAddDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnEdit.addActionListener(e -> showEditDialog());
        btnView.addActionListener(e-> viewInfo());
        cbFilter.addActionListener(e -> loadData());
        btnSearch.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            cbFilter.setSelectedIndex(0);
            loadData();
        });
        btnGanKhach.addActionListener(e -> showAssignDialog());
    }
    
    public void loadData() {
        new SwingWorker<List<Phong>, Void>() {
            List<Phong> list;

            @Override
            protected List<Phong> doInBackground() throws Exception {
                String filter = (String) cbFilter.getSelectedItem();
                String keyword = txtSearch.getText().trim();

                if (!"Tất cả".equals(filter) && (keyword.isEmpty())) {
                    list = controller.loadByStatus(filter);
                } else if (!keyword.isEmpty()) {
                    list = controller.search(keyword);
                } else {
                    list = controller.loadAll();
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    list = get();
                    tableModel.setRowCount(0);
                    for (Phong p : list) {
                        String tenantLabel = "";
                        if ("dangThue".equalsIgnoreCase(p.getTrangThai())) {
                            var info = controller.getTenantInfoByPhong(p.getIdPhong());
                            tenantLabel = (info != null && info.khachHang != null)
                                    ? info.khachHang.getTenKhachHang()
                                    : "(đang thuê)";
                        }

                        tableModel.addRow(new Object[]{
                            p.getIdPhong(),
                            p.getTenPhong(),
                            p.getLoaiPhong(),
                            p.getDienTich() + " m²",
                            moneyFmt.format(p.getGiaThue()),
                            formatStatus(p.getTrangThai()),
                            tenantLabel
                        });
                    }
                    updateStats();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private String formatStatus(String status) {
        if (status == null) return "";
        String lower = status.toLowerCase();
        return switch (lower) {
            case "trong" -> "Trống";
            case "dangthue", "dathue" -> "Đang thuê";
            case "baotri" -> "Bảo trì";
            default -> status;
        };
    }

    private void updateStats() {
        int trong = controller.countStatus("trong");
        int dangThue = controller.countStatus("dangThue");
        int baoTri = controller.countStatus("baoTri");
        int tong = trong + dangThue + baoTri;
        txtInfo.setText(String.format("Tổng: %d | Trống: %d | Đang thuê: %d | Bảo trì: %d", tong, trong, dangThue, baoTri));
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
    
    private void viewInfo() {
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
    
     private void showAssignDialog() {
        int sel = table.getSelectedRow();
        if (sel < 0) { 
            JOptionPane.showMessageDialog(this, "Chọn phòng để gán khách."); 
            return; 
        }
        int idPhong = (int) tableModel.getValueAt(sel, 0);
        
        // Lấy trạng thái gốc từ Phong object để đảm bảo chính xác
        Phong phong = controller.loadAll().stream()
            .filter(p -> p.getIdPhong() == idPhong)
            .findFirst()
            .orElse(null);
        
        if (phong == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String status = phong.getTrangThai();
        
        // Chỉ cho gán khách nếu phòng trống (kiểm tra trạng thái gốc từ database)
        if ("dangThue".equalsIgnoreCase(status) || "daThue".equalsIgnoreCase(status)) { 
            JOptionPane.showMessageDialog(this, "Phòng đã đang thuê, không thể gán khách mới!", "Thông báo", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        if ("baoTri".equalsIgnoreCase(status)) { 
            JOptionPane.showMessageDialog(this, "Phòng đang bảo trì, không thể gán khách!", "Thông báo", JOptionPane.WARNING_MESSAGE); 
            return; 
        }

        // Lấy danh sách khách hàng có thể gán (không có hợp đồng đang thuê)
        List<com.ptpmud.quanlynhatro.model.KhachHang> availableCustomers = controller.getAvailableCustomers();
        if (availableCustomers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có khách hàng nào có thể gán.\nTất cả khách hàng đều đang có hợp đồng thuê hoặc chưa có khách hàng nào trong hệ thống.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build dialog fields
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.gridy=0;
        panel.add(new JLabel("ID Phòng:"), c);
        c.gridx=1; panel.add(new JLabel(String.valueOf(idPhong)), c);

        c.gridx=0; c.gridy++; panel.add(new JLabel("Chọn khách hàng:"), c);
        JComboBox<String> cbKhachHang = new JComboBox<>();
        for (com.ptpmud.quanlynhatro.model.KhachHang kh : availableCustomers) {
            String label = String.format("ID:%d - %s%s%s", 
                kh.getIdKhachHang(), 
                kh.getTenKhachHang(),
                kh.getSoDienThoai() != null && !kh.getSoDienThoai().isEmpty() ? " - " + kh.getSoDienThoai() : "",
                kh.getEmail() != null && !kh.getEmail().isEmpty() ? " - " + kh.getEmail() : ""
            );
            cbKhachHang.addItem(label);
        }
        c.gridx=1; panel.add(cbKhachHang, c);

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
            // Lấy ID khách hàng từ combobox
            int selectedIndex = cbKhachHang.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idKh = availableCustomers.get(selectedIndex).getIdKhachHang();
            
            LocalDate bd = LocalDate.parse(txtNgayBD.getText().trim());
            LocalDate kt = null;
            if (!txtNgayKT.getText().trim().isEmpty()) kt = LocalDate.parse(txtNgayKT.getText().trim());
            double coc = Double.parseDouble(txtCoc.getText().trim());
            
            // Call controller assign
            PhongService.AssignResult res = controller.assignTenantToPhong(idPhong, idKh, bd, kt, coc, cbCreateAccount.isSelected());
            if (res == null) {
                JOptionPane.showMessageDialog(this, "Lỗi không xác định.");
            } else if (!res.success) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + res.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                String msg = res.message;
                if (res.username != null) {
                    msg += "\n\nTài khoản đã tạo:\nUsername: " + res.username + "\nMật khẩu: " + res.plainPassword + "\n\n(Vui lòng đổi mật khẩu ngay khi bàn giao)";
                }
                JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        btnAdd = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cbFilter = new javax.swing.JComboBox<>();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        txtInfo = new javax.swing.JLabel();
        btnGanKhach = new javax.swing.JButton();

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

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnAdd.setText("Thêm");

        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/view.png"))); // NOI18N
        btnView.setText("Xem");

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        btnEdit.setText("Sửa");

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnDelete.setText("Xóa");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnView)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEdit)
                .addGap(18, 18, 18)
                .addComponent(btnDelete)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnView)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete))
                .addGap(2, 2, 2))
        );

        cbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "dangThue", "trong", "baoTri" }));
        cbFilter.setToolTipText("Bộ lọc");
        cbFilter.setMinimumSize(new java.awt.Dimension(88, 20));
        cbFilter.addActionListener(this::cbFilterActionPerformed);

        txtSearch.setToolTipText("Nhập bất kì thông tin phòng cần tìm");
        txtSearch.addActionListener(this::txtSearchActionPerformed);

        btnSearch.setText("🔍 Tìm");
        btnSearch.setToolTipText("Tìm kiếm");
        btnSearch.addActionListener(this::btnSearchActionPerformed);

        btnReload.setText("⟳");
        btnReload.setToolTipText("Làm mới");
        btnReload.setAlignmentY(0.0F);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(cbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
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

        txtInfo.setText("Information");

        btnGanKhach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/adduser.png"))); // NOI18N
        btnGanKhach.setText("Gán khách ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 904, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(btnGanKhach)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtInfo)
                    .addComponent(btnGanKhach))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbFilterActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnGanKhach;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox<String> cbFilter;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable table;
    private javax.swing.JLabel txtInfo;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
