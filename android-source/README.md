# Auto Login Garena (Android)

Ứng dụng quản lý danh sách tài khoản cục bộ bằng popup nổi.

1. Chọn tệp TXT, mỗi dòng `tài_khoản|mật_khẩu`.
2. Cấp quyền Trợ năng và hiển thị popup trên ứng dụng khác.
3. Mở Liên Quân thủ công. Dùng `TK trước`, `TK tiếp` hoặc `List acc` để chọn acc.
4. Khi chọn acc, bạn tự mở form Garena, sau đó chọn acc để điền tài khoản/mật khẩu; app chờ 1000 ms rồi chạm nút “Đăng Nhập Ngay” một lần. Nút `×` đóng popup nổi. App không tự mở game, không đăng xuất và không tự thử lại.
5. Nút `−` thu gọn menu thành icon app; chạm icon để mở lại.

Tự động hóa dựa trên Android Accessibility; chỉ hoạt động nếu game cung cấp các nút và ô nhập cho dịch vụ này. Tài khoản/mật khẩu được lưu trong vùng riêng của app.


## Cập nhật online

App kiểm tra GitHub Releases tại https://github.com/hack10293u-blip/autologgrn/releases/latest. Mỗi lần phát hành, tạo GitHub Release với tag phiên bản (ví dụ 1.0.4) và đính kèm APK có đuôi .apk. Trong app nhấn Kiểm tra cập nhật; nếu có bản cao hơn, app mở liên kết tải và Android sẽ yêu cầu người dùng xác nhận cài đặt. Không cài âm thầm.

Phiên bản giao diện xanh hiện tại: 1.0.4. Repo đã có nhưng tại thời điểm kiểm tra chưa có Release công khai; API /releases/latest trả HTTP 404 đến khi phát hành Release đầu tiên.

Các nút popup được đặt theo tọa độ tâm tương đối (x%, y%) bạn cung cấp, trên khung popup co giãn theo màn hình.

Danh sách acc hiển thị STT hai chữ số ở đầu mỗi dòng; chiều cao popup tối đa 330dp.

Menu 1.0.7 sử dụng nhãn/kích thước/vị trí lấy từ JSON do Menu Designer xuất.

Menu 1.0.8 áp dụng bố cục demo cuối (88% rộng, 72% cao); danh sách xổ xuống lấy tài khoản thực từ tệp đã nạp. KILL thoát app, CLOSE thu gọn popup.

Menu 1.0.9 lấy tọa độ mới nhất từ JSON người dùng gửi.

Menu 1.1.0 áp dụng cấu hình JSON mới; 2.0.0 cân bằng kích thước popup theo cạnh màn hình ngắn hơn để tránh bị kéo dãn.

Phiên bản 2.7.0 đưa GUI demo 2.6 vào APK, kèm ba chủ đề Mặc định/Neon/Edras VTK. Các tùy chọn độ trong suốt, kích thước nút nổi, tự mở Liên Quân, tự bấm đăng nhập và thời gian chờ được nối trực tiếp với dịch vụ Android.
