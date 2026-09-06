package jp.co.sss.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.spring.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	List<Review> findByProductIdOrderByCreatedAtDesc(Integer productId);
}