package jp.co.sss.spring.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.sss.spring.dto.SaleView;
import jp.co.sss.spring.entity.Category;
import jp.co.sss.spring.entity.Product;
import jp.co.sss.spring.entity.SalesItem;
import jp.co.sss.spring.repository.CategoryRepository;
import jp.co.sss.spring.repository.ProductRepository;
import jp.co.sss.spring.repository.SalesItemRepository;

@Controller
public class TopController {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SalesItemRepository salesItemRepository;

	@GetMapping("/top")
	public String top(Model model) {
		LocalDateTime now = LocalDateTime.now();
		List<SalesItem> salesItems = salesItemRepository
				.findByStartMonthLessThanEqualAndEndMonthGreaterThanEqual(now, now);

		List<SaleView> saleViews = new ArrayList<>();
		for (SalesItem salesItem : salesItems) {
			Product product = productRepository.findById(salesItem.getProductId()).orElse(null);
			if (product == null) {
				continue;
			}
			Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
			if (category == null) {
				continue;
			}
			saleViews.add(new SaleView(category, product, salesItem));
		}

		model.addAttribute("saleViews", saleViews);
		return "top";
	}
}