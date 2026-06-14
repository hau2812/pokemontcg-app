package com.pokemon.tcg.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String phone;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}
