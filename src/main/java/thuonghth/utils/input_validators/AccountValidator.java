package thuonghth.utils.input_validators;

import java.util.TreeMap;

import thuonghth.users.AccountDTO;
import thuonghth.utils.PBKDF2PasswordHasher;

public class AccountValidator {
//  AccountDTO
//  private String userID;
//	private String hashedPassword;
//	private short statusID;
	public final String USER_ID_FIELD = "User ID";
	public final String PASSWORD_FIELD = "Password";

	public final int USER_ID_LENGTH = 50;

	public final String USER_ID_MAX_LENGTH_ERROR = "The User ID is over the maximum length of " + USER_ID_LENGTH
			+ " characters. ";

	public String userID;
	public String password;
	public TreeMap<String, String> fieldsMsg;

	public AccountValidator(String userID, String password) {
		this.userID = userID.trim();
		this.password = password.trim();
		this.fieldsMsg = new TreeMap<>();
	}

	public String validateRequiredString(String str, String fieldName) {
		return str.isEmpty() ? "The " + fieldName + " field must not be blank. " : "";
	}

	public String validateUserID(String userID) {
		String errorMsg = "";
		errorMsg += validateRequiredString(userID, USER_ID_FIELD);
		errorMsg += userID.length() > USER_ID_LENGTH ? USER_ID_MAX_LENGTH_ERROR : "";
		return errorMsg;
	}

	public String validatePassword(String password) {
		return validateRequiredString(password, PASSWORD_FIELD);
	}

	public TreeMap<String, String> validateAccount() {
		fieldsMsg.put(AccountDTO.USER_ID_FIELD, validateUserID(this.userID));
		fieldsMsg.put(AccountDTO.PASSWORD_FIELD, validatePassword(this.password));
		return fieldsMsg;
	}

	public boolean isAccountValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(AccountDTO.USER_ID_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(AccountDTO.PASSWORD_FIELD).equals("");
		return isValid;
	}

	public AccountDTO getAccountDTO() {
		return new AccountDTO(this.userID, this.password);
	}
}
