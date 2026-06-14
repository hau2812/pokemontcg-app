package com.pokemon.tcg.service;

import com.pokemon.tcg.model.Subscriber;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SubscriberService subscriberService;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Gửi email xác nhận đăng ký
     */
    public void sendVerificationEmail(Subscriber subscriber) {
        String verifyUrl = baseUrl + "/api/public/verify?token=" + subscriber.getVerificationToken();
        
        if (fromEmail == null || fromEmail.isBlank() || "your-email@gmail.com".equals(fromEmail)) {
            log.warn("\n=========================================================\n" +
                     "⚠ GIẢ LẬP GỬI EMAIL (Vì chưa cấu hình SMTP thật)\n" +
                     "   Bấm vào link này để giả lập người dùng xác nhận email:\n" +
                     "   " + verifyUrl + "\n" +
                     "=========================================================\n");
        }

        String subject = "Xác nhận đăng ký nhận thông báo — Pokémon TCG Store";
        String htmlContent = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #1a1a2e; color: #e2e8f0; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #667eea, #764ba2); padding: 30px; text-align: center;">
                        <h1 style="margin: 0; font-size: 24px; color: white;">⚡ Pokémon TCG Store</h1>
                        <p style="margin: 8px 0 0; color: rgba(255,255,255,0.85); font-size: 14px;">Xác nhận đăng ký nhận thông báo</p>
                    </div>
                    <div style="padding: 30px;">
                        <p style="font-size: 16px; line-height: 1.6;">Xin chào,</p>
                        <p style="font-size: 16px; line-height: 1.6;">Cảm ơn bạn đã đăng ký nhận thông báo từ <strong>Pokémon TCG Store</strong>! Vui lòng nhấn nút bên dưới để xác nhận email của bạn:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 14px 36px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block;">
                                ✅ Xác nhận Email
                            </a>
                        </div>
                        <p style="font-size: 13px; color: #94a3b8; line-height: 1.6;">Nếu bạn không đăng ký, vui lòng bỏ qua email này. Link xác nhận sẽ tự hết hạn.</p>
                    </div>
                    <div style="background: rgba(0,0,0,0.3); padding: 15px; text-align: center; font-size: 12px; color: #64748b;">
                        © 2026 Pokémon TCG Store. All rights reserved.
                    </div>
                </div>
                """.formatted(verifyUrl);

        sendHtmlEmail(subscriber.getEmail(), subject, htmlContent);
    }

    /**
     * Gửi thông báo đến tất cả subscriber đã xác nhận
     */
    public int sendNotificationToAll(String subject, String content) {
        List<Subscriber> verifiedSubscribers = subscriberService.getVerifiedSubscribers();

        if (verifiedSubscribers.isEmpty()) {
            log.warn("Không có subscriber nào đã xác nhận để gửi thông báo.");
            return 0;
        }

        int successCount = 0;
        for (Subscriber subscriber : verifiedSubscribers) {
            String unsubscribeUrl = baseUrl + "/api/public/unsubscribe?email=" + subscriber.getEmail();
            String htmlContent = """
                    <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #1a1a2e; color: #e2e8f0; border-radius: 12px; overflow: hidden;">
                        <div style="background: linear-gradient(135deg, #667eea, #764ba2); padding: 30px; text-align: center;">
                            <h1 style="margin: 0; font-size: 24px; color: white;">⚡ Pokémon TCG Store</h1>
                            <p style="margin: 8px 0 0; color: rgba(255,255,255,0.85); font-size: 14px;">%s</p>
                        </div>
                        <div style="padding: 30px;">
                            <div style="font-size: 15px; line-height: 1.8; white-space: pre-wrap;">%s</div>
                        </div>
                        <div style="background: rgba(0,0,0,0.3); padding: 15px; text-align: center; font-size: 12px; color: #64748b;">
                            <p>Bạn nhận được email này vì đã đăng ký nhận thông báo từ Pokémon TCG Store.</p>
                            <a href="%s" style="color: #94a3b8; text-decoration: underline;">Hủy đăng ký</a>
                        </div>
                    </div>
                    """.formatted(subject, content, unsubscribeUrl);

            try {
                sendHtmlEmail(subscriber.getEmail(), subject, htmlContent);
                successCount++;
            } catch (Exception e) {
                log.error("Lỗi gửi email đến {}: {}", subscriber.getEmail(), e.getMessage());
            }
        }

        log.info("Đã gửi thông báo thành công đến {}/{} subscriber.", successCount, verifiedSubscribers.size());
        return successCount;
    }

    /**
     * Gửi email HTML
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (fromEmail == null || fromEmail.isBlank() || "your-email@gmail.com".equals(fromEmail)) {
            log.warn("⚠ SMTP chưa được cấu hình thật (đang dùng placeholder). Log email thay vì gửi thật:");
            log.info("TO: {} | SUBJECT: {} | CONTENT: {}", to, subject, "(HTML email content omitted for brevity)");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("✉ Email đã gửi thành công đến: {}", to);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email đến {}: {}", to, e.getMessage());
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }
}
