package jp.co.sss.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.spring.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}