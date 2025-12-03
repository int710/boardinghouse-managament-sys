-- File: QuanLyNhaTroFull_v2.sql
-- Mục đích: Tạo đầy đủ Database + Bảng + Indexes + Sample Data + Triggers + Stored Procedures + Event
-- Chạy trên MySQL 8.x

-- =========================
-- 0) (TÙY CHỌN) Bật Event Scheduler nếu có quyền SUPER
-- Nếu có quyền SUPER, bỏ comment dòng dưới để bật scheduler.
-- SET GLOBAL event_scheduler = ON;

-- =========================
-- 1) TẠO DATABASE
-- =========================
DROP DATABASE IF EXISTS QuanLyNhaTro;
CREATE DATABASE QuanLyNhaTro
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE QuanLyNhaTro;

-- =========================
-- 2) TẠO BẢNG (PascalCase bảng, CamelCase cột)
-- =========================

-- Bảng: Phong (phòng trọ)
CREATE TABLE Phong (
    idPhong INT AUTO_INCREMENT PRIMARY KEY,
    tenPhong VARCHAR(50) NOT NULL,
    loaiPhong VARCHAR(50),
    dienTich DOUBLE,
    giaThue DOUBLE NOT NULL,
    trangThai VARCHAR(50) DEFAULT 'trong',    -- 'trong' / 'daThue' / 'baoTri'
    moTa TEXT,
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Bảng: KhachHang (người thuê)
CREATE TABLE KhachHang (
    idKhachHang INT AUTO_INCREMENT PRIMARY KEY,
    tenKhachHang VARCHAR(100) NOT NULL,
    soCccd VARCHAR(20) UNIQUE,
    soDienThoai VARCHAR(20),
    queQuan VARCHAR(100),
    ngheNghiep VARCHAR(100),
    ngaySinh DATE,
    gioiTinh VARCHAR(10),
    ghiChu TEXT,
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Bảng: HopDongThue
CREATE TABLE HopDongThue (
    idHopDong INT AUTO_INCREMENT PRIMARY KEY,
    idPhong INT NOT NULL,
    idKhachHang INT NOT NULL,
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NULL,
    tienCoc DOUBLE DEFAULT 0,
    trangThai VARCHAR(20) DEFAULT 'dangThue', -- 'dangThue' / 'daKetThuc'
    ghiChu TEXT,
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idPhong) REFERENCES Phong(idPhong) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (idKhachHang) REFERENCES KhachHang(idKhachHang) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Bảng: Dien (tiền điện hàng tháng)
CREATE TABLE Dien (
    idDien INT AUTO_INCREMENT PRIMARY KEY,
    idPhong INT NOT NULL,
    thang TINYINT NOT NULL CHECK (thang BETWEEN 1 AND 12),
    nam INT NOT NULL CHECK (nam >= 2000),
    chiSoCu DOUBLE DEFAULT 0,
    chiSoMoi DOUBLE DEFAULT 0,
    donGia DOUBLE DEFAULT 3500, -- mặc định
    thanhTien DOUBLE GENERATED ALWAYS AS ((chiSoMoi - chiSoCu) * donGia) STORED,
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY ukDienPhongThangNam (idPhong, thang, nam),
    FOREIGN KEY (idPhong) REFERENCES Phong(idPhong) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Bảng: Nuoc (tiền nước hàng tháng)
CREATE TABLE Nuoc (
    idNuoc INT AUTO_INCREMENT PRIMARY KEY,
    idPhong INT NOT NULL,
    thang TINYINT NOT NULL CHECK (thang BETWEEN 1 AND 12),
    nam INT NOT NULL CHECK (nam >= 2000),
    soKhoi DOUBLE DEFAULT 0,
    donGia DOUBLE DEFAULT 15000,
    thanhTien DOUBLE GENERATED ALWAYS AS (soKhoi * donGia) STORED,
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY ukNuocPhongThangNam (idPhong, thang, nam),
    FOREIGN KEY (idPhong) REFERENCES Phong(idPhong) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Bảng: DichVu (các dịch vụ thêm)
CREATE TABLE DichVu (
    idDichVu INT AUTO_INCREMENT PRIMARY KEY,
    tenDichVu VARCHAR(100) NOT NULL,
    donGia DOUBLE NOT NULL,
    moTa TEXT,
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Bảng: PhongDichVu (danh sách dịch vụ áp dụng cho phòng)
-- Thêm thang/nam nullable: nếu null => dịch vụ cố định hàng tháng; nếu có thang/nam => áp dụng cho tháng đó
CREATE TABLE PhongDichVu (
    idPhongDichVu INT AUTO_INCREMENT PRIMARY KEY,
    idPhong INT NOT NULL,
    idDichVu INT NOT NULL,
    soLuong INT DEFAULT 1,
    thang TINYINT NULL CHECK (thang BETWEEN 1 AND 12),
    nam INT NULL CHECK (nam >= 2000),
    ghiChu TEXT,
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_phong_dichvu (idPhong, idDichVu, thang, nam),

    FOREIGN KEY (idPhong) REFERENCES Phong(idPhong)
        ON DELETE CASCADE ON UPDATE CASCADE,
        
    FOREIGN KEY (idDichVu) REFERENCES DichVu(idDichVu)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;



-- Bảng: HoaDon (hóa đơn hàng tháng)
CREATE TABLE HoaDon (
    idHoaDon INT AUTO_INCREMENT PRIMARY KEY,
    idPhong INT NOT NULL,
    thang TINYINT NOT NULL CHECK (thang BETWEEN 1 AND 12),
    nam INT NOT NULL CHECK (nam >= 2000),
    tienPhong DOUBLE DEFAULT 0,
    tienDien DOUBLE DEFAULT 0,
    tienNuoc DOUBLE DEFAULT 0,
    tienDichVu DOUBLE DEFAULT 0,
    tienKhac DOUBLE DEFAULT 0,    -- phụ phí khác
    tongTien DOUBLE DEFAULT 0,
    trangThai VARCHAR(20) DEFAULT 'chuaThanhToan',  -- 'chuaThanhToan' / 'daThanhToan'
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY ukHoaDonPhongThangNam (idPhong, thang, nam),
    FOREIGN KEY (idPhong) REFERENCES Phong(idPhong) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Bảng: TaiKhoan (đăng nhập)
CREATE TABLE TaiKhoan (
    idTaiKhoan INT AUTO_INCREMENT PRIMARY KEY,
    tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    matKhau VARCHAR(255) NOT NULL, -- nên hash (BCrypt) trước khi insert
    vaiTro VARCHAR(20) DEFAULT 'admin', -- 'admin' / 'nhanVien'
    hoTen VARCHAR(100),
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Bảng: ThuChi (ghi nhận thu/chi) — đổi ngayNgay -> ngayLap
CREATE TABLE ThuChi (
    idThuChi INT AUTO_INCREMENT PRIMARY KEY,
    ngayLap DATE NOT NULL,
    loai VARCHAR(20) NOT NULL, -- 'THU' / 'CHI'
    soTien DOUBLE NOT NULL,
    nguon VARCHAR(100),
    ghiChu TEXT,
    idPhong INT NULL,
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idPhong) REFERENCES Phong(idPhong) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- 3) INDEXES bổ sung
-- =========================
CREATE INDEX idxPhongTrangThai ON Phong(trangThai);
CREATE INDEX idxHopDongPhong ON HopDongThue(idPhong);
CREATE INDEX idxHopDongKhachHang ON HopDongThue(idKhachHang);
CREATE INDEX idxHoaDonTrangThai ON HoaDon(trangThai);

-- =========================
-- 4) Dữ liệu mẫu
-- =========================
INSERT INTO Phong (tenPhong, loaiPhong, dienTich, giaThue, trangThai)
VALUES
('P101', 'don', 20, 2000000, 'trong'),
('P102', 'doi', 25, 2500000, 'daThue'),
('P201', 'don', 18, 1800000, 'trong');

INSERT INTO KhachHang (tenKhachHang, soCccd, soDienThoai, queQuan, ngheNghiep, ngaySinh, gioiTinh)
VALUES
('Nguyen Van A', '012345678', '0909123456', 'Ha Noi', 'Sinh vien', '2000-01-01', 'Nam'),
('Tran Thi B', '987654321', '0912345678', 'Hai Phong', 'Nhan vien', '1995-05-10', 'Nu');

INSERT INTO HopDongThue (idPhong, idKhachHang, ngayBatDau, ngayKetThuc, tienCoc, trangThai)
VALUES
(2, 1, '2025-10-01', NULL, 1000000, 'dangThue');

INSERT INTO DichVu (tenDichVu, donGia)
VALUES ('wifi', 100000), ('giuXe', 50000), ('rac', 20000);

-- PhongDichVu: wifi cố định cho P101 (thang/nam NULL => cố định); 
INSERT INTO PhongDichVu (idPhong, idDichVu, soLuong, thang, nam)
VALUES (1, 1, 1, NULL, NULL);

-- Dien / Nuoc sample (tháng 10/2025)
INSERT INTO Dien (idPhong, thang, nam, chiSoCu, chiSoMoi, donGia)
VALUES (2, 10, 2025, 1200, 1250, 3500);

INSERT INTO Nuoc (idPhong, thang, nam, soKhoi, donGia)
VALUES (2, 10, 2025, 5, 15000);

-- =========================
-- 5) Stored Procedure: spTaoHoaDonChoPhong (upsert)
--    cập nhật dịch vụ: bao gồm dịch vụ cố định (thang IS NULL) và dịch vụ gắn theo tháng/năm
-- =========================
DELIMITER $$
DROP PROCEDURE IF EXISTS spTaoHoaDonChoPhong $$
CREATE PROCEDURE spTaoHoaDonChoPhong (
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

    -- Tính tiền dịch vụ cho phòng:
    -- lấy cả dịch vụ cố định (thang IS NULL) và dịch vụ gắn cho tháng/năm tương ứng
    SELECT COALESCE(SUM(pdv.soLuong * dv.donGia),0) INTO vTienDichVu
    FROM PhongDichVu pdv
    JOIN DichVu dv ON pdv.idDichVu = dv.idDichVu
    WHERE pdv.idPhong = pIdPhong
      AND (pdv.thang IS NULL OR (pdv.thang = pThang AND pdv.nam = pNam));

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
            tongTien = vTong,
            ngayTao = NOW()
        WHERE idPhong = pIdPhong AND thang = pThang AND nam = pNam;
    ELSE
        INSERT INTO HoaDon (idPhong, thang, nam, tienPhong, tienDien, tienNuoc, tienDichVu, tienKhac, tongTien, ngayTao)
        VALUES (pIdPhong, pThang, pNam, vTienPhong, vTienDien, vTienNuoc, vTienDichVu, vTienKhac, vTong, NOW());
    END IF;
END $$
DELIMITER ;

-- =========================
-- 6) Triggers
--    - Sử dụng BEFORE/AFTER đúng cách để tránh cập nhật chính bảng đang trigger
-- =========================

-- Trigger AFTER INSERT Dien
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterInsertDien $$
CREATE TRIGGER trgAfterInsertDien
AFTER INSERT ON Dien
FOR EACH ROW
BEGIN
    CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
END $$
DELIMITER ;

-- Trigger AFTER UPDATE Dien
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterUpdateDien $$
CREATE TRIGGER trgAfterUpdateDien
AFTER UPDATE ON Dien
FOR EACH ROW
BEGIN
    CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
END $$
DELIMITER ;

-- Trigger AFTER INSERT Nuoc
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterInsertNuoc $$
CREATE TRIGGER trgAfterInsertNuoc
AFTER INSERT ON Nuoc
FOR EACH ROW
BEGIN
    CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
END $$
DELIMITER ;

-- Trigger AFTER UPDATE Nuoc
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterUpdateNuoc $$
CREATE TRIGGER trgAfterUpdateNuoc
AFTER UPDATE ON Nuoc
FOR EACH ROW
BEGIN
    CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
END $$
DELIMITER ;

-- Trigger AFTER INSERT PhongDichVu -> recompute hoa_don for relevant month(s)
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterInsertPhongDichVu $$
CREATE TRIGGER trgAfterInsertPhongDichVu
AFTER INSERT ON PhongDichVu
FOR EACH ROW
BEGIN
    IF NEW.thang IS NULL OR NEW.nam IS NULL THEN
        -- dịch vụ cố định: recalc cho current month/year
        CALL spTaoHoaDonChoPhong(NEW.idPhong, MONTH(CURDATE()), YEAR(CURDATE()));
    ELSE
        CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
    END IF;
END $$
DELIMITER ;

-- Trigger AFTER UPDATE PhongDichVu
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterUpdatePhongDichVu $$
CREATE TRIGGER trgAfterUpdatePhongDichVu
AFTER UPDATE ON PhongDichVu
FOR EACH ROW
BEGIN
    IF NEW.thang IS NULL OR NEW.nam IS NULL THEN
        CALL spTaoHoaDonChoPhong(NEW.idPhong, MONTH(CURDATE()), YEAR(CURDATE()));
    ELSE
        CALL spTaoHoaDonChoPhong(NEW.idPhong, NEW.thang, NEW.nam);
    END IF;
END $$
DELIMITER ;

-- Trigger AFTER DELETE PhongDichVu
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterDeletePhongDichVu $$
CREATE TRIGGER trgAfterDeletePhongDichVu
AFTER DELETE ON PhongDichVu
FOR EACH ROW
BEGIN
    IF OLD.thang IS NULL OR OLD.nam IS NULL THEN
        CALL spTaoHoaDonChoPhong(OLD.idPhong, MONTH(CURDATE()), YEAR(CURDATE()));
    ELSE
        CALL spTaoHoaDonChoPhong(OLD.idPhong, OLD.thang, OLD.nam);
    END IF;
END $$
DELIMITER ;

-- Trigger AFTER INSERT HopDongThue -> cập nhật trạng thái phòng (được phép update bảng khác)
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterInsertHopDong $$
CREATE TRIGGER trgAfterInsertHopDong
AFTER INSERT ON HopDongThue
FOR EACH ROW
BEGIN
    UPDATE Phong SET trangThai = 'daThue' WHERE idPhong = NEW.idPhong;
    CALL spTaoHoaDonChoPhong(NEW.idPhong, MONTH(NEW.ngayBatDau), YEAR(NEW.ngayBatDau));
END $$
DELIMITER ;

-- Trigger BEFORE UPDATE HopDongThue: set NEW.trangThai nếu ngayKetThuc <= today
DELIMITER $$
DROP TRIGGER IF EXISTS trgBeforeUpdateHopDong $$
CREATE TRIGGER trgBeforeUpdateHopDong
BEFORE UPDATE ON HopDongThue
FOR EACH ROW
BEGIN
    IF NEW.ngayKetThuc IS NOT NULL AND NEW.ngayKetThuc <= CURRENT_DATE() THEN
        SET NEW.trangThai = 'daKetThuc';
    END IF;
END $$
DELIMITER ;

-- Trigger AFTER UPDATE HopDongThue: nếu hop dong đã daKetThuc -> cập nhật Phong
DELIMITER $$
DROP TRIGGER IF EXISTS trgAfterUpdateHopDong $$
CREATE TRIGGER trgAfterUpdateHopDong
AFTER UPDATE ON HopDongThue
FOR EACH ROW
BEGIN
    IF NEW.trangThai = 'daKetThuc' THEN
        UPDATE Phong SET trangThai = 'trong' WHERE idPhong = NEW.idPhong;
    END IF;
END $$
DELIMITER ;

-- =========================
-- 7) Event: Hàng ngày kiểm tra hợp đồng hết hạn
-- =========================
-- Nếu event_scheduler ON thì event này sẽ chạy
DELIMITER $$
DROP EVENT IF EXISTS evCheckExpiredContracts $$
CREATE EVENT evCheckExpiredContracts
ON SCHEDULE EVERY 1 DAY
STARTS (CURRENT_TIMESTAMP + INTERVAL 1 HOUR)
DO
BEGIN
    -- Cập nhật hợp đồng đã kết thúc trước ngày hôm nay
    UPDATE HopDongThue h
    JOIN Phong p ON p.idPhong = h.idPhong
    SET h.trangThai = 'daKetThuc', p.trangThai = 'trong'
    WHERE h.ngayKetThuc IS NOT NULL
      AND h.ngayKetThuc < CURRENT_DATE()
      AND h.trangThai <> 'daKetThuc';
END $$
DELIMITER ;

-- =========================
-- 8) FUNCTION HỖ TRỢ
-- =========================
DELIMITER $$
DROP FUNCTION IF EXISTS fnTinhTongHoaDon $$
CREATE FUNCTION fnTinhTongHoaDon (pIdPhong INT, pThang TINYINT, pNam INT)
RETURNS DOUBLE
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
    SELECT COALESCE(SUM(pdv.soLuong * dv.donGia),0) INTO vTienDichVu
    FROM PhongDichVu pdv JOIN DichVu dv ON pdv.idDichVu = dv.idDichVu
    WHERE pdv.idPhong = pIdPhong AND (pdv.thang IS NULL OR (pdv.thang = pThang AND pdv.nam = pNam));

    SET vTong = COALESCE(vTienPhong,0) + COALESCE(vTienDien,0) + COALESCE(vTienNuoc,0) + COALESCE(vTienDichVu,0);
    RETURN vTong;
END $$
DELIMITER ;

-- =========================
-- 9) Hướng dẫn test nhanh (một số query mẫu)
-- =========================
-- INSERT INTO Dien (idPhong, thang, nam, chiSoCu, chiSoMoi, donGia) VALUES (2,10,2025,1200,1250,3500);
-- CALL spTaoHoaDonChoPhong(2,10,2025);
-- SELECT * FROM HoaDon WHERE idPhong = 2 AND thang = 10 AND nam = 2025;
-- SELECT fnTinhTongHoaDon(2,10,2025);

-- =========================
-- 10) Kết thúc file: chèn user mẫu & thu_chi demo
-- =========================
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, hoTen)
VALUES ('admin', 'admin_plaintext_please_change', 'admin', 'Quản trị hệ thống');
TaiKhoan
INSERT INTO ThuChi (ngayLap, loai, soTien, nguon, ghiChu, idPhong)
VALUES (CURDATE(), 'CHI', 500000, 'SuaMayLanh', 'Thay block', 2);

-- End of QuanLyNhaTroFull_v2.sql
