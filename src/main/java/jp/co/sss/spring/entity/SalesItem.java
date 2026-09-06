package jp.co.sss.spring.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sales_items")
@Getter
@Setter
public class SalesItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer saleItemId;

	private Integer productId;
	private Integer companyId;
	private String saleName;
	private String description;
	private Integer discountRate;
	private String salesImgPath;
	private LocalDateTime startMonth;
	private LocalDateTime endMonth;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}