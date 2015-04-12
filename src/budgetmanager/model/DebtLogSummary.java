package budgetmanager.model;
import java.util.ArrayList;

public class DebtLogSummary implements Comparable<DebtLogSummary>{
	
	private ArrayList<DebtLog> debtLogSummary;
	private Debt debt;
	private int payoffDate;
	private double rate;
	private double minPayment;
	
	public DebtLogSummary(){
		debtLogSummary = new ArrayList<DebtLog>();
		payoffDate = 0;
		debt = null;
		rate = 0;
		minPayment = 0;
	}
	

	public int compareTo(DebtLogSummary o) {
		
		if(payoffDate - o.getPayoffDate() > 0)
			return 1;
		if(payoffDate - o.getPayoffDate() < 0)
			return -1;
		
		return 0;
	}

	public void worstCasePayoff(){
		
		double interestPaid = 0;
		double principlePaid = 0;
		double balance = debt.getBalance();
		int month = 0;
		
		
		while(balance > 0){
			DebtLog debtLog = new DebtLog();
			debtLog.setMonth(month);
			interestPaid = (balance * rate) / 12;
			principlePaid = minPayment - interestPaid;
			
			if(month > 0)
				debtLog.setPrinciple(debtLogSummary.get(month -1).getPrinciple());

			else{
				debtLog.setPrinciple(debt.getBalance());

			}
			
			debtLog.setPrinciple(debtLog.getPrinciple() - principlePaid);
			
			if(balance < minPayment){
				balance = 0;
				debtLog.setPrinciple(0);
			}
			
			else{
				balance = debtLog.getPrinciple();
				month++;
			}
			debtLogSummary.add(debtLog);
		}
		
		payoffDate = month;
	}
	
	public void payoffSooner(DebtLogSummary dls, int offset){
		
		int month = offset;
		
		if(month < dls.getPayoffDate()){
			
			double balance = dls.getDebtLogSummary().get(month).getPrinciple();
			double interestPaid = 0;
			double principlePaid = 0;
			
			while(balance > 0){
				DebtLog debtLog = new DebtLog();
				debtLog.setMonth(month);
				interestPaid = (balance * rate) / 12;
				principlePaid = minPayment - interestPaid;
			
				if(month > 0)
					debtLog.setPrinciple(debtLogSummary.get(month - 1).getPrinciple());

				else{
					debtLog.setPrinciple(debt.getBalance());

				}
			
				debtLog.setPrinciple(debtLog.getPrinciple() - principlePaid);
			
				if(balance < minPayment){
					balance = 0;
					debtLog.setPrinciple(0);
					month++;
				}
			
				else{
					balance = debtLog.getPrinciple();
					month++;
				}
				debtLogSummary.set(month - 1, debtLog);
			}
		
			payoffDate = month;
			for(int i = month; i < debtLogSummary.size(); i += 0)
				debtLogSummary.remove(i);
		}
	}
	
	public ArrayList<DebtLog> getDebtLogSummary() {
		return debtLogSummary;
	}

	public void setDebtLogSummary(ArrayList<DebtLog> debtLogSummary) {
		this.debtLogSummary = debtLogSummary;
	}

	public Debt getDebt() {
		return debt;
	}

	public void setDebt(Debt debt) {
		this.debt = debt;
		rate = debt.getRate();
		minPayment = debt.getPayment();
	}
	
	
	public int getPayoffDate() {
		return payoffDate;
	}

	public void setPayoffDate(int payoffDate) {
		this.payoffDate = payoffDate;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getMinPayment() {
		return minPayment;
	}

	public void setMinPayment(double minPayment) {
		this.minPayment = minPayment;
	}	
}
