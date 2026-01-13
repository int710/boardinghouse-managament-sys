-- ============================================
-- FILE: sample_data.sql
-- MÔ TẢ: Dữ liệu mẫu phong phú cho hệ thống Quản Lý Nhà Trọ
-- SỬ DỤNG: Chạy file này SAU KHI đã chạy database.sql
-- ============================================

USE QuanLyNhaTro;

-- ============================================
-- 1. DỮ LIỆU PHÒNG (20 phòng)
-- ============================================
INSERT INTO Phong (tenPhong, loaiPhong, dienTich, giaThue, trangThai, moTa) VALUES
-- Tầng 1
('P101', 'Phòng đơn', 20, 2000000, 'daThue', 'Phòng đơn tầng 1, có cửa sổ, gần cổng'),
('P102', 'Phòng đơn', 18, 1800000, 'daThue', 'Phòng đơn tầng 1, yên tĩnh'),
('P103', 'Phòng đôi', 25, 2500000, 'daThue', 'Phòng đôi tầng 1, rộng rãi'),
('P104', 'Phòng đơn', 20, 2000000, 'trong', 'Phòng đơn tầng 1, mới sửa chữa'),
('P105', 'Phòng đôi', 28, 2800000, 'daThue', 'Phòng đôi tầng 1, có ban công'),
-- Tầng 2
('P201', 'Phòng đơn', 20, 2100000, 'daThue', 'Phòng đơn tầng 2, view đẹp'),
('P202', 'Phòng đơn', 18, 1900000, 'daThue', 'Phòng đơn tầng 2, yên tĩnh'),
('P203', 'Phòng đôi', 25, 2600000, 'daThue', 'Phòng đôi tầng 2, rộng rãi'),
('P204', 'Phòng đơn', 20, 2100000, 'trong', 'Phòng đơn tầng 2, sạch sẽ'),
('P205', 'Phòng đôi', 28, 2900000, 'daThue', 'Phòng đôi tầng 2, có ban công'),
-- Tầng 3
('P301', 'Phòng đơn', 20, 2200000, 'daThue', 'Phòng đơn tầng 3, view đẹp'),
('P302', 'Phòng đơn', 18, 2000000, 'trong', 'Phòng đơn tầng 3, mới sửa'),
('P303', 'Phòng đôi', 25, 2700000, 'daThue', 'Phòng đôi tầng 3, rộng rãi'),
('P304', 'Phòng đơn', 20, 2200000, 'baoTri', 'Phòng đơn tầng 3, đang bảo trì'),
('P305', 'Phòng đôi', 28, 3000000, 'daThue', 'Phòng đôi tầng 3, có ban công'),
-- Tầng 4
('P401', 'Phòng đơn', 20, 2300000, 'daThue', 'Phòng đơn tầng 4, view đẹp'),
('P402', 'Phòng đơn', 18, 2100000, 'trong', 'Phòng đơn tầng 4, sạch sẽ'),
('P403', 'Phòng đôi', 25, 2800000, 'daThue', 'Phòng đôi tầng 4, rộng rãi'),
('P404', 'Phòng đơn', 20, 2300000, 'daThue', 'Phòng đơn tầng 4, yên tĩnh'),
('P405', 'Phòng đôi', 28, 3100000, 'trong', 'Phòng đôi tầng 4, có ban công, view đẹp');

-- ============================================
-- 2. DỮ LIỆU KHÁCH HÀNG (25 khách hàng)
-- ============================================
INSERT INTO KhachHang (tenKhachHang, soCccd, soDienThoai, queQuan, ngheNghiep, ngaySinh, gioiTinh, ghiChu) VALUES
('Nguyễn Văn An', '001234567890', '0901234567', 'Hà Nội', 'Sinh viên', '2002-03-15', 'Nam', 'Sinh viên Đại học Bách Khoa'),
('Trần Thị Bình', '001234567891', '0901234568', 'Hải Phòng', 'Nhân viên văn phòng', '1998-05-20', 'Nữ', 'Làm việc tại quận 1'),
('Lê Văn Cường', '001234567892', '0901234569', 'Đà Nẵng', 'Kỹ sư', '1995-07-10', 'Nam', 'Kỹ sư phần mềm'),
('Phạm Thị Dung', '001234567893', '0901234570', 'Cần Thơ', 'Giáo viên', '1993-09-25', 'Nữ', 'Giáo viên tiểu học'),
('Hoàng Văn Em', '001234567894', '0901234571', 'Hà Nội', 'Sinh viên', '2003-11-30', 'Nam', 'Sinh viên năm 2'),
('Vũ Thị Phương', '001234567895', '0901234572', 'Hải Dương', 'Nhân viên bán hàng', '1997-01-12', 'Nữ', 'Làm tại siêu thị'),
('Đặng Văn Giang', '001234567896', '0901234573', 'Nam Định', 'Công nhân', '1990-04-18', 'Nam', 'Công nhân nhà máy'),
('Bùi Thị Hoa', '001234567897', '0901234574', 'Thái Bình', 'Y tá', '1994-06-22', 'Nữ', 'Y tá bệnh viện'),
('Ngô Văn Ích', '001234567898', '0901234575', 'Hưng Yên', 'Sinh viên', '2001-08-05', 'Nam', 'Sinh viên năm 3'),
('Đỗ Thị Khuê', '001234567899', '0901234576', 'Hà Nam', 'Nhân viên ngân hàng', '1996-10-15', 'Nữ', 'Nhân viên ngân hàng'),
('Lý Văn Long', '001234567900', '0901234577', 'Bắc Ninh', 'Kỹ sư', '1992-12-20', 'Nam', 'Kỹ sư xây dựng'),
('Võ Thị Mai', '001234567901', '0901234578', 'Quảng Ninh', 'Giáo viên', '1991-02-14', 'Nữ', 'Giáo viên THPT'),
('Đinh Văn Nam', '001234567902', '0901234579', 'Lào Cai', 'Sinh viên', '2004-04-08', 'Nam', 'Sinh viên năm 1'),
('Trương Thị Oanh', '001234567903', '0901234580', 'Yên Bái', 'Nhân viên văn phòng', '1999-06-25', 'Nữ', 'Nhân viên công ty'),
('Phan Văn Phúc', '001234567904', '0901234581', 'Tuyên Quang', 'Bác sĩ', '1989-08-30', 'Nam', 'Bác sĩ đa khoa'),
('Lưu Thị Quỳnh', '001234567905', '0901234582', 'Phú Thọ', 'Sinh viên', '2002-10-12', 'Nữ', 'Sinh viên năm 2'),
('Chu Văn Rạng', '001234567906', '0901234583', 'Vĩnh Phúc', 'Kỹ sư', '1993-12-18', 'Nam', 'Kỹ sư điện'),
('Dương Thị Sen', '001234567907', '0901234584', 'Bắc Giang', 'Nhân viên bán hàng', '1998-02-22', 'Nữ', 'Nhân viên shop'),
('Hồ Văn Tâm', '001234567908', '0901234585', 'Bắc Kạn', 'Sinh viên', '2003-04-05', 'Nam', 'Sinh viên năm 1'),
('Tôn Thị Uyên', '001234567909', '0901234586', 'Cao Bằng', 'Giáo viên', '1995-06-10', 'Nữ', 'Giáo viên mầm non'),
('Vương Văn Vinh', '001234567910', '0901234587', 'Lạng Sơn', 'Kỹ sư', '1990-08-15', 'Nam', 'Kỹ sư cơ khí'),
('Lâm Thị Xuân', '001234567911', '0901234588', 'Thái Nguyên', 'Nhân viên văn phòng', '1997-10-20', 'Nữ', 'Nhân viên công ty'),
('Mai Văn Yên', '001234567912', '0901234589', 'Hòa Bình', 'Sinh viên', '2001-12-25', 'Nam', 'Sinh viên năm 3'),
('Lê Thị Zin', '001234567913', '0901234590', 'Sơn La', 'Y tá', '1994-02-28', 'Nữ', 'Y tá phòng khám'),
('Nguyễn Văn A', '001234567914', '0901234591', 'Điện Biên', 'Công nhân', '1991-04-03', 'Nam', 'Công nhân xây dựng');

-- ============================================
-- 3. DỮ LIỆU HỢP ĐỒNG (15 hợp đồng đang thuê)
-- ============================================
INSERT INTO HopDongThue (idPhong, idKhachHang, ngayBatDau, ngayKetThuc, tienCoc, trangThai, ghiChu) VALUES
(1, 1, '2024-09-01', '2025-09-01', 2000000, 'dangThue', 'Hợp đồng 1 năm'),
(2, 2, '2024-10-01', '2025-10-01', 1800000, 'dangThue', 'Hợp đồng 1 năm'),
(3, 3, '2024-08-15', '2025-08-15', 2500000, 'dangThue', 'Hợp đồng 1 năm'),
(5, 4, '2024-11-01', '2025-11-01', 2800000, 'dangThue', 'Hợp đồng 1 năm'),
(6, 5, '2024-09-15', '2025-09-15', 2100000, 'dangThue', 'Hợp đồng 1 năm'),
(7, 6, '2024-10-15', '2025-10-15', 1900000, 'dangThue', 'Hợp đồng 1 năm'),
(8, 7, '2024-08-01', '2025-08-01', 2600000, 'dangThue', 'Hợp đồng 1 năm'),
(10, 8, '2024-11-15', '2025-11-15', 2900000, 'dangThue', 'Hợp đồng 1 năm'),
(11, 9, '2024-09-01', '2025-09-01', 2200000, 'dangThue', 'Hợp đồng 1 năm'),
(13, 10, '2024-10-01', '2025-10-01', 2700000, 'dangThue', 'Hợp đồng 1 năm'),
(15, 11, '2024-08-15', '2025-08-15', 3000000, 'dangThue', 'Hợp đồng 1 năm'),
(16, 12, '2024-11-01', '2025-11-01', 2300000, 'dangThue', 'Hợp đồng 1 năm'),
(18, 13, '2024-09-15', '2025-09-15', 2800000, 'dangThue', 'Hợp đồng 1 năm'),
(19, 14, '2024-10-15', '2025-10-15', 2300000, 'dangThue', 'Hợp đồng 1 năm'),
(20, 15, '2024-08-01', '2025-08-01', 3100000, 'dangThue', 'Hợp đồng 1 năm');

-- Hợp đồng đã kết thúc (để test lịch sử)
INSERT INTO HopDongThue (idPhong, idKhachHang, ngayBatDau, ngayKetThuc, tienCoc, trangThai, ghiChu) VALUES
(4, 16, '2023-01-01', '2024-01-01', 2000000, 'daKetThuc', 'Hợp đồng đã kết thúc'),
(9, 17, '2023-02-01', '2024-02-01', 2100000, 'daKetThuc', 'Hợp đồng đã kết thúc'),
(12, 18, '2023-03-01', '2024-03-01', 2000000, 'daKetThuc', 'Hợp đồng đã kết thúc');

-- ============================================
-- 4. DỮ LIỆU DỊCH VỤ
-- ============================================
INSERT INTO DichVu (tenDichVu, donGia, moTa) VALUES
('Wifi', 100000, 'Dịch vụ internet tốc độ cao'),
('Giữ xe máy', 50000, 'Dịch vụ giữ xe máy an toàn'),
('Giữ xe ô tô', 200000, 'Dịch vụ giữ xe ô tô'),
('Rác', 20000, 'Phí thu gom rác thải'),
('Vệ sinh chung', 30000, 'Dịch vụ vệ sinh khu vực chung'),
('Nước uống', 50000, 'Nước uống đóng chai'),
('Giặt ủi', 100000, 'Dịch vụ giặt ủi quần áo'),
('Bảo vệ', 150000, 'Dịch vụ bảo vệ 24/7');

-- ============================================
-- 5. DỮ LIỆU ĐIỆN (12 tháng gần nhất cho các phòng đang thuê)
-- ============================================
-- Phòng 1 - 12 tháng (1/2024 - 12/2024)
INSERT INTO Dien (idPhong, thang, nam, chiSoCu, chiSoMoi, donGia) VALUES
(1, 1, 2024, 0, 50, 3500),
(1, 2, 2024, 50, 120, 3500),
(1, 3, 2024, 120, 200, 3500),
(1, 4, 2024, 200, 280, 3500),
(1, 5, 2024, 280, 360, 3500),
(1, 6, 2024, 360, 450, 3500),
(1, 7, 2024, 450, 550, 3500),
(1, 8, 2024, 550, 650, 3500),
(1, 9, 2024, 650, 750, 3500),
(1, 10, 2024, 750, 850, 3500),
(1, 11, 2024, 850, 950, 3500),
(1, 12, 2024, 950, 1050, 3500);

-- Phòng 2 - 3 tháng gần nhất
INSERT INTO Dien (idPhong, thang, nam, chiSoCu, chiSoMoi, donGia) VALUES
(2, 10, 2024, 0, 80, 3500),
(2, 11, 2024, 80, 160, 3500),
(2, 12, 2024, 160, 240, 3500);

-- Phòng 3 - 6 tháng gần nhất
INSERT INTO Dien (idPhong, thang, nam, chiSoCu, chiSoMoi, donGia) VALUES
(3, 7, 2024, 0, 100, 3500),
(3, 8, 2024, 100, 200, 3500),
(3, 9, 2024, 200, 300, 3500),
(3, 10, 2024, 300, 400, 3500),
(3, 11, 2024, 400, 500, 3500),
(3, 12, 2024, 500, 600, 3500);

-- Phòng 5, 6, 7, 8, 10, 11, 13, 15, 16, 18, 19, 20 - 3 tháng gần nhất
INSERT INTO Dien (idPhong, thang, nam, chiSoCu, chiSoMoi, donGia) VALUES
(5, 10, 2024, 0, 90, 3500), (5, 11, 2024, 90, 180, 3500), (5, 12, 2024, 180, 270, 3500),
(6, 10, 2024, 0, 70, 3500), (6, 11, 2024, 70, 140, 3500), (6, 12, 2024, 140, 210, 3500),
(7, 10, 2024, 0, 85, 3500), (7, 11, 2024, 85, 170, 3500), (7, 12, 2024, 170, 255, 3500),
(8, 10, 2024, 0, 95, 3500), (8, 11, 2024, 95, 190, 3500), (8, 12, 2024, 190, 285, 3500),
(10, 10, 2024, 0, 75, 3500), (10, 11, 2024, 75, 150, 3500), (10, 12, 2024, 150, 225, 3500),
(11, 10, 2024, 0, 65, 3500), (11, 11, 2024, 65, 130, 3500), (11, 12, 2024, 130, 195, 3500),
(13, 10, 2024, 0, 110, 3500), (13, 11, 2024, 110, 220, 3500), (13, 12, 2024, 220, 330, 3500),
(15, 10, 2024, 0, 120, 3500), (15, 11, 2024, 120, 240, 3500), (15, 12, 2024, 240, 360, 3500),
(16, 10, 2024, 0, 88, 3500), (16, 11, 2024, 88, 176, 3500), (16, 12, 2024, 176, 264, 3500),
(18, 10, 2024, 0, 92, 3500), (18, 11, 2024, 92, 184, 3500), (18, 12, 2024, 184, 276, 3500),
(19, 10, 2024, 0, 78, 3500), (19, 11, 2024, 78, 156, 3500), (19, 12, 2024, 156, 234, 3500),
(20, 10, 2024, 0, 105, 3500), (20, 11, 2024, 105, 210, 3500), (20, 12, 2024, 210, 315, 3500);

-- ============================================
-- 6. DỮ LIỆU NƯỚC (tương ứng với điện)
-- ============================================
-- Phòng 1 - 12 tháng
INSERT INTO Nuoc (idPhong, thang, nam, soKhoi, donGia) VALUES
(1, 1, 2024, 3, 15000),
(1, 2, 2024, 4, 15000),
(1, 3, 2024, 5, 15000),
(1, 4, 2024, 4, 15000),
(1, 5, 2024, 6, 15000),
(1, 6, 2024, 7, 15000),
(1, 7, 2024, 8, 15000),
(1, 8, 2024, 7, 15000),
(1, 9, 2024, 6, 15000),
(1, 10, 2024, 5, 15000),
(1, 11, 2024, 4, 15000),
(1, 12, 2024, 5, 15000);

-- Các phòng khác - 3 tháng gần nhất
INSERT INTO Nuoc (idPhong, thang, nam, soKhoi, donGia) VALUES
(2, 10, 2024, 4, 15000), (2, 11, 2024, 5, 15000), (2, 12, 2024, 4, 15000),
(3, 7, 2024, 6, 15000), (3, 8, 2024, 7, 15000), (3, 9, 2024, 6, 15000),
(3, 10, 2024, 5, 15000), (3, 11, 2024, 6, 15000), (3, 12, 2024, 7, 15000),
(5, 10, 2024, 5, 15000), (5, 11, 2024, 6, 15000), (5, 12, 2024, 5, 15000),
(6, 10, 2024, 3, 15000), (6, 11, 2024, 4, 15000), (6, 12, 2024, 3, 15000),
(7, 10, 2024, 4, 15000), (7, 11, 2024, 5, 15000), (7, 12, 2024, 4, 15000),
(8, 10, 2024, 6, 15000), (8, 11, 2024, 7, 15000), (8, 12, 2024, 6, 15000),
(10, 10, 2024, 4, 15000), (10, 11, 2024, 5, 15000), (10, 12, 2024, 4, 15000),
(11, 10, 2024, 3, 15000), (11, 11, 2024, 4, 15000), (11, 12, 2024, 3, 15000),
(13, 10, 2024, 7, 15000), (13, 11, 2024, 8, 15000), (13, 12, 2024, 7, 15000),
(15, 10, 2024, 8, 15000), (15, 11, 2024, 9, 15000), (15, 12, 2024, 8, 15000),
(16, 10, 2024, 5, 15000), (16, 11, 2024, 6, 15000), (16, 12, 2024, 5, 15000),
(18, 10, 2024, 6, 15000), (18, 11, 2024, 7, 15000), (18, 12, 2024, 6, 15000),
(19, 10, 2024, 4, 15000), (19, 11, 2024, 5, 15000), (19, 12, 2024, 4, 15000),
(20, 10, 2024, 7, 15000), (20, 11, 2024, 8, 15000), (20, 12, 2024, 7, 15000);

-- ============================================
-- 7. DỮ LIỆU HÓA ĐƠN (sẽ được tạo tự động bởi trigger, nhưng thêm một số thủ công)
-- ============================================
-- Hóa đơn tháng 12/2024 cho các phòng (một số đã thanh toán)
INSERT INTO HoaDon (idPhong, thang, nam, tienPhong, tienDien, tienNuoc, tienDichVu, tienKhac, tongTien, trangThai) VALUES
(1, 12, 2024, 2000000, 350000, 75000, 200000, 0, 2625000, 'daThanhToan'),
(2, 12, 2024, 1800000, 280000, 60000, 150000, 0, 2290000, 'chuaThanhToan'),
(3, 12, 2024, 2500000, 350000, 105000, 250000, 0, 3205000, 'daThanhToan'),
(5, 12, 2024, 2800000, 315000, 75000, 200000, 0, 3390000, 'chuaThanhToan'),
(6, 12, 2024, 2100000, 245000, 45000, 150000, 0, 2540000, 'daThanhToan'),
(7, 12, 2024, 1900000, 297500, 60000, 180000, 0, 2437500, 'chuaThanhToan'),
(8, 12, 2024, 2600000, 332500, 90000, 250000, 0, 3272500, 'daThanhToan'),
(10, 12, 2024, 2900000, 262500, 60000, 200000, 0, 3422500, 'chuaThanhToan'),
(11, 12, 2024, 2200000, 227500, 45000, 150000, 0, 2622500, 'daThanhToan'),
(13, 12, 2024, 2700000, 385000, 105000, 300000, 0, 3490000, 'chuaThanhToan'),
(15, 12, 2024, 3000000, 420000, 120000, 350000, 0, 3890000, 'daThanhToan'),
(16, 12, 2024, 2300000, 308000, 75000, 200000, 0, 2883000, 'chuaThanhToan'),
(18, 12, 2024, 2800000, 322000, 90000, 250000, 0, 3462000, 'daThanhToan'),
(19, 12, 2024, 2300000, 273000, 60000, 180000, 0, 2813000, 'chuaThanhToan'),
(20, 12, 2024, 3100000, 367500, 105000, 300000, 0, 3872500, 'daThanhToan');

-- ============================================
-- 8. DỮ LIỆU HÓA ĐƠN DỊCH VỤ (chi tiết dịch vụ trong hóa đơn)
-- ============================================
-- Dịch vụ cho hóa đơn tháng 12/2024
INSERT INTO HoaDonDichVu (idHoaDon, idDichVu, soLuong, donGia) VALUES
-- Phòng 1: Wifi + Giữ xe + Rác
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 1 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 1 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 1 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 1 AND thang = 12 AND nam = 2024), 5, 1, 30000),
-- Phòng 2: Wifi + Giữ xe
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 2 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 2 AND thang = 12 AND nam = 2024), 2, 1, 50000),
-- Phòng 3: Wifi + Giữ xe + Rác + Vệ sinh
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 3 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 3 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 3 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 3 AND thang = 12 AND nam = 2024), 5, 1, 30000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 3 AND thang = 12 AND nam = 2024), 7, 1, 100000),
-- Phòng 5: Wifi + Giữ xe + Rác
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 5 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 5 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 5 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 5 AND thang = 12 AND nam = 2024), 5, 1, 30000),
-- Phòng 6: Wifi + Giữ xe
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 6 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 6 AND thang = 12 AND nam = 2024), 2, 1, 50000),
-- Phòng 7: Wifi + Giữ xe + Rác + Vệ sinh
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 7 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 7 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 7 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 7 AND thang = 12 AND nam = 2024), 5, 1, 30000),
-- Phòng 8: Wifi + Giữ xe + Rác + Vệ sinh + Giặt ủi
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 8 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 8 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 8 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 8 AND thang = 12 AND nam = 2024), 5, 1, 30000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 8 AND thang = 12 AND nam = 2024), 7, 1, 100000),
-- Phòng 10: Wifi + Giữ xe + Rác
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 10 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 10 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 10 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 10 AND thang = 12 AND nam = 2024), 5, 1, 30000),
-- Phòng 11: Wifi + Giữ xe
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 11 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 11 AND thang = 12 AND nam = 2024), 2, 1, 50000),
-- Phòng 13: Wifi + Giữ xe + Rác + Vệ sinh + Giặt ủi
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 13 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 13 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 13 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 13 AND thang = 12 AND nam = 2024), 5, 1, 30000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 13 AND thang = 12 AND nam = 2024), 7, 1, 100000),
-- Phòng 15: Wifi + Giữ xe ô tô + Rác + Vệ sinh + Giặt ủi
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 15 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 15 AND thang = 12 AND nam = 2024), 3, 1, 200000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 15 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 15 AND thang = 12 AND nam = 2024), 5, 1, 30000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 15 AND thang = 12 AND nam = 2024), 7, 1, 100000),
-- Phòng 16: Wifi + Giữ xe + Rác
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 16 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 16 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 16 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 16 AND thang = 12 AND nam = 2024), 5, 1, 30000),
-- Phòng 18: Wifi + Giữ xe + Rác + Vệ sinh + Giặt ủi
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 18 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 18 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 18 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 18 AND thang = 12 AND nam = 2024), 5, 1, 30000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 18 AND thang = 12 AND nam = 2024), 7, 1, 100000),
-- Phòng 19: Wifi + Giữ xe + Rác + Vệ sinh
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 19 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 19 AND thang = 12 AND nam = 2024), 2, 1, 50000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 19 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 19 AND thang = 12 AND nam = 2024), 5, 1, 30000),
-- Phòng 20: Wifi + Giữ xe ô tô + Rác + Vệ sinh + Giặt ủi
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 20 AND thang = 12 AND nam = 2024), 1, 1, 100000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 20 AND thang = 12 AND nam = 2024), 3, 1, 200000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 20 AND thang = 12 AND nam = 2024), 4, 1, 20000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 20 AND thang = 12 AND nam = 2024), 5, 1, 30000),
((SELECT idHoaDon FROM HoaDon WHERE idPhong = 20 AND thang = 12 AND nam = 2024), 7, 1, 100000);

-- ============================================
-- 9. DỮ LIỆU TÀI KHOẢN (tài khoản cho người thuê)
-- ============================================
-- Tài khoản admin (mật khẩu: admin - đã hash)
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, hoTen) VALUES
('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'admin', 'Quản trị hệ thống');

-- Tài khoản cho người thuê (username: kh{idKhachHang}, mật khẩu mặc định: 123456 - đã hash)
-- Lưu ý: Trong thực tế, mật khẩu sẽ được tạo ngẫu nhiên khi tạo hợp đồng
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, hoTen) VALUES
('kh1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Nguyễn Văn An'),
('kh2', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Trần Thị Bình'),
('kh3', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Lê Văn Cường'),
('kh4', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Phạm Thị Dung'),
('kh5', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Hoàng Văn Em'),
('kh6', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Vũ Thị Phương'),
('kh7', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Đặng Văn Giang'),
('kh8', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Bùi Thị Hoa'),
('kh9', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Ngô Văn Ích'),
('kh10', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Đỗ Thị Khuê'),
('kh11', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Lý Văn Long'),
('kh12', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Võ Thị Mai'),
('kh13', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Đinh Văn Nam'),
('kh14', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Trương Thị Oanh'),
('kh15', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Phan Văn Phúc');

-- ============================================
-- 10. DỮ LIỆU THU CHI (một số giao dịch mẫu)
-- ============================================
INSERT INTO ThuChi (ngayLap, loai, soTien, nguon, ghiChu, idPhong) VALUES
('2024-12-01', 'THU', 2625000, 'Phòng P101', 'Thu tiền phòng tháng 12', 1),
('2024-12-02', 'THU', 3205000, 'Phòng P103', 'Thu tiền phòng tháng 12', 3),
('2024-12-03', 'THU', 2540000, 'Phòng P201', 'Thu tiền phòng tháng 12', 6),
('2024-12-04', 'THU', 3272500, 'Phòng P203', 'Thu tiền phòng tháng 12', 8),
('2024-12-05', 'THU', 2622500, 'Phòng P301', 'Thu tiền phòng tháng 12', 11),
('2024-12-06', 'THU', 3890000, 'Phòng P305', 'Thu tiền phòng tháng 12', 15),
('2024-12-07', 'THU', 3462000, 'Phòng P403', 'Thu tiền phòng tháng 12', 18),
('2024-12-08', 'THU', 3872500, 'Phòng P405', 'Thu tiền phòng tháng 12', 20),
('2024-12-10', 'CHI', 500000, 'Sửa máy lạnh', 'Thay block máy lạnh phòng P102', 2),
('2024-12-12', 'CHI', 300000, 'Sửa điện', 'Sửa chập điện phòng P201', 6),
('2024-12-15', 'CHI', 200000, 'Mua bàn ghế', 'Mua bàn ghế mới cho phòng P204', 9),
('2024-12-18', 'CHI', 1500000, 'Sơn phòng', 'Sơn lại phòng P302', 12),
('2024-12-20', 'CHI', 800000, 'Sửa ống nước', 'Sửa rò rỉ ống nước phòng P304', 14);

-- ============================================
-- KẾT THÚC DỮ LIỆU MẪU
-- ============================================
-- Tổng kết:
-- - 20 phòng (15 đang thuê, 4 trống, 1 bảo trì)
-- - 25 khách hàng
-- - 18 hợp đồng (15 đang thuê, 3 đã kết thúc)
-- - 8 dịch vụ
-- - Dữ liệu điện/nước cho 12 tháng (phòng 1) và 3-6 tháng (các phòng khác)
-- - 15 hóa đơn tháng 12/2024 với chi tiết dịch vụ
-- - 16 tài khoản (1 admin + 15 người thuê)
-- - 13 giao dịch thu chi

