package thuonghth.users;

public class UserDTO {
	private int accountID;
	private String name;
	private byte roleID;

	public UserDTO(int accountID, String name, byte roleID) {
		this.accountID = accountID;
		this.name = name;
		this.roleID = roleID;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getRoleID() {
		return roleID;
	}

	public void setRoleID(byte roleID) {
		this.roleID = roleID;
	}

}
