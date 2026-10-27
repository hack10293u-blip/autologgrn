# Auto Login Garena

Ứng dụng Android hỗ trợ quản lý tài khoản và thao tác đăng nhập Garena/Liên Quân thuận tiện hơn thông qua popup nổi.

[![Phiên bản](https://img.shields.io/github/v/release/hack10293u-blip/autologgrn?display_name=tag&label=phi%C3%AAn%20b%E1%BA%A3n&color=7c5cff)](https://github.com/hack10293u-blip/autologgrn/releases/latest)
[![Tải APK](https://img.shields.io/badge/T%E1%BA%A3i%20APK-Release%20m%E1%BB%9Bi%20nh%E1%BA%A5t-22a06b)](https://github.com/hack10293u-blip/autologgrn/releases/latest)
[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://github.com/hack10293u-blip/autologgrn/releases/latest)

## Tính năng chính

- Nhập và quản lý danh sách tài khoản từ tệp TXT.
- Chọn nhanh tài khoản bằng popup nổi trên màn hình.
- Hỗ trợ tự điền mật khẩu và nhấn đăng nhập qua Dịch vụ trợ năng.
- Tùy chọn tự mở Liên Quân khi bật popup.
- Tùy chỉnh thời gian chờ, độ trong suốt và kích thước nút nổi.
- Nhiều giao diện: **Mặc định**, **Neon** và **Edras VTK**.
- Tự dừng thao tác khi phát hiện CAPTCHA, OTP hoặc bước xác minh.

## Tải và cài đặt

1. Mở trang [Releases](https://github.com/hack10293u-blip/autologgrn/releases/latest).
2. Tải tệp APK trong mục **Assets**.
3. Cho phép cài đặt ứng dụng từ nguồn này nếu Android yêu cầu.
4. Mở ứng dụng và cấp quyền **Hiển thị trên ứng dụng khác** cùng **Dịch vụ trợ năng** để sử dụng đầy đủ tính năng.

> [!IMPORTANT]
> APK hiện tại là bản thử nghiệm. Chỉ tải ứng dụng từ trang Releases chính thức của repository này.

## Định dạng tệp tài khoản

Mỗi tài khoản nằm trên một dòng:

```text
tai_khoan|mat_khau|ghi_chu
```

Thông tin tài khoản được lưu cục bộ trong vùng dữ liệu riêng của ứng dụng và không được tải lên máy chủ.

## Mã nguồn

Mã nguồn Android nằm trong thư mục [`android-source`](./android-source). Dự án yêu cầu Android 8.0 (API 26) trở lên.

## Phát hành tự động

GitHub Actions sẽ tự build APK và tạo Release khi nhận tag phiên bản theo định dạng `x.y.z`:

```bash
git push origin main
git tag 2.9.2
git push origin 2.9.2
```

Tiêu đề Release được đặt tự động theo đúng số phiên bản của tag.
