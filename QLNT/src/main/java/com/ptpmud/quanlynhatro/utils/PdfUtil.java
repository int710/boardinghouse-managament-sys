package com.ptpmud.quanlynhatro.utils;

import com.ptpmud.quanlynhatro.model.HoaDon;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.model.HoaDonDichVu;
import com.ptpmud.quanlynhatro.dao.HoaDonDichVuDAO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfUtil {

    public static byte[] renderHoaDon(HoaDon hd, Phong phong, KhachHang khachHang) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.BOLD);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            // Header
            Paragraph pTitle = new Paragraph("HÓA ĐƠN PHÒNG TRỌ", titleFont);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            pTitle.setSpacingAfter(10);
            doc.add(pTitle);

            // Thông tin hóa đơn
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{30, 70});
            infoTable.setSpacingAfter(15);

            addInfoCell(infoTable, "Mã hóa đơn:", "#" + hd.getIdHoaDon(), normalFont);
            addInfoCell(infoTable, "Phòng:", phong != null ? phong.getTenPhong() : ("#" + hd.getIdPhong()), normalFont);
            addInfoCell(infoTable, "Tháng/Năm:", hd.getThang() + "/" + hd.getNam(), normalFont);
            if (khachHang != null) {
                addInfoCell(infoTable, "Khách hàng:", khachHang.getTenKhachHang(), normalFont);
                if (khachHang.getSoDienThoai() != null && !khachHang.getSoDienThoai().isEmpty()) {
                    addInfoCell(infoTable, "SĐT:", khachHang.getSoDienThoai(), normalFont);
                }
            }
            addInfoCell(infoTable, "Ngày tạo:", hd.getNgayTao() != null ? 
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayTao()) : "N/A", normalFont);
            addInfoCell(infoTable, "Trạng thái:", hd.getTrangThai(), normalFont);

            doc.add(infoTable);

            // Bảng chi tiết
            PdfPTable detailTable = new PdfPTable(3);
            detailTable.setWidthPercentage(100);
            detailTable.setWidths(new float[]{50, 25, 25});
            detailTable.setSpacingAfter(15);

            // Header
            PdfPCell headerCell = new PdfPCell(new Phrase("Hạng mục", headerFont));
            headerCell.setPadding(10);
            headerCell.setBackgroundColor(new Color(102, 126, 234));
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailTable.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Đơn vị", headerFont));
            headerCell.setPadding(10);
            headerCell.setBackgroundColor(new Color(102, 126, 234));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailTable.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Thành tiền (VNĐ)", headerFont));
            headerCell.setPadding(10);
            headerCell.setBackgroundColor(new Color(102, 126, 234));
            headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            detailTable.addCell(headerCell);

            // Chi tiết
            addDetailRow(detailTable, "Tiền phòng", "1 tháng", hd.getTienPhong(), normalFont);
            addDetailRow(detailTable, "Điện", "kWh", hd.getTienDien(), normalFont);
            addDetailRow(detailTable, "Nước", "khối", hd.getTienNuoc(), normalFont);
            
            // Chi tiết dịch vụ
            List<HoaDonDichVu> chiTietDichVu = new HoaDonDichVuDAO().findByHoaDon(hd.getIdHoaDon());
            if (chiTietDichVu != null && !chiTietDichVu.isEmpty()) {
                for (HoaDonDichVu hddv : chiTietDichVu) {
                    String tenDv = hddv.getDichVu() != null ? hddv.getDichVu().getTenDichVu() : "Dịch vụ #" + hddv.getIdDichVu();
                    String donVi = hddv.getSoLuong() + " x " + format(hddv.getDonGia());
                    addDetailRow(detailTable, tenDv, donVi, hddv.getThanhTien(), normalFont);
                }
            } else if (hd.getTienDichVu() > 0) {
                addDetailRow(detailTable, "Dịch vụ", "tháng", hd.getTienDichVu(), normalFont);
            }
            
            if (hd.getTienKhac() > 0) {
                addDetailRow(detailTable, "Phí khác", "", hd.getTienKhac(), normalFont);
            }

            // Tổng cộng
            PdfPCell totalLabel = new PdfPCell(new Phrase("TỔNG CỘNG", headerFont));
            totalLabel.setPadding(10);
            totalLabel.setColspan(2);
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            detailTable.addCell(totalLabel);

            PdfPCell totalValue = new PdfPCell(new Phrase(format(hd.getTongTien()), headerFont));
            totalValue.setPadding(10);
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setBackgroundColor(new Color(255, 235, 59));
            detailTable.addCell(totalValue);

            doc.add(detailTable);

            // Footer
            Paragraph footer = new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ!", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            doc.add(footer);

            Paragraph note = new Paragraph("Vui lòng thanh toán đúng hạn. Trân trọng!", smallFont);
            note.setAlignment(Element.ALIGN_CENTER);
            note.setSpacingBefore(5);
            doc.add(note);

            doc.close();
            return out.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void addInfoCell(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(new Color(245, 245, 245));
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }

    private static void addDetailRow(PdfPTable table, String item, String unit, double amount, Font font) {
        PdfPCell itemCell = new PdfPCell(new Phrase(item, font));
        itemCell.setPadding(8);
        table.addCell(itemCell);

        PdfPCell unitCell = new PdfPCell(new Phrase(unit, font));
        unitCell.setPadding(8);
        unitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(unitCell);

        PdfPCell amountCell = new PdfPCell(new Phrase(format(amount), font));
        amountCell.setPadding(8);
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(amountCell);
    }

    private static PdfPCell cell(String text, boolean header) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 12, header ? Font.BOLD : Font.NORMAL);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setPadding(8);
        return c;
    }

    private static String format(double v) {
        return String.format("%,.0f", v);
    }
}