package jp.co.sss.spring.dto;

import jp.co.sss.spring.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemView {

	private Integer cartId;
	private Product product;
	private Integer quantity;

	public Integer getSubtotal() {
		return product.getPrice() * quantity;
	}

	public Integer getTaxSubtotal() {
		return product.getTaxPrice() * quantity;
	}
}