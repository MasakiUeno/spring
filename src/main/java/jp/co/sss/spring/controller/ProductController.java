package jp.co.sss.spring.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.spring.entity.Company;
import jp.co.sss.spring.entity.Product;
import jp.co.sss.spring.entity.Review;
import jp.co.sss.spring.repository.CompanyRepository;
import jp.co.sss.spring.repository.ProductRepository;
import jp.co.sss.spring.repository.ReviewRepository;

@Controller
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@GetMapping("/products")
	public String productList(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer categoryId,
			Model model) {

		List<Product> products = productRepository.findAll();

		if (keyword != null && !keyword.isBlank()) {
			products = products.stream()
					.filter(p -> p.getProductName().contains(keyword))
					.collect(Collectors.toList());
		}
		if (categoryId != null) {
			products = products.stream()
					.filter(p -> p.getCategoryId().equals(categoryId))
					.collect(Collectors.toList());
		}

		Map<Integer, String> companyNames = new HashMap<>();
		for (Product product : products) {
			companyRepository.findById(product.getCompanyId())
					.ifPresent(company -> companyNames.put(product.getProductId(), company.getCompanyName()));
		}

		model.addAttribute("products", products);
		model.addAttribute("companyNames", companyNames);
		return "product/list";
	}

	@GetMapping("/products/{id}")
	public String productDetail(@PathVariable("id") Integer id, Model model) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("商品が見つかりません：id=" + id));

		String companyName = companyRepository.findById(product.getCompanyId())
				.map(Company::getCompanyName)
				.orElse("不明なメーカー");

		List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(id);

		model.addAttribute("product", product);
		model.addAttribute("companyName", companyName);
		model.addAttribute("reviews", reviews);
		return "product/detail";
	}
}