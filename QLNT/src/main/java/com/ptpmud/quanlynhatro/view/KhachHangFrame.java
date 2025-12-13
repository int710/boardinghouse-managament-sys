package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.dao.KhachHangDAO;
import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.utils.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class KhachHangFrame extends javax.swing.JPanel {

    private final KhachHangDAO dao = new KhachHangDAO();

    private final NumberFormat moneyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private DefaultTableModel tableModel;

    public KhachHangFrame() {
        initComponents();
        setupTable();
        setupActions();
        loadData(); // Tải dữ liệu lần đầu
    }

    private void setupTable() {
        String[] cols = {"ID", "Họ tên", "CCCD", "SĐT", "Phòng (nếu có)", "Ngày sinh", "Giới tính", "Ghi chú"};
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
        btnAdd.addActionListener(e -> showDialog(null));
        btnDelete.addActionListener(e -> deleteSelected());
        btnEdit.addActionListener(e -> editSelected());
        btnView.addActionListener(e -> viewContractsSelected());
        cbFilter.addActionListener(e -> loadData());
        btnSearch.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            cbFilter.setSelectedIndex(0);
            loadData();
        });
        btnGanKhach.addActionListener(e -> assignRoomToSelected());
    }

    public void loadData() {
        SwingWorker<List<KhachHang>, Void> w = new SwingWorker<>() {
            @Override
            protected List<KhachHang> doInBackground() {
                String keyword = txtSearch.getText().trim();
                String filter = (String) cbFilter.getSelectedItem();
                try {
                    if (!keyword.isEmpty()) {
                        return dao.findAll().stream()
                                    .filter(k -> k.getTenKhachHang().toLowerCase().contains(keyword.toLowerCase())
                                    || (k.getSoCccd() != null && k.getSoCccd().contains(keyword)))
                                    .toList();
                    } else if ("Nam".equals(filter) || "Nữ".equals(filter)) {
                        return dao.findAll().stream()
                                    .filter(k -> filter.equalsIgnoreCase(k.getGioiTinh()))
                                    .toList();
                    } else {
                        return dao.findAll();
                    }
                } catch (Exception ex) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    List<KhachHang> list = get();
                    tableModel.setRowCount(0);
                    for (KhachHang k : list) {
                        // try to fetch current room for this tenant (if any) via HopDongThue
                        String phong = dao.fetchPhongForKhach(k.getIdKhachHang());
                        tableModel.addRow(new Object[]{
                            k.getIdKhachHang(),
                            k.getTenKhachHang(),
                            k.getSoCccd(),
                            k.getSoDienThoai(),
                            phong,
                            k.getNgaySinh() != null ? k.getNgaySinh().toString() : "",
                            k.getGioiTinh(),
                            k.getGhiChu()
                        });
                    }
                    labelInfo.setText("Tổng: " + list.size());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private void deleteSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn khách để xóa.");
            return;
        }
        int id = (int) tableModel.getValueAt(sel, 0);
        if (JOptionPane.showConfirmDialog(this, "Bạn có muốn xóa khách ID=" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            // dao currently doesn't have delete in your snippet; try to call via SQL
            boolean ok = dao.deleteKhachById(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa thành công.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại. Kiểm tra ràng buộc (hợp đồng...).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Create 
    private void showDialog(KhachHang existing) {
        KhachHangDialog d = new KhachHangDialog(SwingUtilities.getWindowAncestor(this), existing);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        if (d.isSaved()) {
            loadData();
        }
    }

    private class KhachHangDialog extends JDialog {

        private boolean saved = false;
        private KhachHang current;

        private final JTextField txtName = new JTextField(25);
        private final JTextField txtCccd = new JTextField(18);
        private final JTextField txtPhone = new JTextField(14);
        private final JTextField txtQue = new JTextField(20);
        private final JTextField txtNghe = new JTextField(20);
        private final JTextField txtBirth = new JTextField(12); // YYYY-MM-DD
        private final JComboBox<String> cbGender = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        private final JTextArea taNote = new JTextArea(4, 30);

        KhachHangDialog(Window owner, KhachHang exist) {
            super(owner, (exist == null ? "Thêm khách thuê" : "Sửa khách thuê"), ModalityType.APPLICATION_MODAL);
            this.current = exist;
            initUI();
            if (exist != null) {
                fillForm(exist);
            }
            pack();
        }

        private void initUI() {
            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(new EmptyBorder(12, 12, 12, 12));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 6, 6, 6);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            p.add(new JLabel("Họ và tên *:"), c);
            c.gridx = 1;
            p.add(txtName, c);
            c.gridx = 0;
            c.gridy++;
            p.add(new JLabel("Số CCCD:"), c);
            c.gridx = 1;
            p.add(txtCccd, c);
            c.gridx = 0;
            c.gridy++;
            p.add(new JLabel("SĐT:"), c);
            c.gridx = 1;
            p.add(txtPhone, c);
            c.gridx = 0;
            c.gridy++;
            p.add(new JLabel("Quê quán:"), c);
            c.gridx = 1;
            p.add(txtQue, c);
            c.gridx = 0;
            c.gridy++;
            p.add(new JLabel("Nghề nghiệp:"), c);
            c.gridx = 1;
            p.add(txtNghe, c);
            c.gridx = 0;
            c.gridy++;
            p.add(new JLabel("Ngày sinh (YYYY-MM-DD):"), c);
            c.gridx = 1;
            p.add(txtBirth, c);
            c.gridx = 0;
            c.gridy++;
            p.add(new JLabel("Giới tính:"), c);
            c.gridx = 1;
            p.add(cbGender, c);
            c.gridx = 0;
            c.gridy++;
            p.add(new JLabel("Ghi chú:"), c);
            c.gridx = 1;
            p.add(new JScrollPane(taNote), c);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton ok = new JButton("Lưu");
            ok.addActionListener(e -> onSave());
            JButton cancel = new JButton("Hủy");
            cancel.addActionListener(e -> dispose());
            btns.add(ok);
            btns.add(cancel);

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(p, BorderLayout.CENTER);
            getContentPane().add(btns, BorderLayout.SOUTH);
            setResizable(false);
        }

        private void fillForm(KhachHang k) {
            txtName.setText(k.getTenKhachHang());
            txtCccd.setText(k.getSoCccd());
            txtPhone.setText(k.getSoDienThoai());
            txtQue.setText(k.getQueQuan());
            txtNghe.setText(k.getNgheNghiep());
            if (k.getNgaySinh() != null) {
                txtBirth.setText(k.getNgaySinh().toString());
            }
            if (k.getGioiTinh() != null) {
                cbGender.setSelectedItem(k.getGioiTinh());
            }
            taNote.setText(k.getGhiChu());
        }

        private void onSave() {
            // validation
            String ten = txtName.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên khách không được rỗng.");
                return;
            }

            String cccd = txtCccd.getText().trim();
            if (!cccd.isEmpty() && (cccd.length() < 6 || cccd.length() > 20)) {
                JOptionPane.showMessageDialog(this, "CCCD/CMND không hợp lệ (6-20 ký tự) hoặc để trống.");
                return;
            }
            String phone = txtPhone.getText().trim();
            if (!phone.isEmpty() && !phone.matches("[0-9+\\- ]{7,16}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ.");
                return;
            }
            java.sql.Date ngaySinh = null;
            if (!txtBirth.getText().trim().isEmpty()) {
                try {
                    ngaySinh = java.sql.Date.valueOf(LocalDate.parse(txtBirth.getText().trim()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng YYYY-MM-DD.");
                    return;
                }
            }

            // check unique CCCD if new or changed
            if (!cccd.isEmpty()) {
                List<KhachHang> all = dao.findAll();
                for (KhachHang k : all) {
                    if (k.getSoCccd() != null && k.getSoCccd().equals(cccd)) {
                        if (current == null || k.getIdKhachHang() != current.getIdKhachHang()) {
                            JOptionPane.showMessageDialog(this, "CCCD đã tồn tại trong hệ thống.");
                            return;
                        }
                    }
                }
            }

            // build model
            KhachHang k = (current == null) ? new KhachHang() : current;
            k.setTenKhachHang(ten);
            k.setSoCccd(cccd.isEmpty() ? null : cccd);
            k.setSoDienThoai(phone.isEmpty() ? null : phone);
            k.setQueQuan(txtQue.getText().trim());
            k.setNgheNghiep(txtNghe.getText().trim());
            k.setNgaySinh(ngaySinh);
            k.setGioiTinh((String) cbGender.getSelectedItem());
            k.setGhiChu(taNote.getText().trim());

            boolean ok;
            if (current == null) {
                ok = dao.insert(k);
            } else {
                // dao.update not provided in snippet earlier; implement simple update here
                ok = dao.update(k);
            }

            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu thất bại. Kiểm tra log.");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }

    // view
    private void viewContractsSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn khách để xem hợp đồng.");
            return;
        }
        int idKh = (int) tableModel.getValueAt(sel, 0);
        List<String> contracts = dao.fetchContractsByCustomer(idKh);
        if (contracts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có hợp đồng nào cho khách này.");
            return;
        }
        JTextArea ta = new JTextArea(String.join("\n\n", contracts));
        ta.setEditable(false);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(700, 300));
        JOptionPane.showMessageDialog(this, sp, "Hợp đồng của khách ID=" + idKh, JOptionPane.INFORMATION_MESSAGE);
    }
    
     private void editSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn khách để sửa.");
            return;
        }
        int id = (int) tableModel.getValueAt(sel, 0);
        KhachHang k = dao.findById(id);
        if (k == null) {
            JOptionPane.showMessageDialog(this, "Khách thuê không tồn tại.");
            loadData();
            return;
        }
        showDialog(k);
    }
    
     private void assignRoomToSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn khách để gán phòng.");
            return;
        }
        int idKh = (int) tableModel.getValueAt(sel, 0);

        // Check existing active contract for this customer
        if (dao.hasActiveContract(idKh)) {
            JOptionPane.showMessageDialog(this, "Khách này đang có hợp đồng đang thuê. Hủy hợp đồng trước khi gán phòng mới.");
            return;
        }

        // fetch available rooms (status = 'trong')
        List<RoomOption> rooms = fetchAvailableRooms();
        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có phòng trống để gán.");
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Chọn phòng:"), c);
        JComboBox<RoomOption> cbRooms = new JComboBox<>(rooms.toArray(new RoomOption[0]));
        cbRooms.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof RoomOption) setText(((RoomOption)value).label);
                return this;
            }
        });
        c.gridx = 1; panel.add(cbRooms, c);
        c.gridx = 0; c.gridy++;
        panel.add(new JLabel("Ngày bắt đầu (YYYY-MM-DD):"), c);
        JTextField txtStart = new JTextField(LocalDate.now().toString(), 12);
        c.gridx = 1; panel.add(txtStart, c);
        c.gridx = 0; c.gridy++;
        panel.add(new JLabel("Ngày kết thúc (YYYY-MM-DD) hoặc để trống:"), c);
        JTextField txtEnd = new JTextField(12);
        c.gridx = 1; panel.add(txtEnd, c);
        c.gridx = 0; c.gridy++;
        panel.add(new JLabel("Tiền cọc (VNĐ):"), c);
        JTextField txtCoc = new JTextField("0", 12);
        c.gridx = 1; panel.add(txtCoc, c);

        int r = JOptionPane.showConfirmDialog(this, panel, "Gán phòng cho khách", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            RoomOption chosen = (RoomOption) cbRooms.getSelectedItem();
            if (chosen == null) return;
            try {
                LocalDate ngayBatDau = LocalDate.parse(txtStart.getText().trim());
                LocalDate ngayKetThuc = null;
                if (!txtEnd.getText().trim().isEmpty()) ngayKetThuc = LocalDate.parse(txtEnd.getText().trim());
                double tienCoc = Double.parseDouble(txtCoc.getText().trim());

                boolean ok = dao.createHopDongAndAssignRoom(chosen.idPhong, idKh, ngayBatDau, ngayKetThuc, tienCoc);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Gán phòng thành công.");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gán phòng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class RoomOption {
        final int idPhong;
        final String label;
        RoomOption(int id, String label) { this.idPhong = id; this.label = label; }
        @Override public String toString() { return label; }
    }
    
    private List<RoomOption> fetchAvailableRooms() {
        List<RoomOption> list = new ArrayList<>();
        String sql = "SELECT idPhong, tenPhong FROM Phong WHERE trangThai = 'trong' ORDER BY tenPhong";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new RoomOption(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    private int promptCreateKhach() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Họ tên:"), c);
        JTextField tName = new JTextField();
        c.gridx = 1;
        p.add(tName, c);

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("SĐT:"), c);
        JTextField tPhone = new JTextField();
        c.gridx = 1;
        p.add(tPhone, c);

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("CCCD:"), c);
        JTextField tCccd = new JTextField();
        c.gridx = 1;
        p.add(tCccd, c);

        int r = JOptionPane.showConfirmDialog(this, p, "Tạo Khách hàng mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            throw new RuntimeException("Hủy tạo khách");
        }
        // insert KhachHang minimal via DAO
        com.ptpmud.quanlynhatro.dao.KhachHangDAO dao = new com.ptpmud.quanlynhatro.dao.KhachHangDAO();
        com.ptpmud.quanlynhatro.model.KhachHang kh = new com.ptpmud.quanlynhatro.model.KhachHang();
        kh.setTenKhachHang(tName.getText().trim());
        kh.setSoDienThoai(tPhone.getText().trim());
        kh.setSoCccd(tCccd.getText().trim());
        boolean ok = dao.insert(kh);
        if (!ok) {
            throw new RuntimeException("Không thể tạo KhachHang mới");
        }
        return kh.getIdKhachHang();
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
        labelInfo = new javax.swing.JLabel();
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

        cbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Nam", "Nữ" }));
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

        labelInfo.setText("Information");

        btnGanKhach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/adduser.png"))); // NOI18N
        btnGanKhach.setText("Gán phòng");

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
                        .addComponent(labelInfo)))
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
                    .addComponent(labelInfo)
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
    private javax.swing.JLabel labelInfo;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
