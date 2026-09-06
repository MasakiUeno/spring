package jp.co.sss.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.spring.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}