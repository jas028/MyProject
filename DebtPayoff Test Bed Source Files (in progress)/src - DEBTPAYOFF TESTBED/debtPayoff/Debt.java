package debtPayoff;

import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Pair;

public class Debt {
	
	public String name;
	public double balance;
	public double rate;
	public double payment;
	public double totalInterest;
	public double interestPaid;
	public double princPaid;
	
	public ArrayList<Pair<Integer, Double>> principle;
	
	public Debt(String name, double balance, double rate, double payment) {
		this.name = name;
		this.balance = balance;
		this.rate = rate;
		this.payment = payment;
		this.principle = new ArrayList<Pair<Integer, Double>>();
		totalInterest = 0;
		interestPaid = 0;
		princPaid = 0;

		
	
		principle.add(new Pair<Integer, Double>(0, balance));
	}

	
}