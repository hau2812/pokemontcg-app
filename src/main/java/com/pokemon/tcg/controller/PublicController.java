package com.pokemon.tcg.controller;

import com.pokemon.tcg.dto.OrderRequest;
import com.pokemon.tcg.model.Order;
import com.pokemon.tcg.model.Product;
import com.pokemon.tcg.model.Subscriber;
import com.pokemon.tcg.service.EmailService;
import com.pokemon.tcg.service.OrderService;
import com.pokemon.tcg.service.ProductService;
import com.pokemon.tcg.service.SubscriberService;
import com.pokemon.tcg.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ProductService productService;
    private final OrderService orderService;
    private final com.pokemon.tcg.service.CategoryService categoryService;
    private final SubscriberService subscriberService;
    private final EmailService emailService;
    private final SystemSettingService systemSettingService;

    @GetMapping("/settings/{key}")
    public ResponseEntity<?> getSetting(@PathVariable String key) {
        String value = systemSettingService.getSettingValue(key, null);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("key", key, "value", value));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<com.pokemon.tcg.model.Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    /**
     * Đăng ký nhận thông báo — tạo subscriber chưa verified, gửi email xác nhận
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không được để trống"));
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không hợp lệ"));
        }

        try {
            Subscriber subscriber = subscriberService.subscribe(email);
            // Gửi email xác nhận
            emailService.sendVerificationEmail(subscriber);
            return ResponseEntity.ok(Map.of(
                    "message", "Vui lòng kiểm tra email để xác nhận đăng ký!",
                    "email", subscriber.getEmail()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Xác nhận email bằng token (user click link trong email)
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        boolean verified = subscriberService.verify(token);
        if (verified) {
            // Trả về HTML page đẹp thay vì plain text
            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head><meta charset="UTF-8"><title>Xác nhận thành công</title></head>
                    <body style="margin:0; padding:0; font-family:'Segoe UI',Arial,sans-serif; background:#0f0f23; display:flex; justify-content:center; align-items:center; min-height:100vh;">
                        <div style="background:#1a1a2e; border-radius:16px; padding:50px; text-align:center; max-width:500px; box-shadow:0 20px 60px rgba(0,0,0,0.5);">
                            <div style="font-size:60px; margin-bottom:20px;">✅</div>
                            <h1 style="color:#10b981; margin:0 0 10px;">Xác nhận thành công!</h1>
                            <p style="color:#94a3b8; font-size:16px; line-height:1.6;">Email của bạn đã được xác nhận. Bạn sẽ nhận được thông báo từ <strong style="color:#e2e8f0;">Pokémon TCG Store</strong> khi có tin mới.</p>
                            <a href="/" style="display:inline-block; margin-top:25px; background:linear-gradient(135deg,#667eea,#764ba2); color:white; padding:12px 30px; border-radius:8px; text-decoration:none; font-weight:bold;">Quay lại cửa hàng</a>
                        </div>
                    </body>
                    </html>
                    """;
            return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8").body(html);
        }
        String htmlFail = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"><title>Lỗi xác nhận</title></head>
                <body style="margin:0; padding:0; font-family:'Segoe UI',Arial,sans-serif; background:#0f0f23; display:flex; justify-content:center; align-items:center; min-height:100vh;">
                    <div style="background:#1a1a2e; border-radius:16px; padding:50px; text-align:center; max-width:500px; box-shadow:0 20px 60px rgba(0,0,0,0.5);">
                        <div style="font-size:60px; margin-bottom:20px;">❌</div>
                        <h1 style="color:#ef4444; margin:0 0 10px;">Link không hợp lệ</h1>
                        <p style="color:#94a3b8; font-size:16px; line-height:1.6;">Link xác nhận này không hợp lệ hoặc đã được sử dụng. Vui lòng đăng ký lại.</p>
                        <a href="/" style="display:inline-block; margin-top:25px; background:linear-gradient(135deg,#667eea,#764ba2); color:white; padding:12px 30px; border-radius:8px; text-decoration:none; font-weight:bold;">Quay lại cửa hàng</a>
                    </div>
                </body>
                </html>
                """;
        return ResponseEntity.badRequest().header("Content-Type", "text/html; charset=UTF-8").body(htmlFail);
    }

    /**
     * Hủy đăng ký (từ link trong email thông báo)
     */
    @GetMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam String email) {
        subscriberService.unsubscribe(email);
        String html = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"><title>Hủy đăng ký</title></head>
                <body style="margin:0; padding:0; font-family:'Segoe UI',Arial,sans-serif; background:#0f0f23; display:flex; justify-content:center; align-items:center; min-height:100vh;">
                    <div style="background:#1a1a2e; border-radius:16px; padding:50px; text-align:center; max-width:500px; box-shadow:0 20px 60px rgba(0,0,0,0.5);">
                        <div style="font-size:60px; margin-bottom:20px;">👋</div>
                        <h1 style="color:#fbbf24; margin:0 0 10px;">Đã hủy đăng ký</h1>
                        <p style="color:#94a3b8; font-size:16px; line-height:1.6;">Bạn đã được gỡ khỏi danh sách nhận thông báo. Chúng tôi rất tiếc khi thấy bạn ra đi!</p>
                        <a href="/" style="display:inline-block; margin-top:25px; background:linear-gradient(135deg,#667eea,#764ba2); color:white; padding:12px 30px; border-radius:8px; text-decoration:none; font-weight:bold;">Quay lại cửa hàng</a>
                    </div>
                </body>
                </html>
                """;
        return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8").body(html);
    }
}
