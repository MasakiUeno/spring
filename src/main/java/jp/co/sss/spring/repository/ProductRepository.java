package jp.co.sss.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.spring.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Optional<Product> findFirstByCategoryId(Integer categoryId);
}