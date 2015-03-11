package budgetmanager.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Debt {
	
	private final StringProperty name;
	private final DoubleProperty balance;
	private final DoubleProperty rate;
	private final DoubleProperty payment;
	
	public Debt() {
		this(null, 0.0, 0.0, 0.0);
	}
	
	/**
	 * Constructor with initial data
	 * @param name
	 * @param rate
	 * @param balance
	 * @param payment
	 */
	public Debt(String name, Double rate, Double balance, Double payment){
		this.name = new SimpleStringProperty(name);
		this.rate = new SimpleDoubleProperty(rate);
		this.balance = new SimpleDoubleProperty(balance);
		this.payment = new SimpleDoubleProperty(payment);
	}

	public String getName() {
		return name.getValue();
	}
	
	public StringProperty getNameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);;
	}

	public double getBalance() {
		return balance.getValue();
	}
	
	public DoubleProperty getBalanceProperty() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance.setValue(balance);;
	}

	public double getRate() {
		return rate.getValue();
	}
	
	public DoubleProperty getRateProperty() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate.setValue(rate);
	}

	public double getPayment() {
		return payment.getValue();
	}
	
	public DoubleProperty getPaymentProperty() {
		return payment;
	}

	public void setPayment(Double payment) {
		this.payment.setValue(payment);;
	}
}
