package com.pokemon.tcg.service;

import com.pokemon.tcg.dto.OrderRequest;
import com.pokemon.tcg.dto.OutOfStockException;
import com.pokemon.tcg.model.Order;
import com.pokemon.tcg.model.OrderItem;
import com.pokemon.tcg.model.Product;
import com.pokemon.tcg.repository.OrderRepository;
import com.pokemon.tcg.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderCode("DH" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setCustomerPhone(request.getPhone());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        
        BigDecimal finalTotal = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new OutOfStockException("Sản phẩm " + product.getName() + " không đủ số lượng!");
            }

            // Trừ tồn kho
            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            // Tính tiền bằng giá DB
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            finalTotal = finalTotal.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .quantity(itemReq.getQuantity())
                    .priceAtBuy(product.getPrice())
                    .build();
            items.add(orderItem);
        }

        order.setFinalTotal(finalTotal);
        order.setItems(items);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long id, Map<String, String> statusReq) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        String newStatus = statusReq.get("status");

        // Hoàn kho nếu Hủy đơn
        if ("CANCELED".equals(newStatus) && !"CANCELED".equals(order.getStatus())) {
            for (OrderItem item : order.getItems()) {
                productRepository.findById(item.getProductId()).ifPresent(product -> {
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    productRepository.save(product);
                });
            }
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
