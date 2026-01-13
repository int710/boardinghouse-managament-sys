package com.ptpmud.quanlynhatro.service;

import com.ptpmud.quanlynhatro.dao.HoaDonDAO;
import com.ptpmud.quanlynhatro.dao.HoaDonDichVuDAO;
import com.ptpmud.quanlynhatro.dao.DichVuDAO;
import com.ptpmud.quanlynhatro.model.HoaDon;
import com.ptpmud.quanlynhatro.model.HoaDonDichVu;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.dao.PhongDAO;
import com.ptpmud.quanlynhatro.utils.PdfUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HoaDonService {
    private final HoaDonDAO dao = new HoaDonDAO();
    private final HoaDonDichVuDAO hddvDAO = new HoaDonDichVuDAO();
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final EmailService emailService = new EmailService();
    private final PhongDAO phongDAO = new PhongDAO();
    private final ThuChiService thuChiService = new ThuChiService();

    public List<HoaDon> findAll(Integer thang, Integer nam, String trangThai) {
        return dao.findAll(thang, nam, trangThai);
    }

    public List<HoaDon> findByPhong(int idPhong) { return dao.findByPhong(idPhong); }

    public HoaDon findById(int id) { return dao.findById(id); }

    public HoaDon generate(int idPhong, Integer thang, Integer nam) {
        int t = thang != null ? thang : LocalDate.now().getMonthValue();
        int y = nam != null ? nam : LocalDate.now().getYear();
        boolean ok = dao.upsertByProcedure(idPhong, t, y);
        if (!ok) return null;
        return dao.findOne(idPhong, t, y);
    }

    public boolean markPaid(int idHoaDon) {
        boolean ok = dao.updateStatus(idHoaDon, "daThanhToan");
        if (ok) {
            // Automatically add to ThuChi when invoice is marked as paid
            HoaDon hd = dao.findById(idHoaDon);
            if (hd != null) {
                Phong p = phongDAO.findById(hd.getIdPhong());
                String tenPhong = p != null ? p.getTenPhong() : ("#" + hd.getIdPhong());
                String description = "Thu tiền " + tenPhong + " tháng " + hd.getThang();
                thuChiService.addRevenue(hd.getIdPhong(), LocalDate.now(), hd.getTongTien(), description);
            }
        }
        return ok;
    }
    
    public boolean markUnpaid(int idHoaDon) { return dao.updateStatus(idHoaDon, "chuaThanhToan"); }

    public boolean updateTienKhac(int idHoaDon, double tienKhac) {
        return dao.updateTienKhac(idHoaDon, tienKhac);
    }

    public boolean updateManual(HoaDon h) {
        // tự tính lại tổng nếu cần
        h.setTongTien(h.getTienPhong() + h.getTienDien() + h.getTienNuoc() + h.getTienDichVu() + h.getTienKhac());
        return dao.updateManual(h);
    }

    public boolean delete(int id) { return dao.delete(id); }

    public String getCustomerEmail(HoaDon hd) {
        if (hd == null) return null;
        // Lấy thông tin khách hàng từ hợp đồng đang thuê
        com.ptpmud.quanlynhatro.dao.HopDongDAO hdDAO = new com.ptpmud.quanlynhatro.dao.HopDongDAO();
        com.ptpmud.quanlynhatro.model.HopDong hopDong = hdDAO.findActiveByPhong(hd.getIdPhong());
        if (hopDong != null) {
            com.ptpmud.quanlynhatro.dao.KhachHangDAO khDAO = new com.ptpmud.quanlynhatro.dao.KhachHangDAO();
            com.ptpmud.quanlynhatro.model.KhachHang kh = khDAO.findById(hopDong.getIdKhachHang());
            if (kh != null && kh.getEmail() != null && !kh.getEmail().trim().isEmpty()) {
                return kh.getEmail();
            }
        }
        return null;
    }

    public boolean sendReminder(String email, HoaDon hd) {
        if (email == null || email.isEmpty() || hd == null) return false;
        Phong p = phongDAO.findById(hd.getIdPhong());
        // Lấy thông tin khách hàng từ hợp đồng đang thuê
        com.ptpmud.quanlynhatro.dao.HopDongDAO hdDAO = new com.ptpmud.quanlynhatro.dao.HopDongDAO();
        com.ptpmud.quanlynhatro.model.HopDong hopDong = hdDAO.findActiveByPhong(hd.getIdPhong());
        com.ptpmud.quanlynhatro.model.KhachHang kh = null;
        if (hopDong != null) {
            com.ptpmud.quanlynhatro.dao.KhachHangDAO khDAO = new com.ptpmud.quanlynhatro.dao.KhachHangDAO();
            kh = khDAO.findById(hopDong.getIdKhachHang());
        }
        String subject = "[Nhắc thanh toán] Hóa đơn phòng " + (p != null ? p.getTenPhong() : ("#" + hd.getIdPhong())) + " tháng " + hd.getThang() + "/" + hd.getNam();
        String htmlBody = buildHtmlInvoice(hd, p, kh);
        return emailService.sendHtml(email, subject, htmlBody);
    }

    public boolean sendReminderWithPdf(String email, HoaDon hd) {
        if (email == null || email.isEmpty() || hd == null) return false;
        Phong p = phongDAO.findById(hd.getIdPhong());
        // Lấy thông tin khách hàng từ hợp đồng đang thuê
        com.ptpmud.quanlynhatro.dao.HopDongDAO hdDAO = new com.ptpmud.quanlynhatro.dao.HopDongDAO();
        com.ptpmud.quanlynhatro.model.HopDong hopDong = hdDAO.findActiveByPhong(hd.getIdPhong());
        com.ptpmud.quanlynhatro.model.KhachHang kh = null;
        if (hopDong != null) {
            com.ptpmud.quanlynhatro.dao.KhachHangDAO khDAO = new com.ptpmud.quanlynhatro.dao.KhachHangDAO();
            kh = khDAO.findById(hopDong.getIdKhachHang());
        }
        byte[] pdf = PdfUtil.renderHoaDon(hd, p, kh);
        if (pdf == null) return false;
        String subject = "[Hóa đơn] Phòng " + (p != null ? p.getTenPhong() : ("#" + hd.getIdPhong())) + " tháng " + hd.getThang() + "/" + hd.getNam();
        String htmlBody = buildHtmlInvoice(hd, p, kh);
        return emailService.sendHtmlWithAttachment(email, subject, htmlBody, pdf, "HoaDon-" + hd.getIdPhong() + "-" + hd.getThang() + "-" + hd.getNam() + ".pdf");
    }

    private String buildHtmlInvoice(HoaDon hd, Phong p, com.ptpmud.quanlynhatro.model.KhachHang kh) {
        String tenPhong = p != null ? p.getTenPhong() : ("#" + hd.getIdPhong());
        String tenKhach = kh != null ? kh.getTenKhachHang() : "Khách hàng";
        String format = "%,.0f";
        
        // Lấy chi tiết dịch vụ
        List<HoaDonDichVu> chiTietDichVu = getChiTietDichVu(hd.getIdHoaDon());
        StringBuilder dvRows = new StringBuilder();
        if (chiTietDichVu != null && !chiTietDichVu.isEmpty()) {
            for (HoaDonDichVu hddv : chiTietDichVu) {
                String tenDv = hddv.getDichVu() != null ? hddv.getDichVu().getTenDichVu() : "Dịch vụ #" + hddv.getIdDichVu();
                String donVi = hddv.getSoLuong() + " x " + String.format(format, hddv.getDonGia());
                dvRows.append(String.format(
                    "<tr><td>%s (%s)</td><td style=\"text-align: right;\">%s</td></tr>",
                    tenDv, donVi, String.format(format, hddv.getThanhTien())
                ));
            }
        } else if (hd.getTienDichVu() > 0) {
            dvRows.append(String.format(
                "<tr><td>Dịch vụ</td><td style=\"text-align: right;\">%s</td></tr>",
                String.format(format, hd.getTienDichVu())
            ));
        }
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 20px; border-radius: 8px 8px 0 0; text-align: center; }
                    .content { background: #f9f9f9; padding: 20px; border: 1px solid #ddd; }
                    .invoice-table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                    .invoice-table th { background: #667eea; color: white; padding: 12px; text-align: left; }
                    .invoice-table td { padding: 10px; border-bottom: 1px solid #ddd; }
                    .invoice-table tr:nth-child(even) { background: #f5f5f5; }
                    .total { font-size: 18px; font-weight: bold; color: #667eea; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>HÓA ĐƠN PHÒNG TRỌ</h1>
                        <p>Tháng %d/%d</p>
                    </div>
                    <div class="content">
                        <p><strong>Kính gửi:</strong> %s</p>
                        <p><strong>Phòng:</strong> %s</p>
                        <table class="invoice-table">
                            <tr><th>Hạng mục</th><th style="text-align: right;">Thành tiền (VNĐ)</th></tr>
                            <tr><td>Tiền phòng</td><td style="text-align: right;">%s</td></tr>
                            <tr><td>Điện</td><td style="text-align: right;">%s</td></tr>
                            <tr><td>Nước</td><td style="text-align: right;">%s</td></tr>
                            %s
                            <tr><td>Khác</td><td style="text-align: right;">%s</td></tr>
                            <tr class="total"><td>TỔNG CỘNG</td><td style="text-align: right;">%s</td></tr>
                        </table>
                        <p><strong>Trạng thái:</strong> %s</p>
                        <p style="color: #d32f2f;"><strong>Vui lòng thanh toán sớm. Cảm ơn!</strong></p>
                    </div>
                    <div class="footer">
                        <p>Hệ thống Quản lý Nhà trọ - © 2025</p>
                    </div>
                </div>
            </body>
            </html>
            """, hd.getThang(), hd.getNam(), tenKhach, tenPhong,
            String.format(format, hd.getTienPhong()),
            String.format(format, hd.getTienDien()),
            String.format(format, hd.getTienNuoc()),
            dvRows.toString(),
            String.format(format, hd.getTienKhac()),
            String.format(format, hd.getTongTien()),
            hd.getTrangThai());
    }

    public byte[] exportPdf(HoaDon hd) {
        if (hd == null) return null;
        Phong p = phongDAO.findById(hd.getIdPhong());
        // Lấy thông tin khách hàng từ hợp đồng đang thuê
        com.ptpmud.quanlynhatro.dao.HopDongDAO hdDAO = new com.ptpmud.quanlynhatro.dao.HopDongDAO();
        com.ptpmud.quanlynhatro.model.HopDong hopDong = hdDAO.findActiveByPhong(hd.getIdPhong());
        com.ptpmud.quanlynhatro.model.KhachHang kh = null;
        if (hopDong != null) {
            com.ptpmud.quanlynhatro.dao.KhachHangDAO khDAO = new com.ptpmud.quanlynhatro.dao.KhachHangDAO();
            kh = khDAO.findById(hopDong.getIdKhachHang());
        }
        return PdfUtil.renderHoaDon(hd, p, kh);
    }

    public double getLastDienCu(int idPhong, int thang, int nam) { 
        return dao.getLastDienChiSoMoi(idPhong, thang, nam); 
    }
    public double getLastNuocCu(int idPhong, int thang, int nam) { 
        return dao.getLastNuocSoKhoi(idPhong, thang, nam); 
    }

    /**
     * Tạo hoặc cập nhật hóa đơn thủ công, đồng thời ghi chỉ số điện/nước (upsert) và lưu chi tiết dịch vụ.
     * @param dichVuMap Map<idDichVu, soLuong> - các dịch vụ đã chọn
     */
    public HoaDon createOrUpdateManual(int idPhong, int thang, int nam,
                                       double dienCu, double dienMoi, double donGiaDien,
                                       double nuocCu, double nuocMoi, double donGiaNuoc,
                                       Map<Integer, Integer> dichVuMap, double tienKhac) {
        Phong p = phongDAO.findById(idPhong);
        if (p == null) return null;
        double tienPhong = p.getGiaThue();
        double tienDien = Math.max(dienMoi - dienCu, 0) * donGiaDien;
        double tienNuoc = Math.max(nuocMoi - nuocCu, 0) * donGiaNuoc;
        
        // Tính tiền dịch vụ từ Map
        double tienDichVu = 0;
        if (dichVuMap != null) {
            for (Map.Entry<Integer, Integer> entry : dichVuMap.entrySet()) {
                int idDichVu = entry.getKey();
                int soLuong = entry.getValue();
                if (soLuong > 0) {
                    var dv = dichVuDAO.findById(idDichVu);
                    if (dv != null) {
                        tienDichVu += dv.getDonGia() * soLuong;
                    }
                }
            }
        }
        
        double tong = tienPhong + tienDien + tienNuoc + tienDichVu + tienKhac;

        boolean existed = dao.exists(idPhong, thang, nam);
        HoaDon hd;
        
        if (existed) {
            hd = dao.findOne(idPhong, thang, nam);
            if (hd == null) return null;
            // Xóa chi tiết dịch vụ cũ
            hddvDAO.deleteByHoaDon(hd.getIdHoaDon());
        } else {
            // Tạo hóa đơn mới
            String sql = """
                INSERT INTO HoaDon (idPhong, thang, nam, tienPhong, tienDien, tienNuoc, tienDichVu, tienKhac, tongTien, trangThai)
                VALUES (?,?,?,?,?,?,?,?,?,?)
            """;
            try (var c = com.ptpmud.quanlynhatro.utils.DBConnection.getConnection();
                 var ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idPhong);
                ps.setInt(2, thang);
                ps.setInt(3, nam);
                ps.setDouble(4, tienPhong);
                ps.setDouble(5, tienDien);
                ps.setDouble(6, tienNuoc);
                ps.setDouble(7, tienDichVu);
                ps.setDouble(8, tienKhac);
                ps.setDouble(9, tong);
                ps.setString(10, "chuaThanhToan");
                int r = ps.executeUpdate();
                if (r > 0) {
                    try (var gk = ps.getGeneratedKeys()) {
                        if (gk.next()) {
                            hd = new HoaDon();
                            hd.setIdHoaDon(gk.getInt(1));
                            hd.setIdPhong(idPhong);
                            hd.setThang(thang);
                            hd.setNam(nam);
                            hd.setTienPhong(tienPhong);
                            hd.setTienDien(tienDien);
                            hd.setTienNuoc(tienNuoc);
                            hd.setTienDichVu(tienDichVu);
                            hd.setTienKhac(tienKhac);
                            hd.setTongTien(tong);
                            hd.setTrangThai("chuaThanhToan");
                        } else {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        // Cập nhật hóa đơn
        hd.setTienPhong(tienPhong);
        hd.setTienDien(tienDien);
        hd.setTienNuoc(tienNuoc);
        hd.setTienDichVu(tienDichVu);
        hd.setTienKhac(tienKhac);
        hd.setTongTien(tong);
        if (!existed) {
            hd.setTrangThai("chuaThanhToan");
        }
        dao.updateManual(hd);
        
        // Lưu chi tiết dịch vụ
        if (dichVuMap != null) {
            for (Map.Entry<Integer, Integer> entry : dichVuMap.entrySet()) {
                int idDichVu = entry.getKey();
                int soLuong = entry.getValue();
                if (soLuong > 0) {
                    var dv = dichVuDAO.findById(idDichVu);
                    if (dv != null) {
                        HoaDonDichVu hddv = new HoaDonDichVu();
                        hddv.setIdHoaDon(hd.getIdHoaDon());
                        hddv.setIdDichVu(idDichVu);
                        hddv.setSoLuong(soLuong);
                        hddv.setDonGia(dv.getDonGia());
                        hddvDAO.insert(hddv);
                    }
                }
            }
        }
        
        // Upsert điện/nước
        dao.upsertDien(idPhong, thang, nam, dienCu, dienMoi, donGiaDien);
        dao.upsertNuoc(idPhong, thang, nam, nuocCu, nuocMoi, donGiaNuoc);
        
        return hd;
    }

    public double sumTongTienByMonth(int thang, int nam) {
        return dao.sumTongTienByMonth(thang, nam);
    }

    public java.util.Map<String, Double> getRevenueByMonth(int months) {
        return dao.getRevenueByMonth(months);
    }

    public java.util.Map<String, Long> getInvoiceStatusCount() {
        return dao.getInvoiceStatusCount();
    }

    public List<HoaDonDichVu> getChiTietDichVu(int idHoaDon) {
        return hddvDAO.findByHoaDon(idHoaDon);
    }
}

