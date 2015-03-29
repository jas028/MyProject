package budgetmanager.model;

public class User {
	
	private String f_name;
	private String l_name;
	private String email;
	private String user_name;
	private String pass_word;
	
	public User(){
		f_name = null;
		l_name = null;
		email = null;
		user_name = null;
		pass_word = null;
	}
	
	public String getF_name() {
		return f_name;
	}
	public void setF_name(String f_name) {
		this.f_name = f_name;
	}
	public String getL_name() {
		return l_name;
	}
	public void setL_name(String l_name) {
		this.l_name = l_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getPass_word() {
		return pass_word;
	}
	public void setPass_word(String pass_word) {
		this.pass_word = pass_word;
	}
}
