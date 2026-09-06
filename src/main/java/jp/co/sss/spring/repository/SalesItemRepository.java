package jp.co.sss.spring.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.spring.entity.SalesItem;

public interface SalesItemRepository extends JpaRepository<SalesItem, Integer> {

	List<SalesItem> findByStartMonthLessThanEqualAndEndMonthGreaterThanEqual(LocalDateTime start, LocalDateTime end);
}