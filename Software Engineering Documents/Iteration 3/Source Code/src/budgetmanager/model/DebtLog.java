package budgetmanager.model;

public class DebtLog {
	
	private int month;
	private double principle;
	
	public DebtLog(){
		month = 0;
		principle = 0;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public double getPrinciple() {
		return principle;
	}

	public void setPrinciple(double principle) {
		this.principle = principle;
	}
	
}
