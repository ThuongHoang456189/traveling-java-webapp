package thuonghth.users;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import thuonghth.utils.DBHelper;
import thuonghth.utils.MyConstants;
import thuonghth.utils.PBKDF2PasswordHasher;

public class UserDAO {
	public boolean isAdmin(UserDTO user) {
		return user.getRoleID() == MyConstants.ADMIN_ROLE_ID;
	}

	public boolean isCustomer(UserDTO user) {
		return user.getRoleID() == MyConstants.CUSTOMER_USER_ROLE_ID;
	}

	private boolean checkPassword(AccountDTO account) {
		return !account.getHashedPassword().isBlank()
				&& new PBKDF2PasswordHasher().checkPassword(account.getPassword(), account.getHashedPassword());
	}

	public UserDTO login(AccountDTO account) throws NamingException, SQLException {
		UserDTO found = null;
		Connection con = null;
		try {
			con = DBHelper.getConnect();
			CallableStatement cstmt = con.prepareCall("{call dbo.uspLogin(?)}");
			cstmt.setNString(1, account.getUserID());
			ResultSet rs = cstmt.executeQuery();
			if (rs.next()) {
				account.setHashedPassword(rs.getString("password"));
				found = new UserDTO(rs.getInt("accountID"), rs.getNString("name"), rs.getByte("roleID"));
				if (!checkPassword(account))
					found = null;
			}
		} finally {
			if (con != null)
				con.close();
		}
		return found;
	}
}
