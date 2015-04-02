package budgetmanager.model;

public class User {
	
	private String email;
	private String pass_word;
	
	public User(){
		email = "email@email.com";
		pass_word = "password";
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPass_word() {
		return pass_word;
	}
	public void setPass_word(String pass_word) {
		this.pass_word = pass_word;
	}
}
