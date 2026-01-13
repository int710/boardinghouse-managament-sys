package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.HopDong;
import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.service.HopDongService;
import com.ptpmud.quanlynhatro.service.KhachHangService;
import com.ptpmud.quanlynhatro.service.PhongService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * HopDongFrame - quản lý Hợp đồng thuê
 * Features:
 *  - List hợp đồng
 *  - Tạo hợp đồng mới
 *  - Xem chi tiết (kèm link xem hóa đơn / lịch sử phòng)
 *  - Kết thúc hợp đồng
 *  - Gia hạn hợp đồng
 *  - Xem lịch sử hợp đồng theo phòng
 *
 * Assumes existence of:
 *  - HopDongService with methods: findAll(), findByPhong(int), findById(int),
 *    create(HopDongThue), endContract(int), renewContract(int, LocalDate), findHistoryByPhong(int)
 *  - PhongService, KhachHangService for lookups
 */
public class HopDongFrame extends JPanel {
    private final HopDongService hopDongService = new HopDongService();
    private final PhongService phongService = new PhongService();
    private final KhachHangService khachHangService = new KhachHangService();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JComboBox<String> cbFilter; // trạng thái filter
    private final JTextField txtSearch;
    private final JLabel lblInfo;

    public HopDongFrame() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(248, 249, 250));

        // Top action bar
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);

        JLabel title = new JLabel("Quản lý Hợp đồng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        top.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        cbFilter = new JComboBox<>(new String[]{"Tất cả", "dangThue", "daKetThuc"});
        cbFilter.setToolTipText("Lọc trạng thái hợp đồng");
        controls.add(cbFilter);
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Tìm theo phòng/khách (nhập ID hoặc tên)");
        controls.add(txtSearch);
        JButton btnSearch = new JButton("🔍 Tìm");
        controls.add(btnSearch);
        JButton btnReload = new JButton("⟳");
        controls.add(btnReload);
        top.add(controls, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "IdPhòng", "Tên phòng", "Khách", "Bắt đầu", "Kết thúc", "Tiền cọc", "Trạng thái", "Còn lại (ngày)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom actions
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftActions.setOpaque(false);
        JButton btnCreate = new JButton("➕ Tạo Hợp đồng");
        JButton btnView = new JButton("👁 Xem chi tiết");
        JButton btnEnd = new JButton("⛔ Kết thúc");
        JButton btnRenew = new JButton("🔁 Gia hạn");
        JButton btnHistory = new JButton("🕘 Lịch sử phòng");
        leftActions.add(btnCreate);
        leftActions.add(btnView);
        leftActions.add(btnEnd);
        leftActions.add(btnRenew);
        leftActions.add(btnHistory);

        bottom.add(leftActions, BorderLayout.WEST);

        lblInfo = new JLabel("Thông tin: —");
        bottom.add(lblInfo, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        // Actions
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

        // double-click view
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) showDetailDialog();
            }
        });

        // initial load
        loadData();
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
        if (end == null) return -1;
        return ChronoUnit.DAYS.between(LocalDate.now(), end);
    }

    private String formatMoney(double v) {
        return String.format("%,.0f", v);
    }

    private void updateInfoBar() {
        long total = hopDongService.countAll();
        long active = hopDongService.countByStatus("dangThue");
        long ended = hopDongService.countByStatus("daKetThuc");
        lblInfo.setText(String.format("Tổng hợp đồng: %d | Đang thuê: %d | Đã kết thúc: %d", total, active, ended));
    }

    // ---- Dialogs & actions ----

    private void showCreateDialog() {
        try {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6,6,6,6);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx=0; c.gridy=0; panel.add(new JLabel("ID Phòng:"), c);
            JTextField txtIdPhong = new JTextField(); txtIdPhong.setToolTipText("Nhập ID Phòng"); c.gridx=1; panel.add(txtIdPhong, c);

            c.gridx=0; c.gridy++; panel.add(new JLabel("ID Khách:"), c);
            JTextField txtIdKh = new JTextField(); c.gridx=1; panel.add(txtIdKh, c);

            c.gridx=0; c.gridy++; panel.add(new JLabel("Ngày bắt đầu (YYYY-MM-DD):"), c);
            JTextField txtBD = new JTextField(LocalDate.now().toString()); c.gridx=1; panel.add(txtBD, c);

            c.gridx=0; c.gridy++; panel.add(new JLabel("Ngày kết thúc (opt):"), c);
            JTextField txtKT = new JTextField(); c.gridx=1; panel.add(txtKT, c);

            c.gridx=0; c.gridy++; panel.add(new JLabel("Tiền cọc:"), c);
            JTextField txtCoc = new JTextField("0"); c.gridx=1; panel.add(txtCoc, c);

            int r = JOptionPane.showConfirmDialog(this, panel, "Tạo hợp đồng mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return;

            int idPhong = Integer.parseInt(txtIdPhong.getText().trim());
            int idKh = Integer.parseInt(txtIdKh.getText().trim());
            LocalDate bd = LocalDate.parse(txtBD.getText().trim());
            LocalDate kt = null;
            if (!txtKT.getText().trim().isEmpty()) kt = LocalDate.parse(txtKT.getText().trim());
            double coc = Double.parseDouble(txtCoc.getText().trim());

            // check room status
            Phong p = phongService.findById(idPhong);
            if (p == null) { JOptionPane.showMessageDialog(this, "Phòng không tồn tại."); return; }
            if ("baoTri".equalsIgnoreCase(p.getTrangThai())) {
                JOptionPane.showMessageDialog(this, "Phòng đang bảo trì, không thể tạo hợp đồng."); return;
            }
            if ("daThue".equalsIgnoreCase(p.getTrangThai())) {
                JOptionPane.showMessageDialog(this, "Phòng đang thuê. Kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }

            HopDong hd = new HopDong();
            hd.setIdPhong(idPhong);
            hd.setIdKhachHang(idKh);
            hd.setNgayBatDau(bd);
            hd.setNgayKetThuc(kt);
            hd.setTienCoc(coc);
            hd.setTrangThai("dangThue");

            boolean ok = hopDongService.create(hd);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Tạo hợp đồng thành công.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Tạo hợp đồng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Optional<Integer> getSelectedContractId() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn hợp đồng."); return Optional.empty(); }
        return Optional.of((Integer) tableModel.getValueAt(sel, 0));
    }

    private void showDetailDialog() {
        Optional<Integer> maybe = getSelectedContractId();
        if (maybe.isEmpty()) return;
        int id = maybe.get();
        HopDong h = hopDongService.findById(id);
        if (h == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng."); return; }

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
        if (maybe.isEmpty()) return;
        int id = maybe.get();
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn kết thúc hợp đồng ID=" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

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
        if (maybe.isEmpty()) return;
        int id = maybe.get();
        HopDong h = hopDongService.findById(id);
        if (h == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng."); return; }
        String s = JOptionPane.showInputDialog(this, "Nhập ngày kết thúc mới (YYYY-MM-DD):", LocalDate.now().plusMonths(1).toString());
        if (s == null || s.trim().isEmpty()) return;
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
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn một hợp đồng (hoặc phòng) để xem lịch sử."); return; }
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
}
