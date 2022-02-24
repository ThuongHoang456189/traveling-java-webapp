package thuonghth.orders;

import java.math.BigDecimal;

import thuonghth.tours.TourDTO;

public class OrderDetailDTO {
	private int orderDetailID;
	private TourDTO tour;
	private int amount;
	private BigDecimal subTotal;

	public OrderDetailDTO(int orderDetailID, TourDTO tour, int amount, BigDecimal subTotal) {
		this.orderDetailID = orderDetailID;
		this.tour = tour;
		this.amount = amount;
		this.subTotal = subTotal;
	}

	public OrderDetailDTO(TourDTO tour, int amount, BigDecimal subTotal) {
		this.tour = tour;
		this.amount = amount;
		this.subTotal = subTotal;
	}

	public int getOrderDetailID() {
		return orderDetailID;
	}

	public void setOrderDetailID(int orderDetailID) {
		this.orderDetailID = orderDetailID;
	}

	public TourDTO getTour() {
		return tour;
	}

	public void setTour(TourDTO tour) {
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
