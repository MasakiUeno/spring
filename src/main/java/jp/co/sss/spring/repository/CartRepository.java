package jp.co.sss.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.spring.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {

	List<Cart> findByUserId(Integer userId);

	Optional<Cart> findByUserIdAndProductId(Integer userId, Integer productId);
}