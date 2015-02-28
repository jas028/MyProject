//Handles interaction between Model and View

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class BudgetManagerController {

	private BudgetManagerModel 	budgetModel;
	private BudgetManagerView 	budgetView;
	
	public BudgetManagerController(BudgetManagerModel bM, BudgetManagerView bV) {
		
		this.budgetModel = bM;
		this.budgetView = bV;
		
		//add new ActionListener to calcButton of View
		budgetView.addCalcListener(new CalcListener());
	}
	
	//class to implement ActionListener
	private class CalcListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			int num1, num2 = 0;
			
			//reading numbers surrounded by try block in case of noninteger entry
			try {
				
				num1 = budgetView.getNum1();
				num2 = budgetView.getNum2();
				
				budgetModel.sum(num1, num2);
				
				budgetView.setCalcResult(budgetModel.getSum());
			}
			catch(NumberFormatException ex) {
				
				System.out.println(ex);
				
				budgetView.displayErrorMessage("You must enter two integers");
			}
		}	
	}
}
