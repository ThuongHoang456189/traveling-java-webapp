package thuonghth.carts;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.TreeMap;

import javax.naming.NamingException;

import thuonghth.discounts.DiscountDAO;
import thuonghth.discounts.DiscountDTO;
import thuonghth.tours.TourDAO;
import thuonghth.tours.TourDTO;
import thuonghth.utils.input_validators.WrapperInputObject;

public class CartDAO {
	private CartDTO cart;

	public CartDAO(CartDTO cart) {
		this.cart = cart;
	}

	public CartDTO getCart() {
		return cart;
	}

	public void setCart(CartDTO cart) {
		this.cart = cart;
	}

	private BigDecimal getSubtotal(Integer amount, BigDecimal price) {
		return price.multiply(new BigDecimal(amount));
	}

	public CartRecord initCartRecord(TourDTO tour) {
		CartRecord cartRecord = new CartRecord();
		WrapperInputObject<TourDTO> tourWrapper = cartRecord.getTour();
		tourWrapper.setInputObject(tour);
		cartRecord.setAmount(1);
		cartRecord.setSubTotal(getSubtotal(cartRecord.getAmount(), tour.getPrice()));
		return cartRecord;
	}

	public void updateCartRecord(CartRecord cartRecord) {
		WrapperInputObject<TourDTO> tourWrapper = cartRecord.getTour();
		cartRecord.setAmount(cartRecord.getAmount() + 1);
		cartRecord.setSubTotal(getSubtotal(cartRecord.getAmount(), tourWrapper.getInputObject().getPrice()));
	}

	public void updateCart(TourDTO tour) {
		HashMap<Integer, CartRecord> cartList = cart.getCart();

		CartRecord cartRecord = cartList.get(tour.getTourID());

		if (cartRecord == null) {
			cartRecord = initCartRecord(tour);
		} else {
			updateCartRecord(cartRecord);
		}
		cartList.put(tour.getTourID(), cartRecord);
		updateCartTotal();
	}

	public BigDecimal getDiscountPercent() {
		BigDecimal discountPercent = BigDecimal.ZERO;
		WrapperInputObject<DiscountDTO> discountWrapper = cart.getDiscount();
		if (discountWrapper != null) {
			DiscountDTO discount = discountWrapper.getInputObject();
			if (discount != null) {
				discountPercent = discount.getDiscountPercent();
				if (discountPercent == null) {
					discountPercent = BigDecimal.ZERO;
				}
			}
		}
		return discountPercent;
	}

	public void updateCartTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (CartRecord record : cart.getCart().values()) {
			total = total.add(record.getSubTotal());
		}
		total = total.subtract(getDiscountPercent().multiply(total).divide(new BigDecimal(100)));
		cart.setTotal(total);
	}

	public boolean validateDiscount(String discountCode) throws NamingException, SQLException {
		boolean isValid = true;
		discountCode = discountCode.trim();
		DiscountDTO discount = new DiscountDTO(discountCode);
		WrapperInputObject<DiscountDTO> discountWrapper = new WrapperInputObject<>();
		discountWrapper.setFieldsMsg(null);
		if (discountCode.isBlank() || discountCode.length() > 10) {
			discountWrapper.setInputObject(discount);
			discountWrapper.setValid(false);
			if (discountCode.length() > 10) {
				TreeMap<String, String> fieldsMsg = discountWrapper.getFieldsMsg();
				if (fieldsMsg == null) {
					fieldsMsg = new TreeMap<>();
					discountWrapper.setFieldsMsg(fieldsMsg);
				}
				fieldsMsg.put(DiscountDTO.DISCOUNT_CODE_FIELD,
						"The Discount Code field must not exceed 10 characters. ");
				discountWrapper.setFieldsMsg(fieldsMsg);
				isValid = false;
			}
		} else {
			// Lay duoc thong tin ve discount
			DiscountDAO discountDAO = new DiscountDAO(discount);
			discountDAO.loadDiscount();
			discountWrapper = discountDAO.getWrapperDiscount();
			isValid = discountDAO.isDiscountCodeValid();
		}
		cart.setDiscount(discountWrapper);
		return isValid;
	}

	// Can ham xac dinh xem cart da xai discount chua
	// return boolean
	public boolean isDiscountExisting() {
		boolean isExisting = false;
		try {
			isExisting = cart.getDiscount().getInputObject().getDiscountID() != -1;
		} catch (NullPointerException e) {
		}
		return isExisting;
	}

	public boolean validateCart(String discountCode, Connection con) throws NamingException, SQLException {
		boolean isValid = true;
		HashMap<Integer, CartRecord> cartList = cart.getCart();
		TourDAO tourDAO = new TourDAO();
		for (CartRecord record : cartList.values()) {
			WrapperInputObject<TourDTO> tourRecord = record.getTour();
			isValid = isValid && tourDAO.validateTour(tourRecord, con);
		}
		if (!isDiscountExisting())
			isValid = isValid && validateDiscount(discountCode);
		return isValid;
	}
}
