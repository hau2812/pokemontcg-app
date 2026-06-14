package com.pokemon.tcg.controller;

import com.pokemon.tcg.model.Banner;
import com.pokemon.tcg.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BannerController {

    @Autowired
    private BannerRepository bannerRepository;

    @org.springframework.beans.factory.annotation.Value("${app.admin.token}")
    private String adminToken;

    private void checkAuth(String token) {
        if (!adminToken.equals(token)) {
            throw new RuntimeException("Unauthorized");
        }
    }

    // --- PUBLIC ENDPOINTS ---
    
    @GetMapping("/public/banners")
    public ResponseEntity<List<Banner>> getActiveBanners() {
        return ResponseEntity.ok(bannerRepository.findByIsActiveTrueOrderByCreatedAtDesc());
    }

    // --- ADMIN ENDPOINTS ---

    @GetMapping("/admin/banners")
    public ResponseEntity<?> getAllBanners(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            checkAuth(token);
            return ResponseEntity.ok(bannerRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @PostMapping("/admin/banners")
    public ResponseEntity<?> createBanner(@RequestHeader(value = "Authorization", required = false) String token,
                                          @RequestBody Banner banner) {
        try {
            checkAuth(token);
            Banner savedBanner = bannerRepository.save(banner);
            return ResponseEntity.ok(savedBanner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @PutMapping("/admin/banners/{id}")
    public ResponseEntity<?> updateBanner(@RequestHeader(value = "Authorization", required = false) String token,
                                          @PathVariable Long id, 
                                          @RequestBody Banner bannerDetails) {
        try {
            checkAuth(token);
            Optional<Banner> optionalBanner = bannerRepository.findById(id);
            if (optionalBanner.isPresent()) {
                Banner banner = optionalBanner.get();
                banner.setTitle(bannerDetails.getTitle());
                banner.setDescription(bannerDetails.getDescription());
                banner.setImageUrl(bannerDetails.getImageUrl());
                banner.setLinkUrl(bannerDetails.getLinkUrl());
                banner.setIsActive(bannerDetails.getIsActive());
                return ResponseEntity.ok(bannerRepository.save(banner));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @DeleteMapping("/admin/banners/{id}")
    public ResponseEntity<?> deleteBanner(@RequestHeader(value = "Authorization", required = false) String token,
                                          @PathVariable Long id) {
        try {
            checkAuth(token);
            if (bannerRepository.existsById(id)) {
                bannerRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }
}
