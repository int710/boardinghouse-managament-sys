package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.TaiKhoan;
import com.ptpmud.quanlynhatro.service.TaiKhoanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TaiKhoanFrame extends JPanel {
    private final TaiKhoanService service = new TaiKhoanService();
    private final DefaultTableModel model;
    private final JTable table;

    public TaiKhoanFrame() {
        setLayout(new BorderLayout(8,8));
        setBackground(new Color(244,246,249));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.setOpaque(false);
        JButton btnAdd = new JButton("➕ Tạo tài khoản");
        JButton btnDelete = new JButton("🗑 Xóa");
        JButton btnReload = new JButton("⟳");
        top.add(btnAdd); top.add(btnDelete); top.add(btnReload);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID","Tên đăng nhập","Họ tên","Vai trò"},0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> showCreateDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnReload.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        List<TaiKhoan> list = service.findAll();
        model.setRowCount(0);
        for (TaiKhoan tk : list) {
            model.addRow(new Object[]{tk.getIdTaiKhoan(), tk.getTenDangNhap(), tk.getHoTen(), tk.getVaiTro()});
        }
    }

    private void showCreateDialog() {
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JTextField name = new JTextField();
        JComboBox<String> role = new JComboBox<>(new String[]{"admin", "nhanVien", "user"});
        Object[] fields = {
                "Tên đăng nhập:", user,
                "Mật khẩu:", pass,
                "Họ tên:", name,
                "Vai trò:", role
        };
        int r = JOptionPane.showConfirmDialog(this, fields, "Tạo tài khoản", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        boolean ok = service.register(user.getText().trim(), new String(pass.getPassword()), name.getText().trim(), (String) role.getSelectedItem());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã tạo tài khoản.");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản đã tồn tại hoặc dữ liệu không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn tài khoản để xóa."); return; }
        int id = (int) model.getValueAt(sel, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa tài khoản ID=" + id + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        boolean ok = service.delete(id);
        if (ok) {
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản (có thể là admin duy nhất?).", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

