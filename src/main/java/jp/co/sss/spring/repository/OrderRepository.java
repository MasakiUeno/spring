package jp.co.sss.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.spring.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}