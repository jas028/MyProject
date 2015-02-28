//User executes this program to start the app

public class BudgetApp {

	public static void main(String[] args) {
		
		//init components
		BudgetManagerModel 			budgetModel = new BudgetManagerModel();
		BudgetManagerView			budgetView = new BudgetManagerView();
		BudgetManagerController 	budgetController = new BudgetManagerController(budgetModel, budgetView);
		
		budgetView.setVisible(true);
	}
}
