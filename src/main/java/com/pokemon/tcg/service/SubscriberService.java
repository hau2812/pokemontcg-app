package com.pokemon.tcg.service;

import com.pokemon.tcg.model.Subscriber;
import com.pokemon.tcg.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    /**
     * Đăng ký email mới. Nếu email đã tồn tại và đã verified → báo lỗi.
     * Nếu email đã tồn tại nhưng chưa verified → tạo token mới để gửi lại.
     */
    public Subscriber subscribe(String email) {
        Optional<Subscriber> existing = subscriberRepository.findByEmail(email.toLowerCase().trim());

        if (existing.isPresent()) {
            Subscriber sub = existing.get();
            if (sub.isVerified()) {
                throw new RuntimeException("Email này đã được đăng ký và xác nhận rồi!");
            }
            // Chưa verified → tạo token mới để gửi lại email xác nhận
            sub.setVerificationToken(UUID.randomUUID().toString());
            return subscriberRepository.save(sub);
        }

        Subscriber subscriber = Subscriber.builder()
                .email(email.toLowerCase().trim())
                .verified(false)
                .verificationToken(UUID.randomUUID().toString())
                .subscribedAt(LocalDateTime.now())
                .build();

        return subscriberRepository.save(subscriber);
    }

    /**
     * Xác nhận email bằng token
     */
    public boolean verify(String token) {
        Optional<Subscriber> subscriber = subscriberRepository.findByVerificationToken(token);
        if (subscriber.isPresent()) {
            Subscriber sub = subscriber.get();
            sub.setVerified(true);
            sub.setVerificationToken(null); // Xóa token sau khi xác nhận
            subscriberRepository.save(sub);
            return true;
        }
        return false;
    }

    /**
     * Hủy đăng ký bằng email
     */
    public void unsubscribe(String email) {
        subscriberRepository.findByEmail(email.toLowerCase().trim())
                .ifPresent(subscriberRepository::delete);
    }

    /**
     * Lấy tất cả subscriber (cho admin)
     */
    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }

    /**
     * Lấy chỉ subscriber đã verified (để gửi thông báo)
     */
    public List<Subscriber> getVerifiedSubscribers() {
        return subscriberRepository.findAllByVerifiedTrue();
    }

    /**
     * Xóa subscriber theo ID (admin)
     */
    public void deleteSubscriber(Long id) {
        subscriberRepository.deleteById(id);
    }
}
