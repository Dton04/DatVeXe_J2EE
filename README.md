# Bus Ticket Booking System (Hệ Thống Đặt Vé Xe Khách)

Một ứng dụng web hiện đại cho phép người dùng tìm kiếm, đặt vé xe khách và thanh toán trực tuyến. Hệ thống bao gồm trang quản trị (Admin Dashboard) để quản lý lịch trình, tuyến đường, xe khách và nhân viên.

## 🚀 Tính Năng Chính (Core Features)

### 👤 Cho Người Dùng (Customer Features)
- **Tìm kiếm chuyến xe**: Tìm theo điểm đi, điểm đến và ngày khởi hành.
- **Chọn chỗ ngồi**: Xem sơ đồ xe và chọn vị trí ghế trống.
- **Đặt vé trực tuyến**: Lưu thông tin hành khách và trạng thái vé.
- **Thanh toán VNPAY**: Tích hợp cổng thanh toán điện tử an toàn.
- **Quản lý vé**: Xem lịch sử đặt vé và trạng thái thanh toán.

### 🛡️ Cho Quản Trị Viên (Admin Features)
- **Quản lý Tuyến đường (Routes)**: Thêm/Sửa/Xóa các tuyến đường và bến xe (Stations).
- **Quản lý Xe (Buses)**: Quản lý thông tin xe, biển số và loại xe.
- **Quản lý Chuyến xe (Trips)**: Thiết lập lịch trình khởi hành, giá vé.
- **Quản lý Người dùng**: Phân quyền (Admin/Staff/Customer) và quản lý tài khoản.
- **Thống kê & Báo cáo**: Theo dõi doanh thu và lượng vé bán ra.

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

### Backend (`/j2ee16`)
- **Ngôn ngữ**: Java 17
- **Framework**: Spring Boot 3.5.x
- **Bảo mật**: Spring Security & JWT (JSON Web Token)
- **Cơ sở dữ liệu**: PostgreSQL (Cloud-based on Neon)
- **ORM**: Spring Data JPA / Hibernate
- **Migration**: Flyway (Optional)
- **API Documentation**: OpenAPI / Swagger UI
- **Thanh toán**: VNPAY Gateway integration

### Frontend (`/fe_react/my-app`)
- **Framework**: Next.js 16.2.0 (React 19)
- **Styling**: Tailwind CSS 4
- **State Management**: Zustand
- **Form Handling**: React Hook Form + Zod validation
- **Icons**: Lucide React
- **HTTP Client**: Axios

---

## 🏃 Thử Nghiệm Tại Local (Getting Started)

### 1. Phía Backend
1. Đảm bảo bạn đã cài đặt **Java 17** và **Maven**.
2. Kiểm tra cấu hình tại `j2ee16/src/main/resources/application.properties`:
   - Kiểm tra chuỗi kết nối PostgreSQL.
   - Cấu hình VNPAY (nếu cần test thanh toán).
3. Chạy ứng dụng:
   ```bash
   cd j2ee16
   ./mvnw spring-boot:run
   ```
4. Truy cập tài liệu API (Swagger): [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 2. Phía Frontend
1. Cài đặt **Node.js** (v18+).
2. Di chuyển vào thư mục frontend và cài đặt dependencies:
   ```bash
   cd fe_react/my-app
   npm install
   ```
3. Chạy chế độ development:
   ```bash
   npm run dev
   ```
4. Truy cập ứng dụng tại: [http://localhost:3000](http://localhost:3000)

---

## 📁 Cấu Trúc Thư Mục (Project Structure)

```text
├── j2ee16/             # Spring Boot Backend
│   ├── src/main/java   # Mã nguồn Java (Controller, Service, Repository, Entity, DTO)
│   ├── src/main/resources # Cấu hình ứng dụng
│   └── pom.xml         # Quản lý dependencies Maven
├── fe_react/           # Root Frontend
│   └── my-app/         # Next.js Application
├── sql/                # Tập tin script SQL (database.sql)
└── README.md           # Hướng dẫn này
```

## 🔒 Bảo Mật (Security)
Dự án sử dụng cơ chế xác thực JWT. Các API yêu cầu quyền Admin hoặc Staff sẽ được bảo vệ bởi Filter Middleware trong Spring Security.

---
*Phát triển bởi [Tên Của Bạn / Group]*
