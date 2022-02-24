package thuonghth.users;

/**
 * @author thuonghth
 */
public class AccountDTO {

	private String userID;
	private String password;
	private String hashedPassword;

	public static final String USER_ID_FIELD = "userID";
	public static final String PASSWORD_FIELD = "password";

	/**
	 * Creates an account without any arguments.
	 */
	public AccountDTO() {
		this.userID = "";
		this.password = "";
		this.hashedPassword = "";
	}

	/**
	 * Creates an account with specified userID and hashed password.
	 * 
	 * @param userID   The userID or username.
	 * @param password The user's password is hashed then fill in this field.
	 */
	public AccountDTO(String userID, String password) {
		this.userID = userID;
		this.password = password;
		this.hashedPassword = "";
	}

	/**
	 * Get the account's userID.
	 * 
	 * @return A string representing the account's userID.
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * Set the account's userID.
	 * 
	 * @param userID A string containing the account's userID.
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * Get the account's password.
	 * 
	 * @return A string representing the account's password.
	 */

	public String getPassword() {
		return password;
	}

	/**
	 * Set the account's password.
	 * 
	 * @param password A string containing the account's password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get the account's hashed password.
	 * 
	 * @return A string representing the account's hashed password.
	 */
	public String getHashedPassword() {
		return hashedPassword;
	}

	/**
	 * Set the account's userID.
	 * 
	 * @param hashedPassword A string containing the account's hashed password.
	 */
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

}
