import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class FixHtml {
    public static void main(String[] args) throws Exception {
        String file = "C:\\Users\\haduc\\.gemini\\antigravity\\scratch\\pokemon-tcg-app\\src\\main\\resources\\static\\index.html";
        String content = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
        
        Map<String, String> map = new LinkedHashMap<>();
        map.put("S\uFFFDn ph\uFFFDm da h\uFFFDt h\uFFFDng.", "Sản phẩm đã hết hàng.");
        map.put("L\uFFFDi khi d\uFFFDt h\uFFFDng.", "Lỗi khi đặt hàng.");
        map.put("T\uFFFDt c\uFFFD", "Tất cả");
        map.put("Danh m\uFFFDc", "Danh mục");
        map.put("Dang nh\uFFFDp", "Đăng nhập");
        map.put("Qu\uFFFDn tr\uFFFD", "Quản trị");
        map.put("m\uFFFDt kh\uFFFDu", "mật khẩu");
        map.put("ti kho\uFFFDn", "tài khoản");
        map.put("C\uFFFD l\uFFFDi x\uFFFDy ra", "Có lỗi xảy ra");
        map.put("Kh\uFFFDi ph\uFFFDc m\uFFFDt kh\uFFFDu", "Khôi phục mật khẩu");
        map.put("Nh\uFFFDp ti kho\uFFFDn qu\uFFFDn tr\uFFFD d\uFFFD nh\uFFFDn m\uFFFD x\uFFFDc nh\uFFFDn qua email.", "Nhập tài khoản quản trị để nhận mã xác nhận qua email.");
        map.put("T\uFFFDo m\uFFFDt kh\uFFFDu m\uFFFDi", "Tạo mật khẩu mới");
        map.put("Ki\uFFFDm tra h\uFFFDp th\uFFFDu d\uFFFDn c\uFFFDa b\uFFFDn d\uFFFD l\uFFFDy m\uFFFD OTP.", "Kiểm tra hộp thư đến của bạn để lấy mã OTP.");
        map.put("Danh s\uFFFDch S\uFFFDn ph\uFFFDm", "Danh sách Sản phẩm");
        map.put("Danh s\uFFFDch Danh m\uFFFDc", "Danh sách Danh mục");
        map.put("G\uFFFDi th\uFFFDng b\uFFFDo d\uFFFDn t\uFFFDt c\uFFFD Subscriber", "Gửi thông báo đến tất cả Subscriber");
        map.put("Ti\uFFFDu d\uFFFD email", "Tiêu đề email");
        map.put("N\uFFFDi dung email", "Nội dung email");
        map.put("G\uFFFDi email", "Gửi email");
        map.put("Qu\uFFFDn l\uFFFD Banner Qu\uFFFDng C\uFFFDo", "Quản lý Banner Quảng Cáo");
        map.put("T\uFFFDn s\uFFFDn ph\uFFFDm", "Tên sản phẩm");
        map.put("H\uFFFDnh \uFFFDnh ch\uFFFDnh s\uFFFDn ph\uFFFDm", "Hình ảnh chính sản phẩm");
        map.put("Ho\uFFFDc nh\uFFFDp link \uFFFDnh tr\uFFFDc ti\uFFFDp...", "Hoặc nhập link ảnh trực tiếp...");
        map.put("Th\uFFFDm s\uFFFDn ph\uFFFDm", "Thêm sản phẩm");
        map.put("C\uFFFDp nh\uFFFDt", "Cập nhật");
        map.put("H\uFFFDy", "Hủy");
        map.put("Tr\uFFFDng th\uFFFDi", "Trạng thái");
        map.put("Ng\uFFFDy d\uFFFDng k\uFFFD", "Ngày đăng ký");
        map.put("H\uFFFDnh d\uFFFDng", "Hành động");
        map.put("D\uFFFD x\uFFFDc nh\uFFFDn", "Đã xác nhận");
        map.put("Ch\uFFFD x\uFFFDc nh\uFFFDn", "Chờ xác nhận");
        map.put("Ch\uFFFDa c\uFFFD ai d\uFFFDng k\uFFFD", "Chưa có ai đăng ký");
        map.put("\uFFFDnh", "Ảnh");
        map.put("Ti\uFFFDu d\uFFFD", "Tiêu đề");
        map.put("Link d\uFFFDch", "Link đích");
        map.put("Dang hi\uFFFDn th\uFFFD", "Đang hiển thị");
        map.put("D\uFFFD \uFFFDn", "Đã ẩn");
        map.put("Ch\uFFFDa c\uFFFD banner n\uFFFDo", "Chưa có banner nào");
        map.put("C\uFFFDu h\uFFFDnh H\uFFFD th\uFFFDng (Ch\uFFFDnh s\uFFFDch D\uFFFDnh gi\uFFFD)", "Cấu hình Hệ thống (Chính sách Đánh giá)");
        map.put("L\uFFFDu thay d\uFFFDi", "Lưu thay đổi");
        map.put("Qu\uFFFDn tr\uFFFD vi\uFFFDn", "Quản trị viên");
        map.put("Dang xu\uFFFDt", "Đăng xuất");
        map.put("T\uFFFDm ki\uFFFDm", "Tìm kiếm");
        map.put("Th\uFFFDm danh m\uFFFDc", "Thêm danh mục");
        map.put("Ch\uFFFDnh s\uFFFDch b\uFFFDo m\uFFFDt", "Chính sách bảo mật");
        map.put("Hu\uFFFDng d\uFFFDn mua h\uFFFDng", "Hướng dẫn mua hàng");
        map.put("Ch\uFFFDnh s\uFFFDch giao h\uFFFDng", "Chính sách giao hàng");
        map.put("Cam k\uFFFDt c\uFFFDa PikaRyu TCG", "Cam kết của PikaRyu TCG");
        map.put("C\uFFFDu h\uFFFDi th\uFFFDrng g\uFFFDp (FAQ)", "Câu hỏi thường gặp (FAQ)");
        map.put("D\uFFFDch v\uFFFD Card Grading", "Dịch vụ Card Grading");
        map.put("Danh m\uFFFDc n\uFFFDi b\uFFFDt", "Danh mục nổi bật");
        map.put("S\uFFFDn ph\uFFFDm n\uFFFDi b\uFFFDt", "Sản phẩm nổi bật");
        map.put("S\uFFFDn ph\uFFFDm b\uFFFDn ch\uFFFDy", "Sản phẩm bán chạy");
        map.put("S\uFFFDn ph\uFFFDm s\uFFFDp ra m\uFFFDt", "Sản phẩm sắp ra mắt");
        map.put("Th\uFFFDng tin li\uFFFDn h\uFFFD", "Thông tin liên hệ");
        map.put("Di\uFFFDu kho\uFFFDn s\uFFFD d\uFFFDng", "Điều khoản sử dụng");
        map.put("D\uFFFDng", "Đóng");
        map.put("V\uFFFD c\uFFFDa h\uFFFDng", "Về cửa hàng");
        map.put("Th\uFFFDm v\uFFFDo gi\uFFFD h\uFFFDng", "Thêm vào giỏ hàng");
        map.put("Xem gi\uFFFD h\uFFFDng", "Xem giỏ hàng");
        map.put("M\uFFFDt kh\uFFFDu", "Mật khẩu");
        map.put("T\uFFFDi kho\uFFFDn", "Tài khoản");
        map.put("Qu\uFFFDn", "Quên");
        map.put("M\uFFFD x\uFFFDc nh\uFFFDn", "Mã xác nhận");
        map.put("X\uFFFDc nh\uFFFDn", "Xác nhận");
        map.put("D\uFFFDnh gi\uFFFD", "Đánh giá");
        map.put("Th\uFFFDnh c\uFFFDng", "Thành công");
        map.put("Giao h\uFFFDng", "Giao hàng");
        map.put("D\uFFFDt h\uFFFDng", "Đặt hàng");
        map.put("Thanh to\uFFFDn", "Thanh toán");
        map.put("gi\uFFFD", "giá");
        map.put("th\uFFFDm", "thêm");
        map.put("t\uFFFDt c\uFFFD", "tất cả");
        map.put("H\uFFFDnh", "Hình");
        map.put("d\uFFFDch", "đích");
        map.put("h\uFFFDng", "hàng");
        map.put("m\uFFFD", "mã");
        map.put("T\uFFFD", "Từ");
        map.put("D\uFFFDn", "Đến");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }

        Files.write(Paths.get(file), content.getBytes(StandardCharsets.UTF_8));
        System.out.println("Fixed");
    }
}
