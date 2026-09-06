package jp.co.sss.spring.dto;

import jp.co.sss.spring.entity.Category;
import jp.co.sss.spring.entity.Product;
import jp.co.sss.spring.entity.SalesItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaleView {
	private Category category;
	private Product product;
	private SalesItem salesItem;

	public Integer getDiscountedPrice() {
		int discount = product.getPrice() * salesItem.getDiscountRate() / 100;
		return product.getPrice() - discount;
	}
}