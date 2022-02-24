package thuonghth.discounts;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

import javax.naming.NamingException;

import thuonghth.utils.DBHelper;
import thuonghth.utils.input_validators.WrapperInputObject;

public class DiscountDAO {
	private DiscountDTO discount;

	public DiscountDAO(DiscountDTO discount) {
		this.discount = discount;
	}

	public void loadDiscount() throws NamingException, SQLException {
		BigDecimal percent = BigDecimal.ZERO;
		Connection con = null;
		try {
			con = DBHelper.getConnect();
			CallableStatement cstmt = con.prepareCall("{call dbo.uspGetDiscount(?)}");
			cstmt.setString(1, discount.getDiscountCode());
			ResultSet rs = cstmt.executeQuery();
			if (rs.next()) {
				percent = rs.getBigDecimal("discountPercent");
				discount.setDiscountID(rs.getInt("discountID"));
			}
			discount.setDiscountPercent(percent);
		} finally {
			if (con != null)
				con.close();
		}
	}

	// This method for order insertion
	public void loadDiscount(Connection con) throws SQLException {
		BigDecimal percent = BigDecimal.ZERO;
		CallableStatement cstmt = con.prepareCall("{call dbo.uspGetDiscount(?)}");
		cstmt.setString(1, discount.getDiscountCode());
		ResultSet rs = cstmt.executeQuery();
		if (rs.next()) {
			percent = rs.getBigDecimal("discountPercent");
			discount.setDiscountID(rs.getInt("discountID"));
		}
		discount.setDiscountPercent(percent);
	}

	public boolean isDiscountCodeValid() {
		return discount.getDiscountPercent() != null && discount.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0;
	}

	public WrapperInputObject<DiscountDTO> getWrapperDiscount() {
		WrapperInputObject<DiscountDTO> wrapper = new WrapperInputObject<>();
		wrapper.setInputObject(discount);
		wrapper.setValid(isDiscountCodeValid());
		TreeMap<String, String> fieldsMsg = new TreeMap<>();
		fieldsMsg.put(DiscountDTO.DISCOUNT_CODE_FIELD, wrapper.isValid() ? "" : "The discount code is invalid. ");
		wrapper.setFieldsMsg(fieldsMsg);
		return wrapper;
	}
}
