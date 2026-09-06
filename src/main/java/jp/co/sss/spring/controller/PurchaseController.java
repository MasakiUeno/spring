package jp.co.sss.spring.controller;

import java.util.ArrayList;
import java.util.List;

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
import jp.co.sss.spring.entity.Order;
import jp.co.sss.spring.entity.OrderItem;
import jp.co.sss.spring.entity.Product;
import jp.co.sss.spring.repository.CartRepository;
import jp.co.sss.spring.repository.OrderItemRepository;
import jp.co.sss.spring.repository.OrderRepository;
import jp.co.sss.spring.repository.ProductRepository;

@Controller
public class PurchaseController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@GetMapping("/purchase/{id}")
	public String purchaseSingle(@PathVariable("id") Integer id, HttpSession session, Model model) {
		if (session.getAttribute("loginUserId") == null) {
			return "redirect:/login";
		}

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("商品が見つかりません：id=" + id));

		List<CartItemView> items = new ArrayList<>();
		items.add(new CartItemView(null, product, 1));

		session.setAttribute("pendingOrderItems", items);
		session.setAttribute("purchaseFromCart", false);

		model.addAttribute("items", items);
		model.addAttribute("total", calcTotal(items));
		return "purchase/detail";
	}

	@GetMapping("/purchase")
	public String purchaseFromCart(HttpSession session, Model model) {
		Integer userId = (Integer) session.getAttribute("loginUserId");
		if (userId == null) {
			return "redirect:/login";
		}

		List<Cart> carts = cartRepository.findByUserId(userId);
		if (carts.isEmpty()) {
			return "redirect:/cart";
		}

		List<CartItemView> items = new ArrayList<>();
		for (Cart cart : carts) {
			productRepository.findById(cart.getProductId())
					.ifPresent(product -> items.add(new CartItemView(cart.getCartId(), product, cart.getQuantity())));
		}

		session.setAttribute("pendingOrderItems", items);
		session.setAttribute("purchaseFromCart", true);

		model.addAttribute("items", items);
		model.addAttribute("total", calcTotal(items));
		return "purchase/detail";
	}

	@PostMapping("/purchase/confirm")
	@SuppressWarnings("unchecked")
	public String purchaseConfirm(
			@RequestParam String deliveryChoice,
			@RequestParam(required = false) String name1,
			@RequestParam(required = false) String address1,
			@RequestParam(required = false) String apartment1,
			@RequestParam(required = false) String name2,
			@RequestParam(required = false) String address2,
			@RequestParam(required = false) String apartment2,
			@RequestParam String paymentChoice,
			@RequestParam(required = false) String cardNumber1,
			@RequestParam(required = false) String cardExpiry1,
			@RequestParam(required = false) String cardNumber2,
			@RequestParam(required = false) String cardExpiry2,
			HttpSession session, Model model) {

		if (session.getAttribute("loginUserId") == null) {
			return "redirect:/login";
		}

		List<CartItemView> items = (List<CartItemView>) session.getAttribute("pendingOrderItems");
		if (items == null || items.isEmpty()) {
			return "redirect:/cart";
		}

		String name = "1".equals(deliveryChoice) ? name1 : name2;
		String address = "1".equals(deliveryChoice) ? address1 : address2;
		String apartment = "1".equals(deliveryChoice) ? apartment1 : apartment2;

		String cardNumber = "1".equals(paymentChoice) ? cardNumber1 : cardNumber2;
		String cardExpiry = "1".equals(paymentChoice) ? cardExpiry1 : cardExpiry2;

		session.setAttribute("purchaseName", name);
		session.setAttribute("purchaseAddress", address);
		session.setAttribute("purchaseApartment", apartment);
		session.setAttribute("purchaseCardNumber", cardNumber);
		session.setAttribute("purchaseCardExpiry", cardExpiry);

		model.addAttribute("items", items);
		model.addAttribute("name", name);
		model.addAttribute("address", address);
		model.addAttribute("apartment", apartment);
		model.addAttribute("cardNumber", cardNumber);
		model.addAttribute("cardExpiry", cardExpiry);
		model.addAttribute("total", calcTotal(items));
		return "purchase/confirm";
	}

	@PostMapping("/purchase/complete")
	@SuppressWarnings("unchecked")
	public String purchaseComplete(HttpSession session) {
		Integer userId = (Integer) session.getAttribute("loginUserId");
		if (userId == null) {
			return "redirect:/login";
		}

		List<CartItemView> items = (List<CartItemView>) session.getAttribute("pendingOrderItems");
		if (items == null || items.isEmpty()) {
			return "redirect:/cart";
		}

		Order order = new Order();
		order.setUserId(userId);
		order.setTotalAmount(calcTotal(items));
		order.setStatus("注文完了");
		order = orderRepository.save(order);

		for (CartItemView item : items) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(order.getOrderId());
			orderItem.setProductId(item.getProduct().getProductId());
			orderItem.setQuantity(item.getQuantity());
			orderItem.setPrice(item.getProduct().getPrice());
			orderItemRepository.save(orderItem);
		}

		Boolean fromCart = (Boolean) session.getAttribute("purchaseFromCart");
		if (Boolean.TRUE.equals(fromCart)) {
			cartRepository.deleteAll(cartRepository.findByUserId(userId));
		}

		session.setAttribute("completedItems", items);
		session.setAttribute("completedAddress", session.getAttribute("purchaseAddress"));

		session.removeAttribute("pendingOrderItems");
		session.removeAttribute("purchaseFromCart");
		session.removeAttribute("purchaseName");
		session.removeAttribute("purchaseAddress");
		session.removeAttribute("purchaseApartment");
		session.removeAttribute("purchaseCardNumber");
		session.removeAttribute("purchaseCardExpiry");

		return "redirect:/complete";
	}
	
	@GetMapping("/complete")
	@SuppressWarnings("unchecked")
	public String complete(HttpSession session, Model model) {
		List<CartItemView> items = (List<CartItemView>) session.getAttribute("completedItems");
		String address = (String) session.getAttribute("completedAddress");

		model.addAttribute("items", items);
		model.addAttribute("address", address);
		return "complete";
	}

	private int calcTotal(List<CartItemView> items) {
		int total = 0;
		for (CartItemView item : items) {
			total += item.getSubtotal();
		}
		return total;
	}
}