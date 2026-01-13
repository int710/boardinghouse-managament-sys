# 🏠 Hệ Thống Quản Lý Nhà Trọ (Boarding House Management System)

[![Java](https://img.shields.io/badge/Java-23-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![Swing](https://img.shields.io/badge/Java%20Swing-Desktop-green.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)

Hệ thống quản lý nhà trọ toàn diện được phát triển bằng Java Swing, giúp quản lý phòng trọ, khách thuê, hợp đồng, hóa đơn và các dịch vụ một cách hiệu quả.

## 📋 Mục Lục

- [Giới thiệu](#giới-thiệu)
- [Tính năng](#tính-năng)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt](#cài-đặt)
- [Cấu hình](#cấu-hình)
- [Sử dụng](#sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Database Schema](#database-schema)
- [Tài khoản mặc định](#tài-khoản-mặc-định)
- [Hướng dẫn phát triển](#hướng-dẫn-phát-triển)
- [Tác giả](#tác-giả)
- [License](#license)

## 🎯 Giới thiệu

Hệ thống Quản Lý Nhà Trọ là một ứng dụng desktop được xây dựng bằng Java Swing, hỗ trợ quản lý toàn bộ hoạt động của một nhà trọ bao gồm:

- Quản lý phòng trọ (thêm, sửa, xóa, tìm kiếm)
- Quản lý khách thuê (thông tin cá nhân, hợp đồng)
- Quản lý hợp đồng thuê (tạo, gia hạn, kết thúc)
- Quản lý hóa đơn (tạo, thanh toán, xuất PDF)
- Quản lý dịch vụ (Wifi, giữ xe, vệ sinh, v.v.)
- Dashboard thống kê với biểu đồ trực quan
- Gửi email nhắc thanh toán
- Giao diện người thuê (xem hợp đồng, hóa đơn)

## ✨ Tính năng

### 👨‍💼 Dành cho Quản trị viên

#### 📊 Dashboard
- **Thống kê tổng quan**: Tổng phòng, phòng đang thuê, số khách hàng, doanh thu tháng
- **Biểu đồ doanh thu**: Biểu đồ đường hiển thị doanh thu 12 tháng gần nhất
- **Biểu đồ trạng thái**: Pie chart trạng thái hóa đơn, bar chart trạng thái phòng
- **Thống kê hợp đồng**: Số lượng hợp đồng đang thuê, đã kết thúc

#### 🏠 Quản lý Phòng
- Thêm, sửa, xóa phòng trọ
- Quản lý thông tin: tên phòng, loại phòng, diện tích, giá thuê
- Cập nhật trạng thái: trống, đang thuê, bảo trì
- Tìm kiếm và lọc phòng
- Gán khách thuê vào phòng (tự động tạo hợp đồng)

#### 👥 Quản lý Khách hàng
- Quản lý thông tin khách thuê đầy đủ
- Thông tin cá nhân: Họ tên, CCCD, SĐT, quê quán, nghề nghiệp
- Lọc theo giới tính, tìm kiếm theo tên/CCCD
- Xem lịch sử hợp đồng của khách hàng

#### 📄 Quản lý Hợp đồng
- Tạo hợp đồng thuê với validation chặt chẽ
- Kiểm tra phòng trống, khách hàng hợp lệ
- Gia hạn hợp đồng
- Kết thúc hợp đồng (tự động cập nhật trạng thái phòng)
- Xem lịch sử hợp đồng theo phòng
- Tự động tạo tài khoản đăng nhập cho người thuê

#### 💰 Quản lý Hóa đơn
- Tạo hóa đơn thủ công hoặc tự động
- Quản lý chỉ số điện, nước
- Thêm dịch vụ (Wifi, giữ xe, v.v.)
- Đánh dấu đã thanh toán/chưa thanh toán
- Xuất hóa đơn ra PDF
- Gửi email nhắc thanh toán (có đính kèm PDF)
- Lọc hóa đơn theo tháng, năm, trạng thái

#### 🔧 Quản lý Dịch vụ
- Thêm, sửa, xóa dịch vụ
- Quản lý đơn giá dịch vụ
- Áp dụng dịch vụ vào hóa đơn

#### 👤 Quản lý Tài khoản
- Quản lý tài khoản hệ thống
- Phân quyền: Admin, Nhân viên, Người thuê
- Tạo tài khoản cho người thuê tự động

### 👤 Dành cho Người thuê

#### 📄 Xem Hợp đồng
- Xem thông tin hợp đồng đang thuê
- Ngày bắt đầu, ngày kết thúc
- Số ngày còn lại (cảnh báo khi gần hết hạn)
- Tiền cọc, trạng thái hợp đồng

#### 💰 Xem Hóa đơn
- Danh sách tất cả hóa đơn của phòng
- Lọc theo trạng thái thanh toán
- Xem chi tiết từng hóa đơn
- Xuất PDF hóa đơn

#### 🏠 Thông tin Phòng
- Xem thông tin phòng đang thuê
- Diện tích, giá thuê
- Mô tả phòng

#### ⚙️ Quản lý Tài khoản
- Xem thông tin tài khoản
- Đổi mật khẩu (UI sẵn sàng)

## 💻 Yêu cầu hệ thống

### Phần mềm cần thiết

- **Java Development Kit (JDK)**: Version 23 hoặc cao hơn
- **MySQL**: Version 8.0 hoặc cao hơn
- **Maven**: Version 3.8+ (để build project)
- **IDE**: NetBeans, IntelliJ IDEA, hoặc Eclipse (khuyến nghị)

### Hệ điều hành

- Windows 10/11
- macOS 10.14+
- Linux (Ubuntu 18.04+, CentOS 7+)

### Cấu hình tối thiểu

- RAM: 4GB
- Ổ cứng: 500MB trống
- Màn hình: 1280x720 trở lên

## 🚀 Cài đặt

### Bước 1: Clone hoặc tải dự án

```bash
git clone <repository-url>
cd boardinghouse-managament-sys/QLNT
```

Hoặc tải file ZIP và giải nén.

### Bước 2: Cài đặt MySQL

1. Tải và cài đặt MySQL 8.0 từ [mysql.com](https://www.mysql.com/downloads/)
2. Khởi động MySQL Server
3. Tạo database và user (tùy chọn):

```sql
CREATE DATABASE QuanLyNhaTro;
CREATE USER 'qlnt_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON QuanLyNhaTro.* TO 'qlnt_user'@'localhost';
FLUSH PRIVILEGES;
```

### Bước 3: Tạo Database Schema

1. Mở MySQL Workbench hoặc command line
2. Chạy file SQL để tạo database và các bảng:

```bash
mysql -u root -p < src/main/java/com/ptpmud/quanlynhatro/model/database.sql
```

Hoặc import file `database.sql` vào MySQL Workbench.

### Bước 4: Import dữ liệu mẫu (Tùy chọn)

Để có dữ liệu mẫu phong phú để test:

```bash
mysql -u root -p QuanLyNhaTro < sample_data.sql
```

### Bước 5: Cấu hình kết nối Database

Mở file `src/main/java/com/ptpmud/quanlynhatro/utils/DBConnection.java` và cập nhật thông tin kết nối:

```java
private static final String URL = "jdbc:mysql://localhost:3306/QuanLyNhaTro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";  // Thay đổi nếu cần
private static final String PASS = "your_password";  // Thay đổi mật khẩu
```

### Bước 6: Build project

Sử dụng Maven:

```bash
mvn clean install
```

Hoặc build trong IDE:
- **NetBeans**: Click phải project → Clean and Build
- **IntelliJ IDEA**: Maven → Lifecycle → clean → install
- **Eclipse**: Click phải project → Run As → Maven build → Goals: `clean install`

### Bước 7: Chạy ứng dụng

**Cách 1: Chạy từ IDE**
- Mở file `src/main/java/com/ptpmud/quanlynhatro/main.java`
- Click Run hoặc nhấn F6

**Cách 2: Chạy từ JAR**
```bash
java -jar target/QLNT-1.0-SNAPSHOT.jar
```

**Cách 3: Chạy từ Maven**
```bash
mvn exec:java -Dexec.mainClass="com.ptpmud.quanlynhatro.main"
```

## ⚙️ Cấu hình

### Cấu hình Email (Tùy chọn)

Để sử dụng tính năng gửi email nhắc thanh toán, cấu hình trong file `src/main/java/com/ptpmud/quanlynhatro/service/EmailService.java`:

```java
// Cấu hình SMTP
private static final String SMTP_HOST = "smtp.gmail.com";
private static final int SMTP_PORT = 587;
private static final String EMAIL_USER = "your_email@gmail.com";
private static final String EMAIL_PASS = "your_app_password";
```

**Lưu ý**: Với Gmail, cần sử dụng [App Password](https://support.google.com/accounts/answer/185833) thay vì mật khẩu thông thường.

### Cấu hình Database

File cấu hình: `src/main/java/com/ptpmud/quanlynhatro/utils/DBConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/QuanLyNhaTro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASS = "your_password";
```

## 📖 Sử dụng

### Đăng nhập

1. Khởi động ứng dụng
2. Sử dụng tài khoản mặc định:
   - **Username**: `admin`
   - **Password**: `admin`

### Quản lý Phòng

1. Vào menu **Quản lý Phòng**
2. Click **Thêm** để thêm phòng mới
3. Điền thông tin: Tên phòng, loại, diện tích, giá thuê
4. Chọn trạng thái: trống, đang thuê, bảo trì
5. Click **Lưu**

### Tạo Hợp đồng

1. Vào menu **Hợp đồng**
2. Click **Tạo mới**
3. Chọn phòng (chỉ hiển thị phòng trống)
4. Chọn khách hàng
5. Nhập ngày bắt đầu, ngày kết thúc (tùy chọn)
6. Nhập tiền cọc
7. Tích chọn **Tạo tài khoản đăng nhập** nếu muốn tạo tài khoản cho người thuê
8. Click **OK**

**Lưu ý**: Hệ thống sẽ tự động:
- Kiểm tra phòng có trống không
- Kiểm tra khách hàng đã có hợp đồng đang hoạt động chưa
- Cập nhật trạng thái phòng thành "đang thuê"
- Tạo tài khoản với username: `kh{idKhachHang}` và mật khẩu ngẫu nhiên

### Tạo Hóa đơn

1. Vào menu **Hóa đơn**
2. Click **Tạo mới hóa đơn**
3. Chọn phòng, tháng, năm
4. Nhập chỉ số điện mới (chỉ số cũ tự động lấy từ tháng trước)
5. Nhập chỉ số nước mới
6. Chọn dịch vụ đã sử dụng và nhập số lượng
7. Nhập tiền khác (nếu có)
8. Click **OK**

**Validation tự động**:
- Chỉ số mới phải >= chỉ số cũ
- Đơn giá phải > 0
- Số lượng dịch vụ phải > 0 nếu đã chọn

### Xuất PDF Hóa đơn

1. Chọn hóa đơn trong danh sách
2. Click **Xuất hóa đơn**
3. Chọn nơi lưu file
4. File PDF sẽ được tạo với tên: `HoaDon-{idPhong}-{thang}-{nam}.pdf`

### Gửi Email Nhắc Thanh toán

1. Chọn hóa đơn chưa thanh toán
2. Click **Gửi email nhắc** hoặc **Gửi email + PDF**
3. Nhập email người nhận
4. Click **OK**

### Đăng nhập với tài khoản Người thuê

1. Sử dụng username: `kh{idKhachHang}` (ví dụ: `kh1`, `kh2`)
2. Nhập mật khẩu đã được cấp khi tạo hợp đồng
3. Giao diện người thuê sẽ hiển thị:
   - Tab Hợp đồng: Xem thông tin hợp đồng
   - Tab Hóa đơn: Xem và xuất hóa đơn
   - Tab Thông tin phòng: Xem thông tin phòng
   - Tab Tài khoản: Xem thông tin và đổi mật khẩu

## 📁 Cấu trúc dự án

```
QLNT/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ptpmud/
│   │   │           └── quanlynhatro/
│   │   │               ├── controller/      # Điều khiển luồng xử lý
│   │   │               │   ├── AppController.java
│   │   │               │   ├── AuthController.java
│   │   │               │   └── PhongController.java
│   │   │               ├── dao/             # Data Access Object
│   │   │               │   ├── DichVuDAO.java
│   │   │               │   ├── HoaDonDAO.java
│   │   │               │   ├── HopDongDAO.java
│   │   │               │   ├── KhachHangDAO.java
│   │   │               │   ├── PhongDAO.java
│   │   │               │   └── TaiKhoanDAO.java
│   │   │               ├── model/           # Model/Entity
│   │   │               │   ├── DichVu.java
│   │   │               │   ├── HoaDon.java
│   │   │               │   ├── HopDong.java
│   │   │               │   ├── KhachHang.java
│   │   │               │   ├── Phong.java
│   │   │               │   ├── TaiKhoan.java
│   │   │               │   └── database.sql
│   │   │               ├── service/         # Business Logic
│   │   │               │   ├── DichVuService.java
│   │   │               │   ├── EmailService.java
│   │   │               │   ├── HoaDonService.java
│   │   │               │   ├── HopDongService.java
│   │   │               │   ├── KhachHangService.java
│   │   │               │   ├── PhongService.java
│   │   │               │   └── TaiKhoanService.java
│   │   │               ├── utils/           # Tiện ích
│   │   │               │   ├── DBConnection.java
│   │   │               │   ├── PdfUtil.java
│   │   │               │   └── Utils.java
│   │   │               ├── view/            # Giao diện
│   │   │               │   ├── AdminFrame.java
│   │   │               │   ├── HoaDonGD.java
│   │   │               │   ├── HopDongGD.java
│   │   │               │   ├── KhachHangFrame.java
│   │   │               │   ├── LoginFrame.java
│   │   │               │   ├── PhongFrame.java
│   │   │               │   ├── UserHoaDonFrame.java
│   │   │               │   └── ...
│   │   │               └── main.java        # Entry point
│   │   └── resources/
│   │       ├── fonts/                      # Font chữ
│   │       └── images/                     # Hình ảnh icon
│   └── test/
│       └── java/
├── target/                                 # Thư mục build output
├── pom.xml                                 # Maven configuration
├── README.md                               # File này
└── sample_data.sql                         # Dữ liệu mẫu
```

## 🛠️ Công nghệ sử dụng

### Backend
- **Java 23**: Ngôn ngữ lập trình chính
- **Java Swing**: Framework GUI
- **MySQL 8.0**: Hệ quản trị cơ sở dữ liệu
- **JDBC**: Kết nối database

### Libraries & Dependencies
- **MySQL Connector/J 8.3.0**: Driver kết nối MySQL
- **Spring Security Core 6.2.0**: Bảo mật, hash mật khẩu (BCrypt)
- **OpenPDF 1.3.32**: Tạo file PDF hóa đơn
- **JavaMail 1.6.2**: Gửi email

### Tools
- **Maven**: Quản lý dependencies và build
- **NetBeans/IntelliJ IDEA**: IDE phát triển

## 🗄️ Database Schema

### Các bảng chính

1. **Phong**: Thông tin phòng trọ
   - idPhong, tenPhong, loaiPhong, dienTich, giaThue, trangThai, moTa

2. **KhachHang**: Thông tin khách thuê
   - idKhachHang, tenKhachHang, soCccd, soDienThoai, queQuan, ngheNghiep, ngaySinh, gioiTinh

3. **HopDongThue**: Hợp đồng thuê
   - idHopDong, idPhong, idKhachHang, ngayBatDau, ngayKetThuc, tienCoc, trangThai

4. **HoaDon**: Hóa đơn hàng tháng
   - idHoaDon, idPhong, thang, nam, tienPhong, tienDien, tienNuoc, tienDichVu, tienKhac, tongTien, trangThai

5. **Dien**: Chỉ số điện
   - idDien, idPhong, thang, nam, chiSoCu, chiSoMoi, donGia, thanhTien

6. **Nuoc**: Chỉ số nước
   - idNuoc, idPhong, thang, nam, soKhoi, donGia, thanhTien

7. **DichVu**: Danh mục dịch vụ
   - idDichVu, tenDichVu, donGia, moTa

8. **HoaDonDichVu**: Chi tiết dịch vụ trong hóa đơn
   - idHoaDonDichVu, idHoaDon, idDichVu, soLuong, donGia, thanhTien

9. **TaiKhoan**: Tài khoản đăng nhập
   - idTaiKhoan, tenDangNhap, matKhau, vaiTro, hoTen

10. **ThuChi**: Ghi nhận thu chi
    - idThuChi, ngayLap, loai, soTien, nguon, ghiChu, idPhong

### Stored Procedures

- **spTaoHoaDonChoPhong**: Tự động tạo/cập nhật hóa đơn từ chỉ số điện, nước

### Triggers

- Tự động cập nhật trạng thái phòng khi tạo/kết thúc hợp đồng
- Tự động tạo hóa đơn khi thêm/chỉnh sửa chỉ số điện, nước
- Tự động cập nhật tổng tiền hóa đơn khi thêm/sửa/xóa dịch vụ

### Events

- **evCheckExpiredContracts**: Tự động kiểm tra và cập nhật hợp đồng hết hạn hàng ngày

## 🔐 Tài khoản mặc định

Sau khi chạy script `database.sql`, tài khoản mặc định:

- **Username**: `admin`
- **Password**: `admin`
- **Vai trò**: Admin

**⚠️ Lưu ý bảo mật**: Đổi mật khẩu ngay sau lần đăng nhập đầu tiên!

## 👨‍💻 Hướng dẫn phát triển

### Thêm tính năng mới

1. **Tạo Model** (nếu cần): Thêm class trong `model/`
2. **Tạo DAO**: Thêm class trong `dao/` để truy cập database
3. **Tạo Service**: Thêm class trong `service/` để xử lý business logic
4. **Tạo View**: Thêm JFrame/JPanel trong `view/` để hiển thị giao diện
5. **Tạo Controller** (nếu cần): Thêm class trong `controller/` để điều phối

### Coding Standards

- **Naming Convention**: 
  - Class: PascalCase (ví dụ: `HoaDonService`)
  - Method/Variable: camelCase (ví dụ: `getHoaDon()`)
  - Constant: UPPER_SNAKE_CASE (ví dụ: `MAX_RETRY`)
- **Package**: `com.ptpmud.quanlynhatro.{module}`
- **Comments**: Javadoc cho public methods

### Testing

1. Test từng module riêng biệt
2. Test tích hợp các module
3. Test với dữ liệu mẫu từ `sample_data.sql`

## 🐛 Troubleshooting

### Lỗi kết nối Database

**Lỗi**: `Connection refused` hoặc `Access denied`

**Giải pháp**:
1. Kiểm tra MySQL Server đã khởi động chưa
2. Kiểm tra thông tin kết nối trong `DBConnection.java`
3. Kiểm tra user có quyền truy cập database
4. Kiểm tra firewall có chặn port 3306 không

### Lỗi không tìm thấy Driver

**Lỗi**: `ClassNotFoundException: com.mysql.cj.jdbc.Driver`

**Giải pháp**:
1. Kiểm tra dependency MySQL Connector trong `pom.xml`
2. Chạy `mvn clean install` để tải lại dependencies
3. Kiểm tra classpath trong IDE

### Lỗi biên dịch

**Lỗi**: Compilation errors

**Giải pháp**:
1. Kiểm tra JDK version (cần Java 23)
2. Clean và rebuild project
3. Kiểm tra Maven dependencies đã tải đầy đủ chưa

### Lỗi gửi Email

**Lỗi**: Email không gửi được

**Giải pháp**:
1. Kiểm tra cấu hình SMTP trong `EmailService.java`
2. Với Gmail, sử dụng App Password thay vì mật khẩu thông thường
3. Kiểm tra kết nối internet
4. Kiểm tra firewall có chặn port SMTP không

## 📝 Changelog

### Version 1.0 (2025-12-13)

**Tính năng mới**:
- ✅ Dashboard với biểu đồ doanh thu, trạng thái hóa đơn, trạng thái phòng
- ✅ Validation chặt chẽ cho tạo hợp đồng và hóa đơn
- ✅ Tự động tạo tài khoản cho người thuê
- ✅ Giao diện người thuê đầy đủ (xem hợp đồng, hóa đơn, thông tin phòng)
- ✅ Xuất PDF hóa đơn
- ✅ Gửi email nhắc thanh toán

**Cải thiện**:
- 🎨 Giao diện hiện đại, dễ sử dụng
- 🔒 Validation dữ liệu chặt chẽ
- 📊 Biểu đồ trực quan với dữ liệu thực
- 🚀 Hiệu năng tốt hơn

## 👥 Tác giả

**Bùi Thanh Quân** - int710 - CT070242

- Trường: Học viện Kỹ thuật Mật mã (KMA)
- Môn học: Phát triển phần mềm ứng dụng (PTPMUD)

## 📄 License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

## 🙏 Lời cảm ơn

- Giảng viên hướng dẫn
- Cộng đồng Java và MySQL
- Các thư viện mã nguồn mở đã sử dụng

## 📞 Liên hệ

Nếu có câu hỏi hoặc góp ý, vui lòng tạo issue trên repository hoặc liên hệ trực tiếp.

---

**Chúc bạn sử dụng hệ thống hiệu quả! 🎉**

