-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               8.0.36 - MySQL Community Server - GPL
-- Server OS:                    Linux
-- HeidiSQL Version:             12.14.0.7165
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for QuanLyNhaTro
CREATE DATABASE IF NOT EXISTS `QuanLyNhaTro` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `QuanLyNhaTro`;

-- Dumping structure for procedure QuanLyNhaTro.add_index_if_missing
DELIMITER //
CREATE PROCEDURE `add_index_if_missing`(IN p_table VARCHAR(64), IN p_index VARCHAR(64), IN p_cols TEXT)
BEGIN
    DECLARE idx_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO idx_exists
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = p_table AND INDEX_NAME = p_index;
    IF idx_exists = 0 THEN
        SET @s = CONCAT('ALTER TABLE ', p_table, ' ADD INDEX ', p_index, ' ', p_cols);
        PREPARE stmt FROM @s;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

-- Dumping structure for table QuanLyNhaTro.DichVu
CREATE TABLE IF NOT EXISTS `DichVu` (
  `idDichVu` int NOT NULL AUTO_INCREMENT,
  `tenDichVu` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `donGia` double NOT NULL,
  `moTa` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idDichVu`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.DichVu: ~8 rows (approximately)
INSERT INTO `DichVu` (`idDichVu`, `tenDichVu`, `donGia`, `moTa`, `ngayTao`) VALUES
	(1, 'Wifi', 100000, 'Wifi tốc độ cao', '2026-01-09 12:49:47'),
	(2, 'Giữ xe', 50000, 'Phí giữ xe máy tháng', '2026-01-09 12:49:47'),
	(3, 'Rác', 20000, 'Phí vệ sinh rác thải', '2026-01-09 12:49:47'),
	(4, 'Nước nóng', 30000, 'Phí nước nóng', '2026-01-09 12:49:47'),
	(5, 'Bảo trì', 50000, 'Phí bảo trì chung cư', '2026-01-09 12:49:47'),
	(6, 'An ninh', 40000, 'Phí bảo vệ 24/7', '2026-01-09 12:49:47'),
	(7, 'Giặt là', 80000, 'Dịch vụ giặt ủi', '2026-01-09 12:49:47'),
	(8, 'Truyền hình cáp', 120000, 'Gói truyền hình cơ bản', '2026-01-09 12:49:47');

-- Dumping structure for table QuanLyNhaTro.Dien
CREATE TABLE IF NOT EXISTS `Dien` (
  `idDien` int NOT NULL AUTO_INCREMENT,
  `idPhong` int NOT NULL,
  `thang` tinyint NOT NULL,
  `nam` int NOT NULL,
  `chiSoCu` double DEFAULT '0',
  `chiSoMoi` double DEFAULT '0',
  `donGia` double DEFAULT '3500',
  `thanhTien` double GENERATED ALWAYS AS (((`chiSoMoi` - `chiSoCu`) * `donGia`)) STORED,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idDien`),
  UNIQUE KEY `ukDienPhongThangNam` (`idPhong`,`thang`,`nam`),
  KEY `idx_dien_phong_thang_nam` (`idPhong`,`thang`,`nam`),
  CONSTRAINT `Dien_ibfk_1` FOREIGN KEY (`idPhong`) REFERENCES `Phong` (`idPhong`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Dien_chk_1` CHECK ((`thang` between 1 and 12)),
  CONSTRAINT `Dien_chk_2` CHECK ((`nam` >= 2000))
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.Dien: ~36 rows (approximately)
INSERT INTO `Dien` (`idDien`, `idPhong`, `thang`, `nam`, `chiSoCu`, `chiSoMoi`, `donGia`, `ngayTao`) VALUES
	(1, 1, 10, 2025, 1000, 1100, 3500, '2026-01-09 12:49:47'),
	(2, 2, 10, 2025, 800, 900, 3500, '2026-01-09 12:49:47'),
	(3, 3, 10, 2025, 1200, 1320, 3500, '2026-01-09 12:49:47'),
	(4, 6, 10, 2025, 600, 680, 3500, '2026-01-09 12:49:47'),
	(5, 7, 10, 2025, 750, 850, 3500, '2026-01-09 12:49:47'),
	(6, 8, 10, 2025, 1100, 1220, 3500, '2026-01-09 12:49:47'),
	(7, 11, 10, 2025, 400, 465, 3500, '2026-01-09 12:49:47'),
	(8, 12, 10, 2025, 650, 730, 3500, '2026-01-09 12:49:47'),
	(9, 14, 10, 2025, 1500, 1650, 3500, '2026-01-09 12:49:47'),
	(10, 1, 11, 2025, 1100, 1210, 3500, '2026-01-09 12:49:47'),
	(11, 2, 11, 2025, 900, 1005, 3500, '2026-01-09 12:49:47'),
	(12, 3, 11, 2025, 1320, 1450, 3500, '2026-01-09 12:49:47'),
	(13, 6, 11, 2025, 680, 765, 3500, '2026-01-09 12:49:47'),
	(14, 7, 11, 2025, 850, 955, 3500, '2026-01-09 12:49:47'),
	(15, 8, 11, 2025, 1220, 1350, 3500, '2026-01-09 12:49:47'),
	(16, 11, 11, 2025, 465, 535, 3500, '2026-01-09 12:49:47'),
	(17, 12, 11, 2025, 730, 815, 3500, '2026-01-09 12:49:47'),
	(18, 14, 11, 2025, 1650, 1810, 3500, '2026-01-09 12:49:47'),
	(19, 1, 12, 2025, 1210, 1325, 3500, '2026-01-09 12:49:48'),
	(20, 2, 12, 2025, 1005, 1115, 3500, '2026-01-09 12:49:48'),
	(21, 3, 12, 2025, 1450, 1590, 3500, '2026-01-09 12:49:48'),
	(22, 6, 12, 2025, 765, 855, 3500, '2026-01-09 12:49:48'),
	(23, 7, 12, 2025, 955, 1065, 3500, '2026-01-09 12:49:48'),
	(24, 8, 12, 2025, 1350, 1490, 3500, '2026-01-09 12:49:48'),
	(25, 11, 12, 2025, 535, 610, 3500, '2026-01-09 12:49:48'),
	(26, 12, 12, 2025, 815, 905, 3500, '2026-01-09 12:49:48'),
	(27, 14, 12, 2025, 1810, 1980, 3500, '2026-01-09 12:49:48'),
	(28, 1, 1, 2026, 1325, 1333, 3500, '2026-01-09 12:49:48'),
	(29, 2, 1, 2026, 1115, 1230, 3500, '2026-01-09 12:49:48'),
	(30, 3, 1, 2026, 1590, 1740, 3500, '2026-01-09 12:49:48'),
	(31, 6, 1, 2026, 855, 950, 3500, '2026-01-09 12:49:48'),
	(32, 7, 1, 2026, 1065, 1180, 3500, '2026-01-09 12:49:48'),
	(33, 8, 1, 2026, 1490, 1640, 3500, '2026-01-09 12:49:48'),
	(34, 11, 1, 2026, 610, 690, 3500, '2026-01-09 12:49:48'),
	(35, 12, 1, 2026, 905, 1000, 3500, '2026-01-09 12:49:48'),
	(36, 14, 1, 2026, 1980, 2160, 3500, '2026-01-09 12:49:48');

-- Dumping structure for procedure QuanLyNhaTro.drop_index_if_exists
DELIMITER //
CREATE PROCEDURE `drop_index_if_exists`(IN p_table VARCHAR(64), IN p_index VARCHAR(64))
BEGIN
    DECLARE idx_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO idx_exists
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = p_table AND INDEX_NAME = p_index;
    IF idx_exists > 0 THEN
        SET @s = CONCAT('ALTER TABLE ', p_table, ' DROP INDEX ', p_index);
        PREPARE stmt FROM @s;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

-- Dumping structure for event QuanLyNhaTro.evCheckExpiredContracts
DELIMITER //
CREATE EVENT `evCheckExpiredContracts` ON SCHEDULE EVERY 1 DAY STARTS '2026-01-09 11:33:48' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
    UPDATE HopDongThue h
    JOIN Phong p ON p.idPhong = h.idPhong
    SET h.trangThai = 'daKetThuc', p.trangThai = 'trong'
    WHERE h.ngayKetThuc IS NOT NULL
      AND h.ngayKetThuc < CURRENT_DATE()
      AND h.trangThai <> 'daKetThuc';
END//
DELIMITER ;

-- Dumping structure for function QuanLyNhaTro.fnTinhTongHoaDon
DELIMITER //
CREATE FUNCTION `fnTinhTongHoaDon`(pIdPhong INT, pThang TINYINT, pNam INT) RETURNS double
    DETERMINISTIC
BEGIN
    DECLARE vTienPhong DOUBLE DEFAULT 0;
    DECLARE vTienDien DOUBLE DEFAULT 0;
    DECLARE vTienNuoc DOUBLE DEFAULT 0;
    DECLARE vTienDichVu DOUBLE DEFAULT 0;
    DECLARE vTong DOUBLE DEFAULT 0;
    SELECT giaThue INTO vTienPhong FROM Phong WHERE idPhong = pIdPhong LIMIT 1;
    SELECT COALESCE(SUM(thanhTien),0) INTO vTienDien FROM Dien WHERE idPhong = pIdPhong AND thang = pThang AND nam = pNam;
    SELECT COALESCE(SUM(thanhTien),0) INTO vTienNuoc FROM Nuoc WHERE idPhong = pIdPhong AND thang = pThang AND nam = pNam;
    SELECT COALESCE(SUM(hddv.thanhTien),0) INTO vTienDichVu
    FROM HoaDon hd
    JOIN HoaDonDichVu hddv ON hd.idHoaDon = hddv.idHoaDon
    WHERE hd.idPhong = pIdPhong AND hd.thang = pThang AND hd.nam = pNam;
    SET vTong = COALESCE(vTienPhong,0) + COALESCE(vTienDien,0) + COALESCE(vTienNuoc,0) + COALESCE(vTienDichVu,0);
    RETURN vTong;
END//
DELIMITER ;

-- Dumping structure for table QuanLyNhaTro.HoaDon
CREATE TABLE IF NOT EXISTS `HoaDon` (
  `idHoaDon` int NOT NULL AUTO_INCREMENT,
  `idPhong` int NOT NULL,
  `thang` tinyint NOT NULL,
  `nam` int NOT NULL,
  `tienPhong` double DEFAULT '0',
  `tienDien` double DEFAULT '0',
  `tienNuoc` double DEFAULT '0',
  `tienDichVu` double DEFAULT '0',
  `tienKhac` double DEFAULT '0',
  `tongTien` double DEFAULT '0',
  `trangThai` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'chuaThanhToan',
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idHoaDon`),
  UNIQUE KEY `ukHoaDonPhongThangNam` (`idPhong`,`thang`,`nam`),
  KEY `idxHoaDonTrangThai` (`trangThai`),
  KEY `idxHoaDonThangNam` (`thang`,`nam`),
  KEY `idx_hoadon_thang_nam` (`thang`,`nam`),
  KEY `idx_hoadon_trangthai` (`trangThai`),
  KEY `idx_hoadon_phong` (`idPhong`),
  KEY `idx_hoadon_composite` (`idPhong`,`thang`,`nam`),
  KEY `idx_hoadon_ngaytao` (`ngayTao` DESC),
  CONSTRAINT `HoaDon_ibfk_1` FOREIGN KEY (`idPhong`) REFERENCES `Phong` (`idPhong`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `HoaDon_chk_1` CHECK ((`thang` between 1 and 12)),
  CONSTRAINT `HoaDon_chk_2` CHECK ((`nam` >= 2000))
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.HoaDon: ~45 rows (approximately)
INSERT INTO `HoaDon` (`idHoaDon`, `idPhong`, `thang`, `nam`, `tienPhong`, `tienDien`, `tienNuoc`, `tienDichVu`, `tienKhac`, `tongTien`, `trangThai`, `ngayTao`) VALUES
	(1, 1, 1, 2025, 2000000, 0, 0, 0, 0, 2000000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(2, 2, 2, 2025, 2200000, 0, 0, 0, 0, 2200000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(3, 3, 3, 2025, 2800000, 0, 0, 0, 0, 2800000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(4, 6, 1, 2025, 2100000, 0, 0, 0, 0, 2100000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(5, 7, 4, 2025, 2200000, 0, 0, 0, 0, 2200000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(6, 8, 5, 2025, 3000000, 0, 0, 0, 0, 3000000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(7, 11, 9, 2025, 1800000, 0, 0, 0, 0, 1800000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(8, 12, 6, 2025, 2000000, 0, 0, 0, 0, 2000000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(9, 14, 7, 2025, 3200000, 0, 0, 0, 0, 3200000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(10, 1, 1, 2024, 2000000, 0, 0, 0, 0, 2000000, 'chuaThanhToan', '2026-01-09 12:49:46'),
	(11, 1, 10, 2025, 2000000, 350000, 67500, 0, 0, 2417500, 'daThanhToan', '2026-01-09 12:49:47'),
	(12, 2, 10, 2025, 2200000, 350000, 57000, 0, 0, 2607000, 'daThanhToan', '2026-01-09 12:49:47'),
	(13, 3, 10, 2025, 2800000, 420000, 93000, 0, 0, 3313000, 'daThanhToan', '2026-01-09 12:49:47'),
	(14, 6, 10, 2025, 2100000, 280000, 52500, 0, 0, 2432500, 'daThanhToan', '2026-01-09 12:49:47'),
	(15, 7, 10, 2025, 2200000, 350000, 60000, 0, 0, 2610000, 'daThanhToan', '2026-01-09 12:49:47'),
	(16, 8, 10, 2025, 3000000, 420000, 82500, 0, 0, 3502500, 'daThanhToan', '2026-01-09 12:49:47'),
	(17, 11, 10, 2025, 1800000, 227500, 37500, 0, 0, 2065000, 'daThanhToan', '2026-01-09 12:49:47'),
	(18, 12, 10, 2025, 2000000, 280000, 48000, 0, 0, 2328000, 'daThanhToan', '2026-01-09 12:49:47'),
	(19, 14, 10, 2025, 3200000, 525000, 112500, 0, 0, 3837500, 'daThanhToan', '2026-01-09 12:49:47'),
	(20, 1, 11, 2025, 2000000, 385000, 72000, 0, 0, 2457000, 'daThanhToan', '2026-01-09 12:49:47'),
	(21, 2, 11, 2025, 2200000, 367500, 60000, 0, 0, 2627500, 'daThanhToan', '2026-01-09 12:49:47'),
	(22, 3, 11, 2025, 2800000, 455000, 97500, 0, 0, 3352500, 'chuaThanhToan', '2026-01-09 12:49:47'),
	(23, 6, 11, 2025, 2100000, 297500, 55500, 0, 0, 2453000, 'daThanhToan', '2026-01-09 12:49:47'),
	(24, 7, 11, 2025, 2200000, 367500, 63000, 0, 0, 2630500, 'daThanhToan', '2026-01-09 12:49:47'),
	(25, 8, 11, 2025, 3000000, 455000, 87000, 0, 0, 3542000, 'chuaThanhToan', '2026-01-09 12:49:47'),
	(26, 11, 11, 2025, 1800000, 245000, 42000, 0, 0, 2087000, 'chuaThanhToan', '2026-01-09 12:49:47'),
	(27, 12, 11, 2025, 2000000, 297500, 52500, 0, 0, 2350000, 'chuaThanhToan', '2026-01-09 12:49:47'),
	(28, 14, 11, 2025, 3200000, 560000, 117000, 0, 0, 3877000, 'chuaThanhToan', '2026-01-09 12:49:47'),
	(29, 1, 12, 2025, 2000000, 402500, 75000, 0, 0, 2477500, 'daThanhToan', '2026-01-09 12:49:48'),
	(30, 2, 12, 2025, 2200000, 385000, 63000, 0, 0, 2648000, 'chuaThanhToan', '2026-01-09 12:49:48'),
	(31, 3, 12, 2025, 2800000, 490000, 102000, 0, 0, 3392000, 'daThanhToan', '2026-01-09 12:49:48'),
	(32, 6, 12, 2025, 2100000, 315000, 58500, 0, 0, 2473500, 'chuaThanhToan', '2026-01-09 12:49:48'),
	(33, 7, 12, 2025, 2200000, 385000, 67500, 0, 0, 2652500, 'chuaThanhToan', '2026-01-09 12:49:48'),
	(34, 8, 12, 2025, 3000000, 490000, 90000, 0, 0, 3580000, 'daThanhToan', '2026-01-09 12:49:48'),
	(35, 11, 12, 2025, 1800000, 262500, 45000, 0, 0, 2107500, 'chuaThanhToan', '2026-01-09 12:49:48'),
	(36, 12, 12, 2025, 2000000, 315000, 55500, 0, 0, 2370500, 'chuaThanhToan', '2026-01-09 12:49:48'),
	(37, 14, 12, 2025, 3200000, 595000, 122999.99999999999, 0, 0, 3918000, 'chuaThanhToan', '2026-01-09 12:49:48'),
	(38, 1, 1, 2026, 2000000, 28000, 15000, 270000, 0, 2313000, 'chuaThanhToan', '2026-01-09 12:49:48'),
	(40, 3, 1, 2026, 2800000, 525000, 105000, 0, 0, 3430000, 'chuaThanhToan', '2026-01-09 12:49:48'),
	(41, 6, 1, 2026, 2100000, 332500, 60000, 0, 0, 2492500, 'daThanhToan', '2026-01-09 12:49:48'),
	(42, 7, 1, 2026, 2200000, 402500, 70500, 0, 0, 2673000, 'daThanhToan', '2026-01-09 12:49:48'),
	(43, 8, 1, 2026, 3000000, 525000, 94500, 0, 0, 3619500, 'daThanhToan', '2026-01-09 12:49:48'),
	(44, 11, 1, 2026, 1800000, 280000, 48000, 0, 0, 2128000, 'daThanhToan', '2026-01-09 12:49:48'),
	(45, 12, 1, 2026, 2000000, 332500, 58500, 0, 0, 2391000, 'daThanhToan', '2026-01-09 12:49:48'),
	(46, 14, 1, 2026, 3200000, 630000, 127500, 0, 0, 3957500, 'daThanhToan', '2026-01-09 12:49:48'),
	(47, 17, 1, 2026, 3800000, 0, 0, 0, 0, 3800000, 'chuaThanhToan', '2026-01-12 15:53:45');

-- Dumping structure for table QuanLyNhaTro.HoaDonDichVu
CREATE TABLE IF NOT EXISTS `HoaDonDichVu` (
  `idHoaDonDichVu` int NOT NULL AUTO_INCREMENT,
  `idHoaDon` int NOT NULL,
  `idDichVu` int NOT NULL,
  `soLuong` int DEFAULT '1',
  `donGia` double NOT NULL,
  `thanhTien` double GENERATED ALWAYS AS ((`soLuong` * `donGia`)) STORED,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idHoaDonDichVu`),
  KEY `idxHoaDonDichVu_HoaDon` (`idHoaDon`),
  KEY `idx_hoadondichvu_dichvu` (`idDichVu`),
  KEY `idx_hoadondichvu_hoadon` (`idHoaDon`),
  CONSTRAINT `HoaDonDichVu_ibfk_1` FOREIGN KEY (`idHoaDon`) REFERENCES `HoaDon` (`idHoaDon`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `HoaDonDichVu_ibfk_2` FOREIGN KEY (`idDichVu`) REFERENCES `DichVu` (`idDichVu`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.HoaDonDichVu: ~30 rows (approximately)
INSERT INTO `HoaDonDichVu` (`idHoaDonDichVu`, `idHoaDon`, `idDichVu`, `soLuong`, `donGia`, `ngayTao`) VALUES
	(1, 11, 1, 1, 100000, '2026-01-09 12:49:59'),
	(2, 11, 2, 1, 50000, '2026-01-09 12:49:59'),
	(3, 11, 3, 1, 20000, '2026-01-09 12:49:59'),
	(4, 13, 1, 1, 100000, '2026-01-09 12:49:59'),
	(5, 13, 2, 2, 50000, '2026-01-09 12:49:59'),
	(6, 13, 3, 1, 20000, '2026-01-09 12:49:59'),
	(7, 13, 8, 1, 120000, '2026-01-09 12:49:59'),
	(11, 16, 1, 1, 100000, '2026-01-09 12:50:00'),
	(12, 16, 2, 1, 50000, '2026-01-09 12:50:00'),
	(13, 16, 3, 1, 20000, '2026-01-09 12:50:00'),
	(14, 20, 1, 1, 100000, '2026-01-09 12:50:00'),
	(15, 20, 2, 1, 50000, '2026-01-09 12:50:00'),
	(16, 20, 3, 1, 20000, '2026-01-09 12:50:00'),
	(17, 22, 1, 1, 100000, '2026-01-09 12:50:00'),
	(18, 22, 2, 2, 50000, '2026-01-09 12:50:00'),
	(19, 22, 3, 1, 20000, '2026-01-09 12:50:00'),
	(20, 22, 8, 1, 120000, '2026-01-09 12:50:00'),
	(24, 25, 1, 1, 100000, '2026-01-09 12:50:01'),
	(25, 25, 2, 1, 50000, '2026-01-09 12:50:01'),
	(26, 25, 3, 1, 20000, '2026-01-09 12:50:01'),
	(27, 30, 1, 1, 100000, '2026-01-09 12:50:01'),
	(28, 30, 2, 1, 50000, '2026-01-09 12:50:01'),
	(29, 30, 3, 1, 20000, '2026-01-09 12:50:01'),
	(30, 37, 1, 1, 100000, '2026-01-09 12:50:01'),
	(31, 37, 2, 1, 50000, '2026-01-09 12:50:01'),
	(32, 37, 3, 1, 20000, '2026-01-09 12:50:01'),
	(43, 38, 3, 1, 20000, '2026-01-10 14:46:58'),
	(44, 38, 5, 1, 50000, '2026-01-10 14:46:58'),
	(45, 38, 6, 2, 40000, '2026-01-10 14:46:59'),
	(46, 38, 8, 1, 120000, '2026-01-10 14:47:00');

-- Dumping structure for table QuanLyNhaTro.HopDongThue
CREATE TABLE IF NOT EXISTS `HopDongThue` (
  `idHopDong` int NOT NULL AUTO_INCREMENT,
  `idPhong` int NOT NULL,
  `idKhachHang` int NOT NULL,
  `ngayBatDau` date NOT NULL,
  `ngayKetThuc` date DEFAULT NULL,
  `tienCoc` double DEFAULT '0',
  `trangThai` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'dangThue',
  `ghiChu` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idHopDong`),
  KEY `idxHopDongPhong` (`idPhong`),
  KEY `idxHopDongKhachHang` (`idKhachHang`),
  KEY `idxHopDongTrangThai` (`trangThai`),
  KEY `idx_hopdong_phong` (`idPhong`),
  KEY `idx_hopdong_khachhang` (`idKhachHang`),
  KEY `idx_hopdong_trangthai` (`trangThai`),
  KEY `idx_hopdong_ngaybatdau` (`ngayBatDau` DESC),
  KEY `idx_hopdong_phong_trangthai` (`idPhong`,`trangThai`),
  KEY `idx_hopdong_khachhang_trangthai` (`idKhachHang`,`trangThai`),
  CONSTRAINT `HopDongThue_ibfk_1` FOREIGN KEY (`idPhong`) REFERENCES `Phong` (`idPhong`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `HopDongThue_ibfk_2` FOREIGN KEY (`idKhachHang`) REFERENCES `KhachHang` (`idKhachHang`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.HopDongThue: ~11 rows (approximately)
INSERT INTO `HopDongThue` (`idHopDong`, `idPhong`, `idKhachHang`, `ngayBatDau`, `ngayKetThuc`, `tienCoc`, `trangThai`, `ghiChu`, `ngayTao`) VALUES
	(1, 1, 1, '2025-01-01', '2026-01-10', 2000000, 'daKetThuc', 'Hợp đồng 12 tháng, tự động gia hạn', '2026-01-09 12:49:46'),
	(2, 2, 2, '2025-02-01', NULL, 2200000, 'dangThue', 'Hợp đồng 6 tháng', '2026-01-09 12:49:46'),
	(3, 3, 3, '2025-03-01', NULL, 2800000, 'dangThue', 'Thuê dài hạn 1 năm', '2026-01-09 12:49:46'),
	(4, 6, 4, '2025-01-15', NULL, 2100000, 'dangThue', 'Khách hàng thân thiết', '2026-01-09 12:49:46'),
	(5, 7, 5, '2025-04-01', NULL, 2200000, 'dangThue', NULL, '2026-01-09 12:49:46'),
	(6, 8, 6, '2025-05-01', NULL, 3000000, 'dangThue', 'Làm ca đêm', '2026-01-09 12:49:46'),
	(7, 11, 7, '2025-09-01', NULL, 1800000, 'dangThue', 'Sinh viên giá ưu đãi', '2026-01-09 12:49:46'),
	(8, 12, 8, '2025-06-01', NULL, 2000000, 'dangThue', NULL, '2026-01-09 12:49:46'),
	(9, 14, 9, '2025-07-01', NULL, 3200000, 'dangThue', 'Phòng rộng cho gia đình', '2026-01-09 12:49:46'),
	(10, 1, 10, '2024-01-01', '2024-12-31', 2000000, 'daKetThuc', 'Hợp đồng cũ đã hết hạn', '2026-01-09 12:49:46'),
	(11, 1, 1, '2026-01-10', '2026-02-10', 2000000, 'dangThue', NULL, '2026-01-10 06:10:11'),
	(12, 17, 11, '2026-01-12', '2026-07-12', 3800000, 'dangThue', NULL, '2026-01-12 15:53:45');

-- Dumping structure for table QuanLyNhaTro.KhachHang
CREATE TABLE IF NOT EXISTS `KhachHang` (
  `idKhachHang` int NOT NULL AUTO_INCREMENT,
  `tenKhachHang` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `soCccd` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `soDienThoai` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `queQuan` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngheNghiep` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngaySinh` date DEFAULT NULL,
  `gioiTinh` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ghiChu` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idKhachHang`),
  UNIQUE KEY `soCccd` (`soCccd`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_khachhang_ten` (`tenKhachHang`),
  KEY `idx_khachhang_sdt` (`soDienThoai`),
  KEY `idx_khachhang_email` (`email`),
  CONSTRAINT `chk_email_format` CHECK ((`email` like _utf8mb4'%@%.%'))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.KhachHang: ~10 rows (approximately)
INSERT INTO `KhachHang` (`idKhachHang`, `tenKhachHang`, `soCccd`, `soDienThoai`, `email`, `queQuan`, `ngheNghiep`, `ngaySinh`, `gioiTinh`, `ghiChu`, `ngayTao`) VALUES
	(1, 'Bùi Thanh Quân', '001234567890', '0903111222', 'thanhhquan710@gmail.com', 'Hòa Bình', 'Lập trình viên', '2004-10-07', 'Nam', 'Khách hàng VIP', '2026-01-09 12:49:46'),
	(2, 'Nguyễn Thị Mai', '001234567891', '0904222333', 'khachhang2@nhatro.com', 'Thái Bình', 'Giáo viên', '1995-05-15', 'Nữ', 'Thanh toán đúng hạn', '2026-01-09 12:49:46'),
	(3, 'Trần Văn Long', '001234567892', '0905333444', 'khachhang3@nhatro.com', 'Hà Nội', 'Kỹ sư', '1992-08-20', 'Nam', 'Có xe hơi', '2026-01-09 12:49:46'),
	(4, 'Lê Thị Hoa', '001234567893', '0906444555', 'khachhang4@nhatro.com', 'Nam Định', 'Nhân viên văn phòng', '1996-03-10', 'Nữ', NULL, '2026-01-09 12:49:46'),
	(5, 'Hoàng Bảo Phúc', '001234567894', '0907555666', 'khachhang5@nhatro.com', 'Ninh Bình', 'Kinh doanh', '2003-12-25', 'Nam', 'Đi công tác nhiều', '2026-01-09 12:49:46'),
	(6, 'Hoàng Thị Lan', '001234567895', '0908666777', 'khachhang6@nhatro.com', 'Hưng Yên', 'Y tá', '1998-07-18', 'Nữ', 'Làm ca 3', '2026-01-09 12:49:46'),
	(7, 'Kim Ngân', '001234567896', '0909777888', 'khachhang7@nhatro.com', 'TP. Hồ Chí Minh', 'Sinh viên', '2006-11-24', 'Nữ', 'Học đại học KMA', '2026-01-09 12:49:46'),
	(8, 'Vũ Bá Pháo', '001234567897', '0910888999', 'khachhang8@nhatro.com', 'Hà Nội', 'Kế toán', '2004-10-07', 'Nam', 'Sống một mình', '2026-01-09 12:49:46'),
	(9, 'Nguyễn Văn Đức', '001234567898', '0911999000', 'khachhang9@nhatro.com', 'Hà Nội', 'Công nhân', '1997-09-12', 'Nam', NULL, '2026-01-09 12:49:46'),
	(10, 'Phạm Thùy Dung', '001234567899', '0912000111', 'khachhang10@nhatro.com', 'Nam Định', 'Nhân viên bán hàng', '2007-11-02', 'Nữ', 'Thường xuyên OT', '2026-01-09 12:49:46'),
	(11, 'Hồng Nhung', '017204000001', '0123321123', 'nhungbeibie@gmail.com', 'Hòa Bình', 'Sinh Viên HNUE', '2004-02-22', 'Nữ', 'Sinh viên năm cuối', '2026-01-12 15:53:04');

-- Dumping structure for table QuanLyNhaTro.Nuoc
CREATE TABLE IF NOT EXISTS `Nuoc` (
  `idNuoc` int NOT NULL AUTO_INCREMENT,
  `idPhong` int NOT NULL,
  `thang` tinyint NOT NULL,
  `nam` int NOT NULL,
  `soKhoi` double DEFAULT '0',
  `donGia` double DEFAULT '15000',
  `thanhTien` double GENERATED ALWAYS AS ((`soKhoi` * `donGia`)) STORED,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idNuoc`),
  UNIQUE KEY `ukNuocPhongThangNam` (`idPhong`,`thang`,`nam`),
  KEY `idx_nuoc_phong_thang_nam` (`idPhong`,`thang`,`nam`),
  CONSTRAINT `Nuoc_ibfk_1` FOREIGN KEY (`idPhong`) REFERENCES `Phong` (`idPhong`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Nuoc_chk_1` CHECK ((`thang` between 1 and 12)),
  CONSTRAINT `Nuoc_chk_2` CHECK ((`nam` >= 2000))
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.Nuoc: ~36 rows (approximately)
INSERT INTO `Nuoc` (`idNuoc`, `idPhong`, `thang`, `nam`, `soKhoi`, `donGia`, `ngayTao`) VALUES
	(1, 1, 10, 2025, 4.5, 15000, '2026-01-09 12:49:48'),
	(2, 2, 10, 2025, 3.8, 15000, '2026-01-09 12:49:48'),
	(3, 3, 10, 2025, 6.2, 15000, '2026-01-09 12:49:48'),
	(4, 6, 10, 2025, 3.5, 15000, '2026-01-09 12:49:48'),
	(5, 7, 10, 2025, 4, 15000, '2026-01-09 12:49:48'),
	(6, 8, 10, 2025, 5.5, 15000, '2026-01-09 12:49:48'),
	(7, 11, 10, 2025, 2.5, 15000, '2026-01-09 12:49:48'),
	(8, 12, 10, 2025, 3.2, 15000, '2026-01-09 12:49:48'),
	(9, 14, 10, 2025, 7.5, 15000, '2026-01-09 12:49:48'),
	(10, 1, 11, 2025, 4.8, 15000, '2026-01-09 12:49:49'),
	(11, 2, 11, 2025, 4, 15000, '2026-01-09 12:49:49'),
	(12, 3, 11, 2025, 6.5, 15000, '2026-01-09 12:49:49'),
	(13, 6, 11, 2025, 3.7, 15000, '2026-01-09 12:49:49'),
	(14, 7, 11, 2025, 4.2, 15000, '2026-01-09 12:49:49'),
	(15, 8, 11, 2025, 5.8, 15000, '2026-01-09 12:49:49'),
	(16, 11, 11, 2025, 2.8, 15000, '2026-01-09 12:49:49'),
	(17, 12, 11, 2025, 3.5, 15000, '2026-01-09 12:49:49'),
	(18, 14, 11, 2025, 7.8, 15000, '2026-01-09 12:49:49'),
	(19, 1, 12, 2025, 5, 15000, '2026-01-09 12:49:49'),
	(20, 2, 12, 2025, 4.2, 15000, '2026-01-09 12:49:49'),
	(21, 3, 12, 2025, 6.8, 15000, '2026-01-09 12:49:49'),
	(22, 6, 12, 2025, 3.9, 15000, '2026-01-09 12:49:49'),
	(23, 7, 12, 2025, 4.5, 15000, '2026-01-09 12:49:49'),
	(24, 8, 12, 2025, 6, 15000, '2026-01-09 12:49:49'),
	(25, 11, 12, 2025, 3, 15000, '2026-01-09 12:49:49'),
	(26, 12, 12, 2025, 3.7, 15000, '2026-01-09 12:49:49'),
	(27, 14, 12, 2025, 8.2, 15000, '2026-01-09 12:49:49'),
	(28, 1, 1, 2026, 1, 15000, '2026-01-09 12:49:49'),
	(29, 2, 1, 2026, 4.5, 15000, '2026-01-09 12:49:49'),
	(30, 3, 1, 2026, 7, 15000, '2026-01-09 12:49:49'),
	(31, 6, 1, 2026, 4, 15000, '2026-01-09 12:49:49'),
	(32, 7, 1, 2026, 4.7, 15000, '2026-01-09 12:49:49'),
	(33, 8, 1, 2026, 6.3, 15000, '2026-01-09 12:49:49'),
	(34, 11, 1, 2026, 3.2, 15000, '2026-01-09 12:49:49'),
	(35, 12, 1, 2026, 3.9, 15000, '2026-01-09 12:49:49'),
	(36, 14, 1, 2026, 8.5, 15000, '2026-01-09 12:49:49');

-- Dumping structure for table QuanLyNhaTro.Phong
CREATE TABLE IF NOT EXISTS `Phong` (
  `idPhong` int NOT NULL AUTO_INCREMENT,
  `tenPhong` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `loaiPhong` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dienTich` double DEFAULT NULL,
  `giaThue` double NOT NULL,
  `trangThai` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'trong',
  `moTa` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idPhong`),
  KEY `idxPhongTrangThai` (`trangThai`),
  KEY `idx_phong_trangthai` (`trangThai`),
  KEY `idx_phong_tenphong` (`tenPhong`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.Phong: ~15 rows (approximately)
INSERT INTO `Phong` (`idPhong`, `tenPhong`, `loaiPhong`, `dienTich`, `giaThue`, `trangThai`, `moTa`, `ngayTao`) VALUES
	(1, 'P101', 'don', 20, 2000000, 'dangThue', 'Phòng đơn tầng 1, gần cửa chính', '2026-01-09 12:49:46'),
	(2, 'P102', 'don', 22, 2200000, 'dangThue', 'Phòng có ban công nhỏ', '2026-01-09 12:49:46'),
	(3, 'P103', 'doi', 28, 2800000, 'dangThue', 'Phòng đôi rộng rãi', '2026-01-09 12:49:46'),
	(4, 'P104', 'doi', 30, 3000000, 'trong', 'Phòng đôi có điều hòa', '2026-01-09 12:49:46'),
	(5, 'P105', 'don', 20, 2000000, 'trong', 'Phòng cuối hành lang tầng 1', '2026-01-09 12:49:46'),
	(6, 'P201', 'don', 20, 2100000, 'dangThue', 'Phòng tầng 2 thoáng mát', '2026-01-09 12:49:46'),
	(7, 'P202', 'don', 22, 2200000, 'dangThue', 'Phòng view đẹp', '2026-01-09 12:49:46'),
	(8, 'P203', 'doi', 30, 3000000, 'dangThue', 'Phòng đôi có gác lửng', '2026-01-09 12:49:46'),
	(9, 'P204', 'doi', 28, 2800000, 'trong', 'Phòng góc 2 cửa sổ', '2026-01-09 12:49:46'),
	(10, 'P205', 'vip', 35, 3500000, 'trong', 'Phòng VIP đầy đủ tiện nghi', '2026-01-09 12:49:46'),
	(11, 'P301', 'don', 18, 1800000, 'dangThue', 'Phòng kinh tế cho sinh viên', '2026-01-09 12:49:46'),
	(12, 'P302', 'don', 20, 2000000, 'dangThue', 'Phòng đơn tiêu chuẩn', '2026-01-09 12:49:46'),
	(13, 'P303', 'doi', 25, 2500000, 'trong', 'Phòng đôi cửa sổ lớn', '2026-01-09 12:49:46'),
	(14, 'P304', 'doi', 32, 3200000, 'dangThue', 'Phòng rộng có ban công', '2026-01-09 12:49:46'),
	(15, 'P305', 'vip', 40, 4000000, 'baoTri', 'Phòng VIP đang sửa chữa', '2026-01-09 12:49:46'),
	(17, 'P69', 'don', 24, 3800000, 'dangThue', 'Đẹp thoáng, siêu tiện ích, chung cư mini', '2026-01-12 15:51:15');

-- Dumping structure for procedure QuanLyNhaTro.spGetDashboardStats
DELIMITER //
CREATE PROCEDURE `spGetDashboardStats`()
BEGIN
    DECLARE current_month INT DEFAULT MONTH(CURDATE());
    DECLARE current_year INT DEFAULT YEAR(CURDATE());
    
    SELECT 
        (SELECT COUNT(*) FROM Phong) AS totalPhong,
        (SELECT COUNT(*) FROM Phong WHERE trangThai = 'daThue') AS phongDaThue,
        (SELECT COUNT(*) FROM Phong WHERE trangThai = 'trong') AS phongTrong,
        (SELECT COUNT(*) FROM Phong WHERE trangThai = 'baoTri') AS phongBaoTri,
        (SELECT COUNT(*) FROM KhachHang) AS totalKhachHang,
        (SELECT COUNT(*) FROM HopDongThue WHERE trangThai = 'dangThue') AS hopDongActive,
        (SELECT COUNT(*) FROM HopDongThue WHERE trangThai = 'daKetThuc') AS hopDongDone,
        (SELECT COUNT(*) FROM HopDongThue) AS totalHopDong,
        (SELECT COALESCE(SUM(tongTien), 0) 
         FROM HoaDon 
         WHERE thang = current_month AND nam = current_year) AS revenueThisMonth,
        (SELECT COUNT(*) FROM HoaDon WHERE trangThai = 'daThanhToan') AS hoaDonPaid,
        (SELECT COUNT(*) FROM HoaDon WHERE trangThai = 'chuaThanhToan') AS hoaDonUnpaid;
END//
DELIMITER ;

-- Dumping structure for procedure QuanLyNhaTro.spGetLastUtilityReadings
DELIMITER //
CREATE PROCEDURE `spGetLastUtilityReadings`(
    IN p_idPhong INT,
    IN p_thang INT,
    IN p_nam INT
)
BEGIN
    DECLARE prev_thang INT;
    DECLARE prev_nam INT;
    
    -- Tính tháng trước
    IF p_thang = 1 THEN
        SET prev_thang = 12;
        SET prev_nam = p_nam - 1;
    ELSE
        SET prev_thang = p_thang - 1;
        SET prev_nam = p_nam;
    END IF;
    
    -- Lấy cả điện và nước trong 1 query
    SELECT 
        COALESCE(d.chiSoMoi, 0) AS dienCu,
        COALESCE(
            (SELECT SUM(soKhoi) 
             FROM Nuoc 
             WHERE idPhong = p_idPhong 
             AND (nam < p_nam OR (nam = p_nam AND thang < p_thang))), 
            0
        ) AS nuocCu
    FROM Phong p
    LEFT JOIN Dien d ON d.idPhong = p.idPhong 
        AND d.thang = prev_thang 
        AND d.nam = prev_nam
    WHERE p.idPhong = p_idPhong;
END//
DELIMITER ;

-- Dumping structure for procedure QuanLyNhaTro.spGetRevenueByMonths
DELIMITER //
CREATE PROCEDURE `spGetRevenueByMonths`(IN numMonths INT)
BEGIN
    WITH RECURSIVE months AS (
        SELECT 
            YEAR(CURDATE() - INTERVAL (numMonths - 1) MONTH) AS year,
            MONTH(CURDATE() - INTERVAL (numMonths - 1) MONTH) AS month,
            0 AS iter
        UNION ALL
        SELECT 
            YEAR(CURDATE() - INTERVAL (numMonths - 1 - iter - 1) MONTH),
            MONTH(CURDATE() - INTERVAL (numMonths - 1 - iter - 1) MONTH),
            iter + 1
        FROM months
        WHERE iter < numMonths - 1
    )
    SELECT 
        m.month AS thang,
        m.year AS nam,
        COALESCE(SUM(h.tongTien), 0) AS doanhThu
    FROM months m
    LEFT JOIN HoaDon h ON h.thang = m.month AND h.nam = m.year
    GROUP BY m.month, m.year
    ORDER BY m.year, m.month;
END//
DELIMITER ;

-- Dumping structure for procedure QuanLyNhaTro.spTaoHoaDonChoPhong
DELIMITER //
CREATE PROCEDURE `spTaoHoaDonChoPhong`(
    IN pIdPhong INT,
    IN pThang TINYINT,
    IN pNam INT
)
BEGIN
    DECLARE vTienPhong DOUBLE DEFAULT 0;
    DECLARE vTienDien DOUBLE DEFAULT 0;
    DECLARE vTienNuoc DOUBLE DEFAULT 0;
    DECLARE vTienDichVu DOUBLE DEFAULT 0;
    DECLARE vTienKhac DOUBLE DEFAULT 0;
    DECLARE vTong DOUBLE DEFAULT 0;
    -- Lấy tiền phòng hiện tại
    SELECT giaThue INTO vTienPhong FROM Phong WHERE idPhong = pIdPhong LIMIT 1;
    -- Lấy tiền điện nếu có bản ghi tương ứng
    SELECT COALESCE(SUM(thanhTien),0) INTO vTienDien
    FROM Dien
    WHERE idPhong = pIdPhong AND thang = pThang AND nam = pNam;
    -- Lấy tiền nước nếu có
    SELECT COALESCE(SUM(thanhTien),0) INTO vTienNuoc
    FROM Nuoc
    WHERE idPhong = pIdPhong AND thang = pThang AND nam = pNam;
    -- Tính tiền dịch vụ từ HoaDonDichVu (nếu hóa đơn đã tồn tại)
    SELECT COALESCE(SUM(thanhTien),0) INTO vTienDichVu
    FROM HoaDonDichVu hddv
    JOIN HoaDon hd ON hddv.idHoaDon = hd.idHoaDon
    WHERE hd.idPhong = pIdPhong AND hd.thang = pThang AND hd.nam = pNam;
    -- Tổng
    SET vTong = COALESCE(vTienPhong,0) + COALESCE(vTienDien,0) + COALESCE(vTienNuoc,0) + COALESCE(vTienDichVu,0) + COALESCE(vTienKhac,0);
    -- Upsert vào HoaDon
    IF EXISTS (SELECT 1 FROM HoaDon WHERE idPhong = pIdPhong AND thang = pThang AND nam = pNam) THEN
        UPDATE HoaDon
        SET tienPhong = vTienPhong,
            tienDien = vTienDien,
            tienNuoc = vTienNuoc,
            tienDichVu = vTienDichVu,
            tienKhac = vTienKhac,
            tongTien = vTong
        WHERE idPhong = pIdPhong AND thang = pThang AND nam = pNam;
    ELSE
        INSERT INTO HoaDon (idPhong, thang, nam, tienPhong, tienDien, tienNuoc, tienDichVu, tienKhac, tongTien)
        VALUES (pIdPhong, pThang, pNam, vTienPhong, vTienDien, vTienNuoc, vTienDichVu, vTienKhac, vTong);
    END IF;
END//
DELIMITER ;

-- Dumping structure for table QuanLyNhaTro.TaiKhoan
CREATE TABLE IF NOT EXISTS `TaiKhoan` (
  `idTaiKhoan` int NOT NULL AUTO_INCREMENT,
  `tenDangNhap` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `matKhau` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `vaiTro` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'admin',
  `hoTen` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `idKhachHang` int DEFAULT NULL,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idTaiKhoan`),
  UNIQUE KEY `tenDangNhap` (`tenDangNhap`),
  KEY `idx_taikhoan_tendangnhap` (`tenDangNhap`),
  KEY `idx_taikhoan_vaitro` (`vaiTro`),
  KEY `idx_taikhoan_khachhang` (`idKhachHang`),
  CONSTRAINT `fk_taikhoan_khachhang` FOREIGN KEY (`idKhachHang`) REFERENCES `KhachHang` (`idKhachHang`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.TaiKhoan: ~8 rows (approximately)
INSERT INTO `TaiKhoan` (`idTaiKhoan`, `tenDangNhap`, `matKhau`, `vaiTro`, `hoTen`, `idKhachHang`, `ngayTao`) VALUES
	(2, 'nhanvien1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'nhanVien', 'Nguyễn Nhân Viên', NULL, '2026-01-09 10:33:48'),
	(3, 'nhanvien2', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'nhanVien', 'Trần Quản Lý', NULL, '2026-01-09 10:33:48'),
	(4, 'user1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'user', 'Người dùng thử nghiệm', NULL, '2026-01-09 10:33:48'),
	(5, 'admin', '$2a$12$NdFDOGA73wk4STC6Sw8vru5m0cvEcmFPyukyTZzResNAow6Y1Kh6.', 'admin', 'Quản trị hệ thống', NULL, '2026-01-09 10:35:14'),
	(6, 'nhanvien', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'nhanVien', 'Nguyễn Văn Nhân Viên', NULL, '2026-01-09 12:50:02'),
	(7, 'ketoan', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqJZxKxK5u', 'nhanVien', 'Trần Thị Kế Toán', NULL, '2026-01-09 12:50:02'),
	(8, 'admin@kma.com', '$2a$12$5oL/XACvxSw3s2iKl6BXiubXezOzUnOtcZ.Wq/z0xfYl9ApYnrhDy', 'user', 'Bùi Thanh Quân', 1, '2026-01-10 06:10:13'),
	(9, 'user2', '$2a$12$A6ruKnPL6fBU0/iyxDfxMeG.5p68/g53SBtPqitXoU2u61gAjJ8Xu', 'nhanVien', 'Bộ PC', NULL, '2026-01-12 13:46:57'),
	(10, 'nhungbeibie@gmail.com', '$2a$12$A1lx4xoOXO39a0sTrHbuku8cMGGQKqjOqMMLikwAnDCUIIOnk2UVm', 'user', 'Hồng Nhung', 11, '2026-01-12 15:53:45');

-- Dumping structure for table QuanLyNhaTro.ThuChi
CREATE TABLE IF NOT EXISTS `ThuChi` (
  `idThuChi` int NOT NULL AUTO_INCREMENT,
  `ngayLap` date NOT NULL,
  `loai` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `soTien` double NOT NULL,
  `danhMuc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ghiChu` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `idPhong` int DEFAULT NULL,
  `ngayTao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idThuChi`),
  KEY `idPhong` (`idPhong`),
  CONSTRAINT `ThuChi_ibfk_1` FOREIGN KEY (`idPhong`) REFERENCES `Phong` (`idPhong`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table QuanLyNhaTro.ThuChi: ~33 rows (approximately)
INSERT INTO `ThuChi` (`idThuChi`, `ngayLap`, `loai`, `soTien`, `danhMuc`, `ghiChu`, `idPhong`, `ngayTao`) VALUES
	(1, '2025-10-05', 'THU', 2500000, 'TienPhong', 'Thu tiền P101 tháng 10', 1, '2026-01-09 12:50:02'),
	(2, '2025-10-05', 'THU', 2700000, 'TienPhong', 'Thu tiền P102 tháng 10', 2, '2026-01-09 12:50:02'),
	(3, '2025-10-05', 'THU', 3500000, 'TienPhong', 'Thu tiền P103 tháng 10', 3, '2026-01-09 12:50:02'),
	(4, '2025-10-06', 'THU', 2600000, 'TienPhong', 'Thu tiền P201 tháng 10', 6, '2026-01-09 12:50:02'),
	(5, '2025-10-06', 'THU', 2700000, 'TienPhong', 'Thu tiền P202 tháng 10', 7, '2026-01-09 12:50:02'),
	(6, '2025-10-07', 'THU', 3500000, 'TienPhong', 'Thu tiền P203 tháng 10', 8, '2026-01-09 12:50:02'),
	(7, '2025-10-07', 'THU', 2000000, 'TienPhong', 'Thu tiền P301 tháng 10', 11, '2026-01-09 12:50:02'),
	(8, '2025-10-08', 'THU', 2400000, 'TienPhong', 'Thu tiền P302 tháng 10', 12, '2026-01-09 12:50:02'),
	(9, '2025-10-08', 'THU', 3800000, 'TienPhong', 'Thu tiền P304 tháng 10', 14, '2026-01-09 12:50:02'),
	(10, '2025-11-05', 'THU', 2550000, 'TienPhong', 'Thu tiền P101 tháng 11', 1, '2026-01-09 12:50:02'),
	(11, '2025-11-05', 'THU', 2750000, 'TienPhong', 'Thu tiền P102 tháng 11', 2, '2026-01-09 12:50:02'),
	(12, '2025-11-06', 'THU', 2650000, 'TienPhong', 'Thu tiền P201 tháng 11', 6, '2026-01-09 12:50:02'),
	(13, '2025-11-06', 'THU', 2750000, 'TienPhong', 'Thu tiền P202 tháng 11', 7, '2026-01-09 12:50:02'),
	(14, '2025-12-05', 'THU', 2600000, 'TienPhong', 'Thu tiền P101 tháng 12', 1, '2026-01-09 12:50:02'),
	(15, '2025-12-05', 'THU', 3600000, 'TienPhong', 'Thu tiền P103 tháng 12', 3, '2026-01-09 12:50:02'),
	(16, '2025-12-06', 'THU', 3600000, 'TienPhong', 'Thu tiền P203 tháng 12', 8, '2026-01-09 12:50:02'),
	(17, '2025-10-15', 'CHI', 500000, 'SuaChua', 'Sửa ống nước tầng 2', NULL, '2026-01-09 12:50:02'),
	(18, '2025-10-20', 'CHI', 1200000, 'TienDien', 'Tiền điện khu vực chung tháng 9', NULL, '2026-01-09 12:50:02'),
	(19, '2025-11-01', 'CHI', 1300000, 'TienDien', 'Tiền điện khu vực chung tháng 10', NULL, '2026-01-09 12:50:02'),
	(20, '2025-11-10', 'CHI', 300000, 'VeSinh', 'Dọn vệ sinh khu vực chung', NULL, '2026-01-09 12:50:02'),
	(21, '2025-11-15', 'CHI', 800000, 'SuaChua', 'Sửa cửa P305', 15, '2026-01-09 12:50:02'),
	(22, '2025-12-01', 'CHI', 1400000, 'TienDien', 'Tiền điện khu vực chung tháng 11', NULL, '2026-01-09 12:50:02'),
	(23, '2025-12-10', 'CHI', 600000, 'SuaChua', 'Thay bóng đèn hành lang', NULL, '2026-01-09 12:50:02'),
	(24, '2025-12-20', 'CHI', 2500000, 'MuaSam', 'Mua máy lạnh mới cho P204', 9, '2026-01-09 12:50:02'),
	(25, '2026-01-10', 'THU', 3957500, 'TienPhong', 'Thu tiền P304 tháng 1', 14, '2026-01-10 13:47:59'),
	(26, '2026-01-10', 'CHI', 200000, 'Khac', 'Sửa máy giặt', NULL, '2026-01-10 13:48:53'),
	(27, '2026-01-10', 'CHI', 888888, 'MuaSam', 'Mua truyền hình K+ xem sách thể thao :D', 5, '2026-01-10 14:00:38'),
	(28, '2026-01-10', 'THU', 2391000, 'TienPhong', 'Thu tiền P302 tháng 1', 12, '2026-01-10 14:01:14'),
	(29, '2026-01-10', 'THU', 2673000, 'TienPhong', 'Thu tiền P202 tháng 1', 7, '2026-01-10 14:01:30'),
	(30, '2026-01-10', 'THU', 2128000, 'TienPhong', 'Thu tiền P301 tháng 1', 11, '2026-01-10 14:16:45'),
	(31, '2026-01-10', 'THU', 3619500, 'TienPhong', 'Thu tiền P203 tháng 1', 8, '2026-01-10 14:17:02'),
	(32, '2026-01-10', 'THU', 2492500, 'TienPhong', 'Thu tiền P201 tháng 1', 6, '2026-01-10 14:45:45'),
	(33, '2026-01-12', 'CHI', 350000, 'NhanCong', 'Dọn dẹp vệ sinh tòa nhà', NULL, '2026-01-12 13:45:10');

-- Dumping structure for view QuanLyNhaTro.v_HoaDonDetail
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `v_HoaDonDetail` (
	`idHoaDon` INT NOT NULL,
	`idPhong` INT NOT NULL,
	`thang` TINYINT NOT NULL,
	`nam` INT NOT NULL,
	`tienPhong` DOUBLE NULL,
	`tienDien` DOUBLE NULL,
	`tienNuoc` DOUBLE NULL,
	`tienDichVu` DOUBLE NULL,
	`tienKhac` DOUBLE NULL,
	`tongTien` DOUBLE NULL,
	`trangThai` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`ngayTao` TIMESTAMP NULL,
	`tenPhong` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`loaiPhong` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`idKhachHang` INT NULL,
	`tenKhachHang` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`email` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`soDienThoai` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci'
);

-- Dumping structure for view QuanLyNhaTro.v_HopDongDetail
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `v_HopDongDetail` (
	`idHopDong` INT NOT NULL,
	`idPhong` INT NOT NULL,
	`idKhachHang` INT NOT NULL,
	`ngayBatDau` DATE NOT NULL,
	`ngayKetThuc` DATE NULL,
	`tienCoc` DOUBLE NULL,
	`trangThai` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`ghiChu` TEXT NULL COLLATE 'utf8mb4_unicode_ci',
	`ngayTao` TIMESTAMP NULL,
	`tenPhong` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`loaiPhong` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`giaThue` DOUBLE NULL,
	`tenKhachHang` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`soDienThoai` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`email` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`soCccd` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci'
);

-- Dumping structure for trigger QuanLyNhaTro.trgAfterInsertDien
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trgAfterInsertDien` AFTER INSERT ON `Dien` FOR EACH ROW BEGIN
    CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger QuanLyNhaTro.trgAfterInsertHopDong
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trgAfterInsertHopDong` AFTER INSERT ON `HopDongThue` FOR EACH ROW BEGIN
    UPDATE Phong SET trangThai = 'daThue' WHERE idPhong = NEW.idPhong;
    CALL spTaoHoaDonChoPhong(NEW.idPhong, MONTH(NEW.ngayBatDau), YEAR(NEW.ngayBatDau));
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger QuanLyNhaTro.trgAfterInsertNuoc
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trgAfterInsertNuoc` AFTER INSERT ON `Nuoc` FOR EACH ROW BEGIN
    CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger QuanLyNhaTro.trgAfterUpdateDien
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trgAfterUpdateDien` AFTER UPDATE ON `Dien` FOR EACH ROW BEGIN
    CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger QuanLyNhaTro.trgAfterUpdateHopDong
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trgAfterUpdateHopDong` AFTER UPDATE ON `HopDongThue` FOR EACH ROW BEGIN
    IF NEW.trangThai = 'daKetThuc' THEN
        UPDATE Phong SET trangThai = 'trong' WHERE idPhong = NEW.idPhong;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger QuanLyNhaTro.trgAfterUpdateNuoc
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trgAfterUpdateNuoc` AFTER UPDATE ON `Nuoc` FOR EACH ROW BEGIN
    CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger QuanLyNhaTro.trgBeforeUpdateHopDong
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trgBeforeUpdateHopDong` BEFORE UPDATE ON `HopDongThue` FOR EACH ROW BEGIN
    IF NEW.ngayKetThuc IS NOT NULL AND NEW.ngayKetThuc <= CURRENT_DATE() THEN
        SET NEW.trangThai = 'daKetThuc';
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `v_HoaDonDetail`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_HoaDonDetail` AS select `h`.`idHoaDon` AS `idHoaDon`,`h`.`idPhong` AS `idPhong`,`h`.`thang` AS `thang`,`h`.`nam` AS `nam`,`h`.`tienPhong` AS `tienPhong`,`h`.`tienDien` AS `tienDien`,`h`.`tienNuoc` AS `tienNuoc`,`h`.`tienDichVu` AS `tienDichVu`,`h`.`tienKhac` AS `tienKhac`,`h`.`tongTien` AS `tongTien`,`h`.`trangThai` AS `trangThai`,`h`.`ngayTao` AS `ngayTao`,`p`.`tenPhong` AS `tenPhong`,`p`.`loaiPhong` AS `loaiPhong`,`hd`.`idKhachHang` AS `idKhachHang`,`kh`.`tenKhachHang` AS `tenKhachHang`,`kh`.`email` AS `email`,`kh`.`soDienThoai` AS `soDienThoai` from (((`HoaDon` `h` left join `Phong` `p` on((`p`.`idPhong` = `h`.`idPhong`))) left join `HopDongThue` `hd` on(((`hd`.`idPhong` = `h`.`idPhong`) and (`hd`.`trangThai` = 'dangThue')))) left join `KhachHang` `kh` on((`kh`.`idKhachHang` = `hd`.`idKhachHang`)))
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `v_HopDongDetail`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_HopDongDetail` AS select `hd`.`idHopDong` AS `idHopDong`,`hd`.`idPhong` AS `idPhong`,`hd`.`idKhachHang` AS `idKhachHang`,`hd`.`ngayBatDau` AS `ngayBatDau`,`hd`.`ngayKetThuc` AS `ngayKetThuc`,`hd`.`tienCoc` AS `tienCoc`,`hd`.`trangThai` AS `trangThai`,`hd`.`ghiChu` AS `ghiChu`,`hd`.`ngayTao` AS `ngayTao`,`p`.`tenPhong` AS `tenPhong`,`p`.`loaiPhong` AS `loaiPhong`,`p`.`giaThue` AS `giaThue`,`kh`.`tenKhachHang` AS `tenKhachHang`,`kh`.`soDienThoai` AS `soDienThoai`,`kh`.`email` AS `email`,`kh`.`soCccd` AS `soCccd` from ((`HopDongThue` `hd` left join `Phong` `p` on((`p`.`idPhong` = `hd`.`idPhong`))) left join `KhachHang` `kh` on((`kh`.`idKhachHang` = `hd`.`idKhachHang`)))
;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
