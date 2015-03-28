package budgetmanager.model;

public class Totals {
	
	private Double incomeTotal;
	private Double expenseTotal;
	private Double miscellaneousTotal;
	private Double housingTotal;
	private Double billTotal;
	private Double foodTotal;
	private Double recreationTotal;
	private Double savingsTotal;
	private Double netTotal;
	
	public Totals() {
		this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	}
	
	public Totals(Double incomeTotal, Double expenseTotal,
			Double miscellaneousTotal, Double housingTotal, Double billTotal,
			Double foodTotal, Double recreationTotal, Double savingsTotal, Double netTotal) {
		this.incomeTotal = incomeTotal;
		this.expenseTotal = expenseTotal;
		this.miscellaneousTotal = miscellaneousTotal;
		this.housingTotal = housingTotal;
		this.billTotal = billTotal;
		this.foodTotal = foodTotal;
		this.recreationTotal = recreationTotal;
		this.savingsTotal = savingsTotal;
		this.netTotal = netTotal;
	}
	
	public Double getIncomeTotal() {
		return incomeTotal;
	}
	public void setIncomeTotal(Double incomeTotal) {
		this.incomeTotal = incomeTotal;
	}
	public Double getExpenseTotal() {
		return expenseTotal;
	}
	public void setExpenseTotal(Double expenseTotal) {
		this.expenseTotal = expenseTotal;
	}
	public Double getMiscellaneousTotal() {
		return miscellaneousTotal;
	}
	public void setMiscellaneousTotal(Double miscellaneousTotal) {
		this.miscellaneousTotal = miscellaneousTotal;
	}
	public Double getHousingTotal() {
		return housingTotal;
	}
	public void setHousingTotal(Double housingTotal) {
		this.housingTotal = housingTotal;
	}
	public Double getBillTotal() {
		return billTotal;
	}
	public void setBillTotal(Double billTotal) {
		this.billTotal = billTotal;
	}
	public Double getFoodTotal() {
		return foodTotal;
	}
	public void setFoodTotal(Double foodTotal) {
		this.foodTotal = foodTotal;
	}
	public Double getRecreationTotal() {
		return recreationTotal;
	}
	public void setRecreationTotal(Double recreationTotal) {
		this.recreationTotal = recreationTotal;
	}
	public Double getSavingsTotal() {
		return savingsTotal;
	}
	public void setSavingsTotal(Double savingsTotal) {
		this.savingsTotal = savingsTotal;
	}
	public Double getNetTotal() {
		return netTotal;
	}
	public void setNetTotal(Double netTotal) {
		this.netTotal = netTotal;
	}
}
