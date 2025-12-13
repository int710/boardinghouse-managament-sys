package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.DichVu;
import com.ptpmud.quanlynhatro.service.DichVuService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DichVuFrame extends JPanel {

    private final DichVuService service = new DichVuService();
    private DefaultTableModel dvModel;
    private JTable dvTable;

    public DichVuFrame() {
        setLayout(new BorderLayout(8,8));
        setBackground(new Color(244,246,249));
        initUI();
        loadData();
    }

    private void initUI() {

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.setOpaque(false);

        JButton btnAdd = new JButton("➕ Thêm");
        JButton btnEdit = new JButton("✏ Sửa");
        JButton btnDel = new JButton("🗑 Xóa");

        top.add(btnAdd); 
        top.add(btnEdit); 
        top.add(btnDel);
        add(top, BorderLayout.NORTH);

        dvModel = new DefaultTableModel(
                new String[]{"ID","Tên dịch vụ","Đơn giá","Mô tả"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        dvTable = new JTable(dvModel);
        dvTable.setRowHeight(30);

        add(new JScrollPane(dvTable), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDel.addActionListener(e -> deleteSelected());
    }

    private void loadData() {
        List<DichVu> list = service.getAll();
        dvModel.setRowCount(0);

        for (DichVu d : list) {
            dvModel.addRow(new Object[]{
                d.getIdDichVu(),
                d.getTenDichVu(),
                d.getDonGia(),
                d.getMoTa()
            });
        }
    }

    private void showAddDialog() {
        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        JTextField tName = new JTextField();
        JTextField tPrice = new JTextField();
        JTextArea tMoTa = new JTextArea(4,20);

        p.add(new JLabel("Tên dịch vụ:")); p.add(tName);
        p.add(new JLabel("Đơn giá:")); p.add(tPrice);
        p.add(new JLabel("Mô tả:")); p.add(new JScrollPane(tMoTa));

        int r = JOptionPane.showConfirmDialog(
                this, p, "Thêm dịch vụ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (r != JOptionPane.OK_OPTION) return;

        try {
            String name = tName.getText().trim();
            double price = Double.parseDouble(tPrice.getText().trim());

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên dịch vụ trống.");
                return;
            }

            DichVu dv = new DichVu();
            dv.setTenDichVu(name);
            dv.setDonGia(price);
            dv.setMoTa(tMoTa.getText().trim());

            if (service.create(dv)) {
                JOptionPane.showMessageDialog(this, "Đã thêm.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDialog() {
        int sel = dvTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ để sửa.");
            return;
        }

        int id = (int) dvModel.getValueAt(sel, 0);
        DichVu dv = service.getById(id);

        if (dv == null) {
            JOptionPane.showMessageDialog(this, "Dịch vụ không tồn tại.");
            loadData();
            return;
        }

        JPanel p = new JPanel(new GridLayout(0,1,6,6));

        JTextField tName = new JTextField(dv.getTenDichVu());
        JTextField tPrice = new JTextField(String.valueOf((long) dv.getDonGia()));
        JTextArea tMoTa = new JTextArea(dv.getMoTa(),4,20);

        p.add(new JLabel("Tên dịch vụ:")); p.add(tName);
        p.add(new JLabel("Đơn giá:")); p.add(tPrice);
        p.add(new JLabel("Mô tả:")); p.add(new JScrollPane(tMoTa));

        int r = JOptionPane.showConfirmDialog(
                this, p, "Sửa dịch vụ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (r != JOptionPane.OK_OPTION) return;

        try {
            dv.setTenDichVu(tName.getText().trim());
            dv.setDonGia(Double.parseDouble(tPrice.getText().trim()));
            dv.setMoTa(tMoTa.getText().trim());

            if (service.update(dv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int sel = dvTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ để xóa.");
            return;
        }

        int id = (int) dvModel.getValueAt(sel, 0);

        if (JOptionPane.showConfirmDialog(
                this, "Xóa dịch vụ ID=" + id + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) return;

        if (service.delete(id)) {
            JOptionPane.showMessageDialog(this, "Đã xóa.");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại (kiểm tra ràng buộc).",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
