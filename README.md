# Auto Login Garena

Phiên bản hiện tại: **2.7.0**.

- APK thử nghiệm: `AutoLoginGarena-v2.7.0-debug.apk`
- Mã nguồn Android: `android-source/`
- GUI tích hợp ba chủ đề: Mặc định, Neon và Edras VTK.
- Tính năng: quản lý tài khoản TXT, popup nổi, chọn tài khoản, Accessibility tự điền, tùy chọn tự mở Liên Quân, tự đăng nhập, delay, độ trong suốt và kích thước nút nổi.

Thông tin tài khoản được lưu cục bộ trong vùng dữ liệu riêng của ứng dụng. Dịch vụ tự động dừng khi phát hiện màn hình CAPTCHA, OTP hoặc xác minh.

> APK trong repo là bản debug để kiểm thử. Android có thể yêu cầu cho phép cài ứng dụng từ nguồn này.

## Phát hành tự động

GitHub Actions tự build, tải APK lên và tạo Release khi repo nhận tag dạng `x.y.z`:

```bash
git push origin main
git tag 2.7.2
git push origin 2.7.2
```
