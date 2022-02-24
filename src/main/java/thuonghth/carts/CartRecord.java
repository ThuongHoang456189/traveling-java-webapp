package thuonghth.carts;

import java.math.BigDecimal;

import thuonghth.tours.TourDTO;
import thuonghth.utils.input_validators.WrapperInputObject;

public class CartRecord {
	private WrapperInputObject<TourDTO> tour;
	private int amount;
	private BigDecimal subTotal;

	public CartRecord() {
		this.tour = new WrapperInputObject<>();
		this.amount = 0;
		this.subTotal = BigDecimal.ZERO;
	}

	public WrapperInputObject<TourDTO> getTour() {
		return tour;
	}

	public void setTour(WrapperInputObject<TourDTO> tour) {
		this.tour = tour;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

}
