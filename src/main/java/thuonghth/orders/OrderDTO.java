package thuonghth.orders;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedList;

import thuonghth.discounts.DiscountDTO;
import thuonghth.users.UserDTO;

public class OrderDTO {
	private int orderID;
	private LinkedList<OrderDetailDTO> orderDetails;
	private Date orderDate;
	private DiscountDTO discount;
	private BigDecimal total;
	private UserDTO customer;

	public OrderDTO(int orderID, LinkedList<OrderDetailDTO> orderDetails, Date orderDate, DiscountDTO discount,
			BigDecimal total, UserDTO customer) {
		this.orderID = orderID;
		this.orderDetails = orderDetails;
		this.orderDate = orderDate;
		this.discount = discount;
		this.total = total;
		this.customer = customer;
	}

	public OrderDTO(LinkedList<OrderDetailDTO> orderDetails, DiscountDTO discount, BigDecimal total, UserDTO customer) {
		this.orderDetails = orderDetails;
		this.discount = discount;
		this.total = total;
		this.customer = customer;
	}
	
	public OrderDTO(LinkedList<OrderDetailDTO> orderDetails, DiscountDTO discount, BigDecimal total) {
		this.orderDetails = orderDetails;
		this.discount = discount;
		this.total = total;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public LinkedList<OrderDetailDTO> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(LinkedList<OrderDetailDTO> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public DiscountDTO getDiscount() {
		return discount;
	}

	public void setDiscount(DiscountDTO discount) {
		this.discount = discount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public UserDTO getCustomer() {
		return customer;
	}

	public void setCustomer(UserDTO customer) {
		this.customer = customer;
	}

}
