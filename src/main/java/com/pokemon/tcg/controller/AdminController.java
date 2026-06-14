package com.pokemon.tcg.controller;

import com.pokemon.tcg.dto.LoginRequest;
import com.pokemon.tcg.dto.LoginResponse;
import com.pokemon.tcg.model.Order;
import com.pokemon.tcg.model.Product;
import com.pokemon.tcg.model.Subscriber;
import com.pokemon.tcg.service.EmailService;
import com.pokemon.tcg.service.OrderService;
import com.pokemon.tcg.service.ProductService;
import com.pokemon.tcg.service.SubscriberService;
import com.pokemon.tcg.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final com.pokemon.tcg.service.FileStorageService fileStorageService;
    private final com.pokemon.tcg.service.CategoryService categoryService;
    private final SubscriberService subscriberService;
    private final EmailService emailService;
    private final SystemSettingService systemSettingService;

    @Value("${app.admin.token}")
    private String adminToken;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    // A simple check for MVP to avoid full Spring Security setup
    private boolean isAuthorized(String token) {
        return adminToken.equals(token);
    }

    private void checkAuth(String token) {
        if (!isAuthorized(token)) {
            throw new RuntimeException("Unauthorized");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String currentUsername = systemSettingService.getSettingValue("admin_username", adminUsername);
        String currentPassword = systemSettingService.getSettingValue("admin_password", adminPassword);

        if (currentUsername.equals(loginRequest.getUsername()) && currentPassword.equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(new LoginResponse(adminToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestHeader(value = "Authorization", required = false) String token, @org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        checkAuth(token);
        String fileUrl = fileStorageService.storeFile(file);
        return ResponseEntity.ok(Map.of("imageUrl", fileUrl));
    }

    @PutMapping("/settings/{key}")
    public ResponseEntity<?> updateSetting(@RequestHeader(value = "Authorization", required = false) String token,
                                           @PathVariable String key,
                                           @RequestBody Map<String, String> payload) {
        checkAuth(token);
        String value = payload.get("value");
        if (value == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Value cannot be null"));
        }
        systemSettingService.saveSetting(key, value);
        return ResponseEntity.ok(Map.of("message", "Setting updated successfully"));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(@RequestHeader(value = "Authorization", required = false) String token) {
        checkAuth(token);
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Product product) {
        checkAuth(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id, @RequestBody Product product) {
        checkAuth(token);
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id) {
        checkAuth(token);
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(@RequestHeader(value = "Authorization", required = false) String token) {
        checkAuth(token);
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id, @RequestBody Map<String, String> statusReq) {
        checkAuth(token);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, statusReq));
    }

    // Category Endpoints
    @GetMapping("/categories")
    public ResponseEntity<List<com.pokemon.tcg.model.Category>> getAllCategoriesAdmin(@RequestHeader(value = "Authorization", required = false) String token) {
        checkAuth(token);
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<com.pokemon.tcg.model.Category> createCategory(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody com.pokemon.tcg.model.Category category) {
        checkAuth(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<com.pokemon.tcg.model.Category> updateCategory(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id, @RequestBody com.pokemon.tcg.model.Category category) {
        checkAuth(token);
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id) {
        checkAuth(token);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // SUBSCRIBER & NOTIFICATION ENDPOINTS
    // ==========================================

    @GetMapping("/subscribers")
    public ResponseEntity<List<Subscriber>> getAllSubscribers(@RequestHeader(value = "Authorization", required = false) String token) {
        checkAuth(token);
        return ResponseEntity.ok(subscriberService.getAllSubscribers());
    }

    @DeleteMapping("/subscribers/{id}")
    public ResponseEntity<Void> deleteSubscriber(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id) {
        checkAuth(token);
        subscriberService.deleteSubscriber(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notifications/send")
    public ResponseEntity<?> sendNotification(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Map<String, String> body) {
        checkAuth(token);
        String subject = body.get("subject");
        String content = body.get("content");

        if (subject == null || subject.isBlank() || content == null || content.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tiêu đề và nội dung không được để trống"));
        }

        int sentCount = emailService.sendNotificationToAll(subject, content);
        return ResponseEntity.ok(Map.of(
                "message", "Đã gửi thông báo thành công!",
                "sentCount", sentCount
        ));
    }

    private static final Map<String, String> otpStorage = new java.util.concurrent.ConcurrentHashMap<>();

    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String currentUsername = systemSettingService.getSettingValue("admin_username", adminUsername);
        if (!currentUsername.equals(username)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tên đăng nhập không tồn tại."));
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        otpStorage.put(username, otp);

        String emailContent = "<p>Xin chào quản trị viên,</p>" +
                              "<p>Mã OTP để khôi phục mật khẩu của bạn là: <strong style='font-size: 24px; color: #3b82f6;'>" + otp + "</strong></p>" +
                              "<p>Mã này sẽ hết hạn sau khi sử dụng.</p>";

        // Gửi email đến email cấu hình trong application.properties
        try {
            emailService.sendHtmlEmail("pikaryutcg@gmail.com", "Mã khôi phục mật khẩu PikaRyu TCG", emailContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Không thể gửi email OTP."));
        }

        return ResponseEntity.ok(Map.of("message", "Mã xác nhận đã được gửi đến email quản trị."));
    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");

        if (otp == null || !otp.equals(otpStorage.get(username))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã OTP không hợp lệ."));
        }

        systemSettingService.saveSetting("admin_password", newPassword);
        otpStorage.remove(username);

        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
    }
}
