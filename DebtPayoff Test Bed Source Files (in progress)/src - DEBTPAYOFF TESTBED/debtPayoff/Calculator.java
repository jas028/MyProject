package debtPayoff;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Calculator extends Application{

	public static void main(String[] args) {

		// mortgage  -  175,000  -  1,100  -  4%
		// cc        -  1,200    -  15     - 18%
		// sl		 -  30,000   -  300	   - 6.7%
		// car loan  -  10,000   -  250    - 3%

		ArrayList<Debt> debtList = new ArrayList<Debt>();	
		ArrayList<Debt> calDebtList = new ArrayList<Debt>();
		ArrayList<Debt> storeDebtList = new ArrayList<Debt>();
		
		for (Debt debt:debtList){
			calDebtList.add(debt);
			storeDebtList.add(debt);
		}
		

		calDebtList.add(new Debt("Mortgage", 175000, .04, 1100));
		calDebtList.add(new Debt("Credit Card", 1200, .18, 15));
		calDebtList.add(new Debt("Student Loan", 30000, .067, 300));
		calDebtList.add(new Debt("Car Loan", 10000, .03, 250));
		storeDebtList.add(new Debt("Mortgage", 175000, .04, 1100));
		storeDebtList.add(new Debt("Credit Card", 1200, .18, 15));
		storeDebtList.add(new Debt("Student Loan", 30000, .067, 300));
		storeDebtList.add(new Debt("Car Loan", 10000, .03, 250));

		double maxRate;
		int month = 1;
		int maxRateIndex = 0;
		int index = 0;
		double totalBalance = 0;
		double extraPayment = 0;			// pass in extra payment data

		for (Debt debt:calDebtList){
			totalBalance += debt.balance;
		}

		while (totalBalance > 0){
			totalBalance = 0;
			maxRate = 0;


			for (Debt debt:calDebtList){			// Assess data for one month
				if (maxRate < debt.rate){
					maxRate = debt.rate;
					maxRateIndex = calDebtList.indexOf(debt);
				}			
			}

			for (Debt debt:calDebtList){			// Update data for one month
				debt.interestPaid = (debt.balance * debt.rate) / 12;
				
				debt.princPaid = debt.payment - debt.interestPaid;
				if (calDebtList.indexOf(debt) == maxRateIndex){
					debt.princPaid += extraPayment;
				}
				
// The first line here throws an exception for an index being out of bounds.  To get around it I made a second
// arraylist named storeDebtList that ony stores the values.  We can still use the calDebtList to make the calculations
// and delete debts from calDebtList when they reach zero.
//				
//				debtList.get(calDebtList.indexOf(debt)).totalInterest += debt.interestPaid;
				storeDebtList.get(calDebtList.indexOf(debt)).totalInterest += debt.interestPaid;
				
				if (debt.balance < (debt.princPaid + debt.interestPaid)){
					storeDebtList.get(calDebtList.indexOf(debt)).principle.add(new Pair<Integer, Double>(month, 0.00));
					debt.balance = 0;
				}
				else{
					debt.balance -= debt.princPaid;
					storeDebtList.get(calDebtList.indexOf(debt)).principle.add(new Pair<Integer, Double>(month, debt.balance));
				}
			}
			month++;
			index++;
			
			
			
		for (Debt debt:debtList){
			totalBalance += debt.balance;
		}
		}






	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

























	/*

		// TODO Auto-generated method stub

	// mortgage  -  175,000  -  1,100  -  4%
	// cc        -  1,200    -  15     - 18%
	// sl		 -  30,000   -  300	   - 6.7%
	// car loan  -  10,000   -  250    - 3%

	String[] name = {"Mortgage", "Credit Card", "Student Loan", "Car Loan"};
	double[] balance = {175000, 1200, 30000, 10000};
	double[] minPayment = {1100, 15, 300, 250};
	double[] rate = {.04, .18, .067, .03};
	double[] principle = new double[12*50];
	double[] totalInterest = new double[4];
	double[] extraPayment = new double[4];
	int months = 0;
	double interestPaid = 0;
	double princPaid = 0;
	double payment = 0;
	double leftoverPayment = 0;

	int maxIndex = 0;

	double totalBalance = 0;
	for (int x = 0; x < 4; x++){
		totalBalance += balance[x];
	}
	System.out.println(totalBalance);
	while (totalBalance > 0){
		totalBalance = 0;
		for (int x = 0; x < 4; x++){

			balance[x] -= minPayment[x];
		}


		for (int x = 0; x < 4; x++){
			totalBalance += balance[x];
		}
	}



/*	
	double maxRate = rate[0];
	//for (int x = 0; x < 4; x++){
	//	payment += minPayment[x]; 	
	//}

	for (int i = 0; i < 4; i++){

		for (int x = 1; x < 4; x++){
			if (rate[x] > maxRate){
				maxRate = rate[x];
				maxIndex = x;
			}
		}
		extraPayment[maxIndex] = 300;

		principle[0] = balance[i];

		payment = minPayment[i] + extraPayment[i];	

		while (balance[i] > 0){
			interestPaid = (balance[i] * rate[i])/12;
			princPaid = payment  - interestPaid;
			totalInterest[i] += interestPaid;

			if (months > 0){
				principle[months] = principle[months - 1];
			}
			principle[months] -= princPaid;

			if (balance[i] < payment){
				rate[i] = 0;
				leftoverPayment = payment - balance[i];
				balance[i] = 0;
				months = 0;
				for (int j = 0; j < 4; j++){
					extraPayment[j] = 0;
				}
			}
			else{
				balance[i] = principle[months];
				months++;
			}
		}
	}

	for (int i = 0; i < months; i++){
		System.out.println(principle[i]);
	}
	System.out.println("Total Interest Paid: " + totalInterest[0]);

	//months[0] = (Math.log(actualPayment/(actualPayment - balance[0]*rate[0])))/(Math.log(1 + rate[0]));

	//System.out.println("Debt Payoff Calculator: Avalanche Method \n");
	System.out.println(leftoverPayment);
	}
	 */
}

