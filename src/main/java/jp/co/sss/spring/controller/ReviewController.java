package jp.co.sss.spring.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.sss.spring.entity.Product;
import jp.co.sss.spring.entity.Review;
import jp.co.sss.spring.repository.ProductRepository;
import jp.co.sss.spring.repository.ReviewRepository;

@Controller
public class ReviewController {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/reviews/new/{id}")
	public String reviewNew(@PathVariable("id") Integer id, HttpSession session, Model model) {
		if (session.getAttribute("loginUserId") == null) {
			return "redirect:/login";
		}

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("商品が見つかりません：id=" + id));

		model.addAttribute("product", product);
		return "review/new";
	}

	@PostMapping("/reviews")
	public String reviewCreate(
			@RequestParam Integer productId,
			@RequestParam Integer rating,
			@RequestParam(required = false) String contactEmail,
			@RequestParam String comment,
			@RequestParam(required = false) MultipartFile reviewImage,
			@RequestParam String dummyUserName,
			HttpSession session) throws IOException {

		Integer loginUserId = (Integer) session.getAttribute("loginUserId");
		if (loginUserId == null) {
			return "redirect:/login";
		}

		String imgPath = "";
		if (reviewImage != null && !reviewImage.isEmpty()) {
			String fileName = UUID.randomUUID() + "_" + reviewImage.getOriginalFilename();
			Path uploadDir = Paths.get("uploads/reviews");
			Files.createDirectories(uploadDir);
			Path savePath = uploadDir.resolve(fileName);
			reviewImage.transferTo(savePath);
			imgPath = "/uploads/reviews/" + fileName;
		}

		Review review = new Review();
		review.setUserId(loginUserId);
		review.setProductId(productId);
		review.setRating(rating);
		review.setComment(comment);
		review.setDummyUserName(dummyUserName);
		review.setContactEmail(contactEmail);
		review.setReviewImgPath(imgPath);
		reviewRepository.save(review);

		return "redirect:/products";
	}
}