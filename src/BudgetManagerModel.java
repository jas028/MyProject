//Stores all data and performs all calculations

public class BudgetManagerModel {
	
	private int sumValue;
	
	public void sum(int product1, int product2) {
		
		this.sumValue = product1 + product2;
	}
	
	public int getSum() {
		
		return this.sumValue;	
	}
	
}
