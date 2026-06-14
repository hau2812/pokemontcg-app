package com.pokemon.tcg.repository;

import com.pokemon.tcg.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    boolean existsByEmail(String email);
    Optional<Subscriber> findByEmail(String email);
    Optional<Subscriber> findByVerificationToken(String verificationToken);
    List<Subscriber> findAllByVerifiedTrue();
}
