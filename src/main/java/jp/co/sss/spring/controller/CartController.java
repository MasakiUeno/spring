package jp.co.sss.spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.spring.dto.CartItemView;
import jp.co.sss.spring.entity.Cart;
import jp.co.sss.spring.entity.Product;
import jp.co.sss.spring.repository.CartRepository;
import jp.co.sss.spring.repository.ProductRepository;

@Controller
public class CartController {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	@PostMapping("/cart/add/{id}")
	public String cartAdd(@PathVariable("id") Integer id,
			@RequestParam(defaultValue = "1") Integer quantity,
			HttpSession session, Model model) {
		Integer userId = (Integer) session.getAttribute("loginUserId");
		if (userId == null) {
			return "redirect:/login";
		}

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("商品が見つかりません：id=" + id));

		Optional<Cart> existing = cartRepository.findByUserIdAndProductId(userId, id);

		Cart cart;
		if (existing.isPresent()) {
			cart = existing.get();
			cart.setQuantity(cart.getQuantity() + quantity);
		} else {
			cart = new Cart();
			cart.setUserId(userId);
			cart.setProductId(id);
			cart.setQuantity(quantity);
		}
		cartRepository.save(cart);

		List<Cart> carts = cartRepository.findByUserId(userId);

		model.addAttribute("product", product);
		model.addAttribute("quantity", cart.getQuantity());
		model.addAttribute("cartTotal", calcCartTotal(carts));
		return "cart/add";
	}

	private int calcCartTotal(List<Cart> carts) {
		int total = 0;
		for (Cart c : carts) {
			Product p = productRepository.findById(c.getProductId()).orElse(null);
			if (p != null) {
				total += p.getTaxPrice() * c.getQuantity();
			}
		}
		return total;
	}

	@GetMapping("/cart")
	public String cartList(HttpSession session, Model model) {
		Integer userId = (Integer) session.getAttribute("loginUserId");
		if (userId == null) {
			return "redirect:/login";
		}

		List<Cart> carts = cartRepository.findByUserId(userId);

		List<CartItemView> items = new ArrayList<>();
		int totalCount = 0;
		int subtotal = 0;
		int taxTotal = 0;
		for (Cart cart : carts) {
			Product product = productRepository.findById(cart.getProductId()).orElse(null);
			if (product == null) {
				continue;
			}
			CartItemView item = new CartItemView(cart.getCartId(), product, cart.getQuantity());
			items.add(item);
			totalCount += cart.getQuantity();
			subtotal += product.getPrice() * cart.getQuantity();
			taxTotal += product.getTaxPrice() * cart.getQuantity();
		}

		model.addAttribute("items", items);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("subtotal", subtotal);
		model.addAttribute("taxTotal", taxTotal);
		return "cart/list";
	}

	@PostMapping("/cart/update/{cartId}")
	public String cartUpdate(@PathVariable("cartId") Integer cartId,
			@RequestParam("quantity") Integer quantity, HttpSession session) {
		Integer userId = (Integer) session.getAttribute("loginUserId");
		if (userId == null) {
			return "redirect:/login";
		}

		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new RuntimeException("カート情報が見つかりません：id=" + cartId));

		if (!cart.getUserId().equals(userId)) {
			throw new RuntimeException("不正なアクセスです");
		}

		cart.setQuantity(quantity);
		cartRepository.save(cart);
		return "redirect:/cart";
	}

	@PostMapping("/cart/delete/{cartId}")
	public String cartDelete(@PathVariable("cartId") Integer cartId, HttpSession session) {
		Integer userId = (Integer) session.getAttribute("loginUserId");
		if (userId == null) {
			return "redirect:/login";
		}

		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new RuntimeException("カート情報が見つかりません：id=" + cartId));

		if (!cart.getUserId().equals(userId)) {
			throw new RuntimeException("不正なアクセスです");
		}

		cartRepository.deleteById(cartId);
		return "redirect:/cart";
	}
}