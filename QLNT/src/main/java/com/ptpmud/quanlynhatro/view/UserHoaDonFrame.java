package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.HoaDon;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.model.TaiKhoan;
import com.ptpmud.quanlynhatro.service.HoaDonService;
import com.ptpmud.quanlynhatro.service.HopDongService;
import com.ptpmud.quanlynhatro.service.PhongService;
import com.ptpmud.quanlynhatro.service.TaiKhoanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện đơn giản cho người thuê xem lịch sử hóa đơn của phòng mình.
 * Mapping: username dạng kh{idKhachHang} -> lấy hợp đồng đang thuê -> phòng.
 */
public class UserHoaDonFrame extends JFrame {
    private final TaiKhoan tk;
    private final HoaDonService hoaDonService = new HoaDonService();
    private final HopDongService hopDongService = new HopDongService();
    private final PhongService phongService = new PhongService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel lblInfo;
    private Integer idPhongBound = null;

    public UserHoaDonFrame(TaiKhoan tk) {
        this.tk = tk;
        setTitle("Lịch sử hóa đơn - " + tk.getTenDangNhap());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        lblInfo = new JLabel("Phòng: (đang tìm...)");
        add(lblInfo, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Tháng","Năm","Tiền phòng","Điện","Nước","Dịch vụ","Khác","Tổng","Trạng thái","Ngày tạo"},0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Tải lại");
        btnRefresh.addActionListener(e -> loadData());
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        resolvePhong();
        loadData();
    }

    private void resolvePhong() {
        int khId = taiKhoanService.parseKhachHangIdFromUsername(tk.getTenDangNhap());
        if (khId <= 0) {
            lblInfo.setText("Không xác định phòng (username không theo mẫu kh{id}).");
            return;
        }
        var hd = hopDongService.findActiveByKhachHang(khId);
        if (hd != null) {
            idPhongBound = hd.getIdPhong();
            Phong p = phongService.findById(idPhongBound);
            lblInfo.setText("Phòng: " + (p != null ? p.getTenPhong() : ("#" + idPhongBound)) + " | Khách: " + tk.getHoTen());
        } else {
            lblInfo.setText("Chưa có hợp đồng đang thuê. Không thể lấy hóa đơn.");
        }
    }

    private void loadData() {
        if (idPhongBound == null) {
            return;
        }
        List<HoaDon> list = hoaDonService.findByPhong(idPhongBound);
        model.setRowCount(0);
        for (HoaDon h : list) {
            model.addRow(new Object[]{
                    h.getThang(), h.getNam(),
                    format(h.getTienPhong()), format(h.getTienDien()), format(h.getTienNuoc()), format(h.getTienDichVu()),
                    format(h.getTienKhac()), format(h.getTongTien()), h.getTrangThai(), h.getNgayTao()
            });
        }
    }

    private String format(double v) {
        return String.format("%,.0f", v);
    }
}

