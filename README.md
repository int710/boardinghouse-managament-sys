<p align="center">
  <img src="src/main/resources/images/logo.png" alt="Logo" width="120">
</p>

<h1 align="center">🏠 Hệ Thống Quản Lý Nhà Trọ</h1>
<h3 align="center">Boarding House Management System</h3>

<p align="center">
  <a href="https://www.oracle.com/java/"><img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"></a>
  <a href="https://www.mysql.com/"><img src="https://img.shields.io/badge/MySQL-8.0+-00758F?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"></a>
  <a href="https://firebase.google.com/"><img src="https://img.shields.io/badge/Firebase-Realtime_DB-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase"></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/License-Educational-blue?style=flat-square" alt="License">
  <img src="https://img.shields.io/badge/Version-1.0.0-green?style=flat-square" alt="Version">
  <img src="https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square" alt="Build">
</p>

---

## 📋 Mục Lục

- [Giới thiệu](#-giới-thiệu)
- [Tính năng](#-tính-năng)
- [Kiến trúc hệ thống](#-kiến-trúc-hệ-thống)
- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Cài đặt](#-cài-đặt)
- [Cấu hình](#️-cấu-hình)
- [Hướng dẫn sử dụng](#-hướng-dẫn-sử-dụng)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Công nghệ sử dụng](#️-công-nghệ-sử-dụng)
- [Database Schema](#️-database-schema)
- [API & Services](#-api--services)
- [Troubleshooting](#-troubleshooting)
- [Changelog](#-changelog)
- [Tác giả](#-tác-giả)
- [License](#-license)

---

## 🎯 Giới thiệu

**Hệ thống Quản Lý Nhà Trọ (QLNT)** là ứng dụng desktop được phát triển bằng **Java Swing**, tích hợp **Firebase Realtime Database** cho tính năng chat realtime. Hệ thống hỗ trợ quản lý toàn diện hoạt động kinh doanh nhà trọ:

- 🏠 Quản lý phòng trọ (thêm, sửa, xóa, tìm kiếm)
- 👥 Quản lý khách thuê (thông tin cá nhân, lịch sử thuê)
- 📄 Quản lý hợp đồng (tạo, gia hạn, kết thúc)
- 💰 Quản lý hóa đơn (điện, nước, dịch vụ, xuất PDF)
- 📊 Dashboard thống kê với biểu đồ trực quan
- 💬 Chat realtime giữa Admin và Người thuê
- 📧 Gửi email nhắc thanh toán

---

## ✨ Tính năng

### 👨‍💼 Dành cho Admin

| Module                    | Tính năng                                                                                                |
| ------------------------- | -------------------------------------------------------------------------------------------------------- |
| **📊 Dashboard**          | Thống kê tổng quan, biểu đồ doanh thu 12 tháng, pie chart trạng thái hóa đơn, bar chart trạng thái phòng |
| **🏠 Quản lý Phòng**      | CRUD phòng, quản lý trạng thái (trống/đang thuê/bảo trì), gán khách vào phòng                            |
| **👥 Quản lý Khách hàng** | Quản lý thông tin cá nhân, CCCD, lọc theo giới tính, tìm kiếm                                            |
| **📄 Quản lý Hợp đồng**   | Tạo/gia hạn/kết thúc hợp đồng, tự động cập nhật trạng thái phòng, tạo tài khoản cho khách                |
| **💰 Quản lý Hóa đơn**    | Tạo hóa đơn (điện, nước, dịch vụ), xuất PDF, gửi email nhắc thanh toán                                   |
| **🔧 Quản lý Dịch vụ**    | CRUD dịch vụ (Wifi, giữ xe, vệ sinh...)                                                                  |
| **👤 Quản lý Tài khoản**  | Phân quyền Admin/Nhân viên/Người thuê                                                                    |
| **💬 Chat Realtime**      | Trò chuyện với người thuê, ghim tin nhắn hệ thống (`/pin`, `/unpin`)                                     |
| **📈 Thu Chi**            | Ghi nhận và thống kê thu chi                                                                             |

### 👤 Dành cho Người thuê

| Module                 | Tính năng                                              |
| ---------------------- | ------------------------------------------------------ |
| **📄 Hợp đồng**        | Xem thông tin hợp đồng, ngày còn lại, cảnh báo hết hạn |
| **💰 Hóa đơn**         | Xem danh sách hóa đơn, chi tiết, xuất PDF              |
| **🏠 Thông tin phòng** | Xem thông tin phòng đang thuê                          |
| **💬 Chat**            | Trò chuyện với Admin, nhận thông báo hệ thống          |
| **⚙️ Tài khoản**       | Xem thông tin, đổi mật khẩu                            |

---

## 🏗 Kiến trúc hệ thống

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌────────────┐ │
│  │ AdminFrame  │ │ UserFrame   │ │ LoginFrame  │ │ ChatFrame  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                        CONTROLLER LAYER                          │
│  ┌────────────────┐ ┌────────────────┐ ┌────────────────────┐   │
│  │ AppController  │ │ AuthController │ │ PhongController    │   │
│  └────────────────┘ └────────────────┘ └────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                        SERVICE LAYER                             │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐             │
│  │ PhongService │ │HoaDonService │ │EmailService  │ ...         │
│  └──────────────┘ └──────────────┘ └──────────────┘             │
│  ┌──────────────────────────────────────────────────┐           │
│  │            FirebaseService (Realtime Chat)       │           │
│  └──────────────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                          DAO LAYER                               │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │ PhongDAO │ │HoaDonDAO │ │HopDongDAO│ │TaiKhoanDAO│ ...      │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                        DATA LAYER                                │
│  ┌─────────────────────┐    ┌───────────────────────────┐       │
│  │   MySQL Database    │    │  Firebase Realtime DB     │       │
│  │   (Main Storage)    │    │  (Chat Messages)          │       │
│  └─────────────────────┘    └───────────────────────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 💻 Yêu cầu hệ thống

### Phần mềm

| Yêu cầu   | Phiên bản | Ghi chú                          |
| --------- | --------- | -------------------------------- |
| **JDK**   | 17+       | Khuyến nghị JDK 17 LTS           |
| **MySQL** | 8.0+      | Server + Workbench               |
| **Maven** | 3.8+      | Build tool                       |
| **IDE**   | -         | NetBeans, IntelliJ IDEA, Eclipse |

### Phần cứng tối thiểu

- **RAM**: 4GB
- **Ổ cứng**: 500MB trống
- **Màn hình**: 1280x720

### Hệ điều hành

- Windows 10/11
- macOS 10.14+
- Linux (Ubuntu 18.04+)

---

## 🚀 Cài đặt

## Tải về ứng dụng (Download)

Bạn có thể tải xuống bản cài đặt mới nhất dành cho Windows bên dưới (Có thể lỗi kết nối DB do đang deploy free trên Railway):

| Phiên bản              | Hệ điều hành  | Link tải xuống                                                                                                            |
| :--------------------- | :------------ | :------------------------------------------------------------------------------------------------------------------------ |
| **iNhaTro Pro v1.0.2** | Windows (MSI) | [**Download Now (.msi)**](https://github.com/int710/boardinghouse-managament-sys/releases/tag/v1.0.2) |

_(Lưu ý: Sau khi cài đặt, nếu Windows báo bảo vệ "Windows Protected your PC", hãy chọn **More info** -> **Run anyway**)_

### Bước 1: Clone Repository

```bash
git clone https://github.com/your-username/boardinghouse-management-sys.git
cd boardinghouse-management-sys/QLNT
```

### Bước 2: Cài đặt MySQL & Tạo Database

```sql
-- Tạo database
CREATE DATABASE IF NOT EXISTS QuanLyNhaTro
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Sử dụng database
USE QuanLyNhaTro;

-- Import schema
SOURCE src/main/java/com/ptpmud/quanlynhatro/model/database.sql;

-- (Tùy chọn) Import dữ liệu mẫu
SOURCE sample_data.sql;
```

### Bước 3: Cấu hình kết nối Database

Chỉnh sửa file `src/main/java/com/ptpmud/quanlynhatro/utils/DBConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/QuanLyNhaTro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASS = "your_password";
```

### Bước 4: Cấu hình Firebase (Chat Realtime)

1. Tạo project trên [Firebase Console](https://console.firebase.google.com/)
2. Tải file `firebase-adminsdk.json`
3. Đặt vào thư mục `config/firebase-adminsdk.json`
4. Cập nhật URL trong `FirebaseService.java`:

```java
firebaseService.initialize(
    "config/firebase-adminsdk.json",
    "https://your-project.firebasedatabase.app/"
);
```

### Bước 5: Build & Run

```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn exec:java -Dexec.mainClass="com.ptpmud.quanlynhatro.main"

# Hoặc chạy file JAR
java -jar target/QLNT-fat.jar
```

---

## ⚙️ Cấu hình

### Database Connection

File: `src/main/java/com/ptpmud/quanlynhatro/utils/DBConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/QuanLyNhaTro";
private static final String USER = "root";
private static final String PASS = "password";
```

### Email Service (SMTP)

File: `src/main/java/com/ptpmud/quanlynhatro/service/EmailService.java`

```java
private static final String SMTP_HOST = "smtp.gmail.com";
private static final int SMTP_PORT = 587;
private static final String EMAIL_USER = "your_email@gmail.com";
private static final String EMAIL_PASS = "your_app_password"; // App Password, không phải mật khẩu thường
```

> ⚠️ **Lưu ý**: Với Gmail, cần tạo [App Password](https://support.google.com/accounts/answer/185833)

### Firebase Realtime Database

File: `config/firebase-adminsdk.json` (Service Account Key)

```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "...",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...",
  "client_email": "firebase-adminsdk-xxx@your-project.iam.gserviceaccount.com"
}
```

---

## 📖 Hướng dẫn sử dụng

### Tài khoản mặc định

| Username | Password | Vai trò |
| -------- | -------- | ------- |
| `admin`  | `admin`  | Admin   |

> ⚠️ **Bảo mật**: Đổi mật khẩu ngay sau lần đăng nhập đầu tiên!

### Lệnh Chat Admin

| Lệnh              | Mô tả                  |
| ----------------- | ---------------------- |
| `/pin <nội dung>` | Ghim tin nhắn hệ thống |
| `/unpin`          | Bỏ ghim tin nhắn       |

### Quy trình nghiệp vụ

```
1. Thêm Phòng → 2. Thêm Khách hàng → 3. Tạo Hợp đồng → 4. Tạo Hóa đơn → 5. Thanh toán
```

---

## 📁 Cấu trúc dự án

```
QLNT/
├── 📁 config/
│   └── firebase-adminsdk.json      # Firebase credentials
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/ptpmud/quanlynhatro/
│   │   │   ├── 📁 controller/      # Controllers
│   │   │   │   ├── AppController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   └── PhongController.java
│   │   │   ├── 📁 dao/             # Data Access Objects
│   │   │   │   ├── DichVuDAO.java
│   │   │   │   ├── HoaDonDAO.java
│   │   │   │   ├── HoaDonDichVuDAO.java
│   │   │   │   ├── HopDongDAO.java
│   │   │   │   ├── KhachHangDAO.java
│   │   │   │   ├── PhongDAO.java
│   │   │   │   ├── PhongDichVuDAO.java
│   │   │   │   ├── TaiKhoanDAO.java
│   │   │   │   └── ThuChiDAO.java
│   │   │   ├── 📁 model/           # Entity Models
│   │   │   │   ├── database.sql    # Database schema
│   │   │   │   ├── DichVu.java
│   │   │   │   ├── HoaDon.java
│   │   │   │   ├── HopDong.java
│   │   │   │   ├── KhachHang.java
│   │   │   │   ├── Phong.java
│   │   │   │   └── TaiKhoan.java
│   │   │   ├── 📁 service/         # Business Logic
│   │   │   │   ├── DichVuService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   ├── FirebaseService.java  # Chat Realtime
│   │   │   │   ├── HoaDonService.java
│   │   │   │   ├── HopDongService.java
│   │   │   │   ├── KhachHangService.java
│   │   │   │   ├── PhongService.java
│   │   │   │   ├── TaiKhoanService.java
│   │   │   │   └── ThuChiService.java
│   │   │   ├── 📁 utils/           # Utilities
│   │   │   │   ├── DBConnection.java
│   │   │   │   ├── PdfUtil.java
│   │   │   │   └── Utils.java
│   │   │   ├── 📁 view/            # GUI Frames
│   │   │   │   ├── AdminFrame.java
│   │   │   │   ├── ChatFrame.java      # Chat Realtime UI
│   │   │   │   ├── HoaDonGD.java
│   │   │   │   ├── HopDongGD.java
│   │   │   │   ├── KhachHangFrame.java
│   │   │   │   ├── LoginFrame.java
│   │   │   │   ├── PhongFrame.java
│   │   │   │   ├── TaiKhoanGD.java
│   │   │   │   ├── ThuChiGD.java
│   │   │   │   └── UserFrame.java
│   │   │   └── main.java           # Entry Point
│   │   └── 📁 resources/
│   │       ├── 📁 fonts/           # Custom fonts
│   │       └── 📁 images/          # Icons & Images
│   └── 📁 test/java/               # Unit tests
├── 📁 target/                      # Build output
├── pom.xml                         # Maven configuration
├── README.md                       # Documentation
├── CHAT_GUIDE.md                   # Chat feature guide
├── BUILD_GUIDE.md                  # Build instructions
└── sample_data.sql                 # Sample data
```

---

## 🛠️ Công nghệ sử dụng

### Core Technologies

| Công nghệ              | Phiên bản | Mục đích       |
| ---------------------- | --------- | -------------- |
| **Java**               | 17+       | Ngôn ngữ chính |
| **Java Swing**         | -         | GUI Framework  |
| **MySQL**              | 8.0+      | Database chính |
| **Firebase Admin SDK** | 9.2.0     | Chat Realtime  |

### Dependencies

| Library                | Version | Purpose                   |
| ---------------------- | ------- | ------------------------- |
| `mysql-connector-j`    | 8.3.0   | MySQL JDBC Driver         |
| `spring-security-core` | 6.2.0   | Password hashing (BCrypt) |
| `HikariCP`             | 5.1.0   | Connection Pooling        |
| `openpdf`              | 1.3.32  | PDF Generation            |
| `javax.mail`           | 1.6.2   | Email Service             |
| `firebase-admin`       | 9.2.0   | Firebase Realtime DB      |

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   Phong     │───────│ HopDongThue │───────│ KhachHang   │
└─────────────┘  1:N  └─────────────┘  N:1  └─────────────┘
      │                                            │
      │ 1:N                                        │ 1:1
      ▼                                            ▼
┌─────────────┐                            ┌─────────────┐
│   HoaDon    │                            │  TaiKhoan   │
└─────────────┘                            └─────────────┘
      │
      │ 1:N
      ▼
┌─────────────────┐       ┌─────────────┐
│ HoaDonDichVu    │───────│   DichVu    │
└─────────────────┘  N:1  └─────────────┘
```

### Bảng chính

| Bảng            | Mô tả                          |
| --------------- | ------------------------------ |
| `Phong`         | Thông tin phòng trọ            |
| `KhachHang`     | Thông tin khách thuê           |
| `HopDongThue`   | Hợp đồng thuê                  |
| `HoaDon`        | Hóa đơn hàng tháng             |
| `Dien` / `Nuoc` | Chỉ số điện, nước              |
| `DichVu`        | Danh mục dịch vụ               |
| `HoaDonDichVu`  | Chi tiết dịch vụ trong hóa đơn |
| `TaiKhoan`      | Tài khoản đăng nhập            |
| `ThuChi`        | Ghi nhận thu chi               |

### Stored Procedures & Triggers

- **spTaoHoaDonChoPhong**: Tự động tạo hóa đơn từ chỉ số điện, nước
- **Trigger**: Tự động cập nhật trạng thái phòng khi tạo/kết thúc hợp đồng
- **Event**: Kiểm tra hợp đồng hết hạn hàng ngày

---

## 🔌 API & Services

### FirebaseService

```java
// Singleton instance
FirebaseService.getInstance();

// Gửi tin nhắn
firebaseService.sendMessage(userName, content);

// Lắng nghe tin nhắn realtime
firebaseService.listenForMessages(message -> {
    // Handle new message
});

// Ghim tin nhắn (Admin)
firebaseService.setPinnedMessage(content);
firebaseService.clearPinnedMessage();
```

### EmailService

```java
// Gửi email nhắc thanh toán
EmailService.sendPaymentReminder(toEmail, hoaDon);

// Gửi email với đính kèm PDF
EmailService.sendEmailWithPDF(toEmail, subject, body, pdfFile);
```

---

## 🐛 Troubleshooting

<details>
<summary><b>❌ Lỗi kết nối Database</b></summary>

**Lỗi**: `Connection refused` hoặc `Access denied`

**Giải pháp**:

1. Kiểm tra MySQL Server đã chạy chưa
2. Kiểm tra thông tin kết nối trong `DBConnection.java`
3. Kiểm tra firewall port 3306

</details>

<details>
<summary><b>❌ Lỗi Firebase</b></summary>

**Lỗi**: `Firebase initialization failed`

**Giải pháp**:

1. Kiểm tra file `config/firebase-adminsdk.json` tồn tại
2. Kiểm tra URL Firebase Realtime DB đúng
3. Kiểm tra kết nối internet

</details>

<details>
<summary><b>❌ Lỗi gửi Email</b></summary>

**Lỗi**: `Authentication failed`

**Giải pháp**:

1. Với Gmail, sử dụng App Password thay vì mật khẩu thường
2. Bật "Less secure app access" hoặc sử dụng OAuth2
3. Kiểm tra SMTP port (587 cho TLS)

</details>

<details>
<summary><b>❌ Lỗi biên dịch</b></summary>

**Giải pháp**:

```bash
# Clean và rebuild
mvn clean install -U

# Kiểm tra Java version
java -version
```

</details>

---

## 📝 Changelog

### v1.0.0 (2026-01-11)

#### ✨ Features

- Dashboard với biểu đồ doanh thu, trạng thái
- Quản lý phòng, khách hàng, hợp đồng, hóa đơn
- Xuất PDF hóa đơn
- Gửi email nhắc thanh toán
- **Chat Realtime** với Firebase
- Ghim tin nhắn hệ thống (`/pin`, `/unpin`)
- Giao diện người thuê đầy đủ

#### 🔧 Technical

- Kiến trúc MVC rõ ràng
- Firebase Admin SDK integration
- BCrypt password hashing
- HikariCP connection pooling

---

## 👥 Tác giả

<table>
  <tr>
    <td align="center">
      <b>Bùi Thanh Quân</b><br>
      <sub>CT070242</sub><br>
      <sub>Học viện Kỹ thuật Mật mã (KMA)</sub>
    </td>
  </tr>
</table>

<table>
  <tr>
    <td align="center">
      <b>Vũ Bá Pháo</b><br>
      <b>Hoàng Bảo Phúc</b><br>
      <sub>Cộng sự</sub><br>
      <sub>Học viện Kỹ thuật Mật mã (KMA)</sub>
    </td>
  </tr>
</table>

**Môn học**: Phát triển phần mềm ứng dụng (PTPMUD)

---

## 📄 License

Dự án được phát triển cho mục đích **học tập và nghiên cứu**.

```
Copyright (c) 2026 Bùi Thanh Quân
Học viện Kỹ thuật Mật mã (KMA)
```

---

<p align="center">
  <b>⭐ Nếu dự án hữu ích, hãy cho một star! ⭐</b>
</p>

<p align="center">
  Made with ❤️ by KMA Students
</p>
