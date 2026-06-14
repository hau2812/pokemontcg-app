package com.pokemon.tcg.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String description;
    
    private BigDecimal price;
    
    private Integer stockQuantity;
    
    private String imageUrl;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String detailedDescription;

    @jakarta.persistence.ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    @jakarta.persistence.CollectionTable(name = "product_images", joinColumns = @jakarta.persistence.JoinColumn(name = "product_id"))
    @jakarta.persistence.Column(name = "image_url")
    @Builder.Default
    private java.util.List<String> additionalImages = new java.util.ArrayList<>();

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "category_id")
    private Category category;
    
    @Builder.Default
    private Boolean isDeleted = false;

    @Builder.Default
    private Boolean isFeatured = false;

    @Builder.Default
    private Boolean isBestSeller = false;

    @Builder.Default
    private Boolean isComingSoon = false;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (isDeleted == null) {
            isDeleted = false;
        }
        if (isFeatured == null) {
            isFeatured = false;
        }
        if (isBestSeller == null) {
            isBestSeller = false;
        }
        if (isComingSoon == null) {
            isComingSoon = false;
        }
    }
}
