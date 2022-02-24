package thuonghth.carts;

import java.math.BigDecimal;
import java.util.HashMap;

import thuonghth.discounts.DiscountDTO;
import thuonghth.utils.input_validators.WrapperInputObject;

public class CartDTO {
	private HashMap<Integer, CartRecord> cart;
	private WrapperInputObject<DiscountDTO> discount;
	private BigDecimal total;
	private String status;

	public static final String INITED_STATUS = "initiated";
	public static final String ORDERING_STATUS = "ordering";
	public static final String CONFIRMED_STATUS = "confirmed";

	public CartDTO() {
		this.cart = new HashMap<>();
		this.total = BigDecimal.ZERO;
		this.status = INITED_STATUS;
	}

	public HashMap<Integer, CartRecord> getCart() {
		return cart;
	}

	public void setCart(HashMap<Integer, CartRecord> cart) {
		this.cart = cart;
	}

	public WrapperInputObject<DiscountDTO> getDiscount() {
		return discount;
	}

	public void setDiscount(WrapperInputObject<DiscountDTO> discount) {
		this.discount = discount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
