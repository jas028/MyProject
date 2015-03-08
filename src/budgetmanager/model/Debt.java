package budgetmanager.model;

public class Debt {
	String name = "";
	double balance = 0;
	double rate = 0;
	double payment = 0;
	
	public Debt(String name, double rate, double balance, double payment){
		this.name = name;
		this.rate = rate;
		this.balance = balance;
		this.payment = payment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getPayment() {
		return payment;
	}

	public void setPayment(double payment) {
		this.payment = payment;
	}

}
