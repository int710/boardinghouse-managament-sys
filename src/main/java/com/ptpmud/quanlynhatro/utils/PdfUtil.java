package com.ptpmud.quanlynhatro.utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.ptpmud.quanlynhatro.dao.HoaDonDichVuDAO;
import com.ptpmud.quanlynhatro.model.*;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class PdfUtil {

    // ================= MÀU SẮC =================
    private static final Color PRIMARY = new Color(41, 128, 185);     // Xanh dương đậm chuyên nghiệp
    private static final Color PRIMARY_LIGHT = new Color(52, 152, 219);
    private static final Color HEADER_BG = new Color(236, 240, 241);
    private static final Color ROW_ALT = new Color(248, 250, 251);
    private static final Color TOTAL_BG = PRIMARY;
    private static final Color TOTAL_TEXT = Color.WHITE;

    // ================= FONT & IMAGE =================
    private static Font titleFont, bigBoldFont, headerFont, normalFont, smallFont;
    private static Image logo, signature, qr;

    static {
        try {
            BaseFont bf = BaseFont.createFont("fonts/Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            titleFont = new Font(bf, 22, Font.BOLD, PRIMARY);
            bigBoldFont = new Font(bf, 18, Font.BOLD, TOTAL_TEXT);
            headerFont = new Font(bf, 13, Font.BOLD);
            normalFont = new Font(bf, 11);
            smallFont = new Font(bf, 9);

            logo = Image.getInstance(PdfUtil.class.getResource("/images/logo.png"));
            logo.scaleToFit(85, 85);

            signature = Image.getInstance(PdfUtil.class.getResource("/images/signature.png"));
            signature.scaleToFit(130, 65);

            qr = Image.getInstance(PdfUtil.class.getResource("/images/qr.png"));
            qr.scaleToFit(130, 130);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi load font hoặc hình ảnh", e);
        }
    }

    public static byte[] renderHoaDon(HoaDon hd, Phong phong, KhachHang khachHang) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // ================= HEADER =================
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{22, 78});

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(logoCell);

            Paragraph company = new Paragraph();
            company.add(new Phrase("NHÀ TRỌ KMA\n", headerFont));
            company.add(new Phrase("Địa chỉ: 141 Chiến Thắng, Phường Phương Liệt, TP Hà Nội\n", normalFont));
            company.add(new Phrase("SĐT: 0399 888 666", normalFont));

            PdfPCell companyCell = new PdfPCell(company);
            companyCell.setBorder(Rectangle.NO_BORDER);
            companyCell.setVerticalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(companyCell);

            doc.add(headerTable);

            // ================= TIÊU ĐỀ =================
            Paragraph title = new Paragraph("HÓA ĐƠN PHÒNG TRỌ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(20);
            title.setSpacingAfter(25);
            doc.add(title);

            // ================= THÔNG TIN CHUNG =================
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{30, 70});
            infoTable.setSpacingAfter(20);

            addInfoRow(infoTable, "Mã hóa đơn:", "#" + hd.getIdHoaDon(), HEADER_BG);
            addInfoRow(infoTable, "Phòng:", phong != null ? phong.getTenPhong() : "#" + hd.getIdPhong(), Color.WHITE);
            addInfoRow(infoTable, "Tháng / Năm:", hd.getThang() + "/" + hd.getNam(), HEADER_BG);
            
            if (khachHang != null) {
                addInfoRow(infoTable, "Khách hàng:", khachHang.getTenKhachHang(), Color.WHITE);
                if (khachHang.getSoDienThoai() != null && !khachHang.getSoDienThoai().trim().isEmpty()) {
                    addInfoRow(infoTable, "Số điện thoại:", khachHang.getSoDienThoai(), HEADER_BG);
                }
                if (khachHang.getEmail() != null && !khachHang.getEmail().trim().isEmpty()) {
                    addInfoRow(infoTable, "Email:", khachHang.getEmail(), Color.WHITE);
                }
            }

            addInfoRow(infoTable, "Ngày tạo:", 
                hd.getNgayTao() != null 
                    ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayTao()) 
                    : "N/A", Color.WHITE);
            addInfoRow(infoTable, "Trạng thái:", hd.getTrangThai(), HEADER_BG);

            doc.add(infoTable);

            // ================= BẢNG CHI TIẾT =================
            PdfPTable detailTable = new PdfPTable(3);
            detailTable.setWidthPercentage(100);
            detailTable.setWidths(new float[]{50, 25, 25});

            // Header bảng
            addTableHeader(detailTable, "Hạng mục", Element.ALIGN_LEFT);
            addTableHeader(detailTable, "Đơn vị", Element.ALIGN_CENTER);
            addTableHeader(detailTable, "Thành tiền (VNĐ)", Element.ALIGN_RIGHT);

            boolean alternate = false;

            addDetailRow(detailTable, "Tiền phòng", "1 tháng", hd.getTienPhong(), alternate = !alternate);
            addDetailRow(detailTable, "Tiền điện", "kWh", hd.getTienDien(), alternate = !alternate);
            addDetailRow(detailTable, "Tiền nước", "Khối", hd.getTienNuoc(), alternate = !alternate);

            List<HoaDonDichVu> dvList = new HoaDonDichVuDAO().findByHoaDon(hd.getIdHoaDon());
            if (dvList != null && !dvList.isEmpty()) {
                for (HoaDonDichVu dv : dvList) {
                    String ten = dv.getDichVu() != null ? dv.getDichVu().getTenDichVu() : "Dịch vụ #" + dv.getIdDichVu();
                    String donvi = dv.getSoLuong() + " x " + format(dv.getDonGia());
                    addDetailRow(detailTable, ten, donvi, dv.getThanhTien(), alternate = !alternate);
                }
            }

            if (hd.getTienKhac() > 0) {
                addDetailRow(detailTable, "Chi phí khác", "", hd.getTienKhac(), alternate = !alternate);
            }

            // Dòng tổng cộng
            PdfPCell totalLabel = new PdfPCell(new Phrase("TỔNG CỘNG", bigBoldFont));
            totalLabel.setColspan(2);
            totalLabel.setPadding(12);
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabel.setBackgroundColor(TOTAL_BG);
            totalLabel.setBorder(Rectangle.NO_BORDER);
            detailTable.addCell(totalLabel);

            PdfPCell totalValue = new PdfPCell(new Phrase(format(hd.getTongTien()) + " VNĐ", bigBoldFont));
            totalValue.setPadding(12);
            totalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalValue.setBackgroundColor(TOTAL_BG);
            totalValue.setBorder(Rectangle.NO_BORDER);
            detailTable.addCell(totalValue);

            doc.add(detailTable);

            // ================= THANH TOÁN & CHỮ KÝ =================
            PdfPTable bottomTable = new PdfPTable(2);
            bottomTable.setWidthPercentage(100);
            bottomTable.setWidths(new float[]{50, 50});
            bottomTable.setSpacingBefore(30);

            // QR Code
            PdfPCell qrCell = new PdfPCell();
            qrCell.setBorder(Rectangle.NO_BORDER);
            Paragraph qrText = new Paragraph("Quét mã để thanh toán\n\n", normalFont);
            qrText.setAlignment(Element.ALIGN_CENTER);
            qrCell.addElement(qrText);
            qrCell.addElement(qr);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bottomTable.addCell(qrCell);

            // Tổng tiền + chữ ký
            Paragraph totalPay = new Paragraph();
            totalPay.add(new Phrase("TỔNG TIỀN THANH TOÁN\n\n", headerFont));
            totalPay.add(new Phrase(format(hd.getTongTien()) + " VNĐ", titleFont));
            totalPay.setAlignment(Element.ALIGN_CENTER);
            totalPay.setSpacingAfter(20);

            PdfPCell signCell = new PdfPCell();
            signCell.setBorder(Rectangle.NO_BORDER);
            signCell.addElement(totalPay);
            signCell.addElement(new Phrase("Người lập hóa đơn\n", normalFont));
            signCell.addElement(signature);
            signCell.addElement(new Phrase("(Ký, ghi rõ họ tên)", smallFont));
            signCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bottomTable.addCell(signCell);

            doc.add(bottomTable);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= HELPER METHODS =================
    private static void addInfoRow(PdfPTable table, String label, String value, Color bg) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, normalFont));
        labelCell.setPadding(9);
        labelCell.setBackgroundColor(bg);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, normalFont));
        valueCell.setPadding(9);
        valueCell.setBackgroundColor(bg);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    private static void addTableHeader(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setPadding(12);
        cell.setHorizontalAlignment(alignment);
        cell.setBackgroundColor(PRIMARY_LIGHT);
        cell.setBorderColor(Color.WHITE);
        table.addCell(cell);
    }

    private static void addDetailRow(PdfPTable table, String item, String unit, double amount, boolean alternate) {
        Color bg = alternate ? ROW_ALT : Color.WHITE;

        PdfPCell itemCell = createDetailCell(item, normalFont, Element.ALIGN_LEFT, bg);
        PdfPCell unitCell = createDetailCell(unit, normalFont, Element.ALIGN_CENTER, bg);
        PdfPCell amountCell = createDetailCell(format(amount), normalFont, Element.ALIGN_RIGHT, bg);

        table.addCell(itemCell);
        table.addCell(unitCell);
        table.addCell(amountCell);
    }

    private static PdfPCell createDetailCell(String text, Font font, int alignment, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(10);
        cell.setHorizontalAlignment(alignment);
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(new Color(220, 220, 220));
        cell.setBorderWidth(0.5f);
        return cell;
    }

    private static String format(double v) {
        return String.format("%,.0f", v);
    }
}