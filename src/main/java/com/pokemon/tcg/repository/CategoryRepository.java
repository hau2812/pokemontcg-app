package com.pokemon.tcg.repository;

import com.pokemon.tcg.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
