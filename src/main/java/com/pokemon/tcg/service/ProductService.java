package com.pokemon.tcg.service;

import com.pokemon.tcg.model.Product;
import com.pokemon.tcg.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsDeletedFalse();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setImageUrl(updatedProduct.getImageUrl());
        existing.setDetailedDescription(updatedProduct.getDetailedDescription());
        if (updatedProduct.getAdditionalImages() != null) {
            existing.setAdditionalImages(new java.util.ArrayList<>(updatedProduct.getAdditionalImages()));
        } else {
            existing.setAdditionalImages(new java.util.ArrayList<>());
        }
        existing.setCategory(updatedProduct.getCategory());
        
        if (updatedProduct.getIsDeleted() != null) {
            existing.setIsDeleted(updatedProduct.getIsDeleted());
        }
        if (updatedProduct.getIsFeatured() != null) {
            existing.setIsFeatured(updatedProduct.getIsFeatured());
        }
        if (updatedProduct.getIsBestSeller() != null) {
            existing.setIsBestSeller(updatedProduct.getIsBestSeller());
        }
        if (updatedProduct.getIsComingSoon() != null) {
            existing.setIsComingSoon(updatedProduct.getIsComingSoon());
        }
        
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product existing = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setIsDeleted(true);
        productRepository.save(existing);
    }
}
