package thuonghth.orders;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;

import thuonghth.carts.CartDTO;
import thuonghth.carts.CartRecord;
import thuonghth.discounts.DiscountDTO;
import thuonghth.tours.TourDTO;
import thuonghth.users.UserDTO;

public class OrderDAO {
	private OrderDTO order;

	private OrderDetailDTO convertCartRecordToOrderDetail(CartRecord cartRecord) {
		TourDTO tour = cartRecord.getTour().getInputObject();
		int amount = cartRecord.getAmount();
		BigDecimal subTotal = cartRecord.getSubTotal();
		return new OrderDetailDTO(tour, amount, subTotal);
	}

	public OrderDTO convertCartToOrder(CartDTO cart) {
		LinkedList<OrderDetailDTO> orderDetails = new LinkedList<>();
		for (CartRecord record : cart.getCart().values()) {
			orderDetails.add(convertCartRecordToOrderDetail(record));
		}
		DiscountDTO discount = cart.getDiscount() == null || cart.getDiscount().getInputObject() == null ? null
				: cart.getDiscount().getInputObject();
		BigDecimal total = cart.getTotal();
		return new OrderDTO(orderDetails, discount, total);
	}

	private boolean insertOrderDetail(OrderDetailDTO detail, Connection con) throws SQLException {
		CallableStatement cstmt = con.prepareCall("{? = call dbo.uspInsertOrderDetail(?,?,?,?)}");
		cstmt.registerOutParameter(1, Types.BIT);
		cstmt.setInt(2, detail.getOrderDetailID());
		cstmt.setInt(3, detail.getTour().getTourID());
		cstmt.setInt(4, detail.getAmount());
		cstmt.setBigDecimal(5, detail.getSubTotal());
		cstmt.execute();
		return cstmt.getBoolean(1);
	}

	public boolean insertOrder(CartDTO cart, Connection con, UserDTO customer) throws SQLException {
		order = convertCartToOrder(cart);
		boolean success = false;
		if (con != null) {
			CallableStatement cstmt = con.prepareCall("{call dbo.uspInsertOrder(?,?,?)}");
			cstmt.setInt(1, order.getDiscount() == null ? -1 : order.getDiscount().getDiscountID());
			cstmt.setBigDecimal(2, order.getTotal());
			cstmt.setInt(3, customer.getAccountID());
			ResultSet rs = cstmt.executeQuery();
			if (rs.next()) {
				int orderID = rs.getInt(1);
				order.setOrderID(orderID);
				order.setOrderDate(rs.getDate(2));
				for (OrderDetailDTO detail : order.getOrderDetails()) {
					detail.setOrderDetailID(orderID);
					success = insertOrderDetail(detail, con);
					if (!success)
						break;
				}
			}
		}
		return success;
	}

	public OrderDTO getOrder() {
		return order;
	}

	public void setOrder(OrderDTO order) {
		this.order = order;
	}

}
