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
@Table(name = "reviews")
@Getter
@Setter
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reviewId;

	private Integer userId;
	private Integer productId;
	private Integer rating;
	private String comment;
	private String dummyUserName;
	private String contactEmail;
	private String reviewImgPath;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}