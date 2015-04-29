package budgetmanager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import budgetmanager.model.*;
import budgetmanager.mysql.MySqlConnect;
import budgetmanager.util.ExpenseCategory;
import budgetmanager.view.NavigationLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Main class execution is invoked upon.
 *
 */
public class BudgetManager extends Application {
	
	public static User user = new User();
	private MySqlConnect sql;
	private ArrayList<DebtLogSummary> debtSummary;
	private Stage primaryStage;
	private BorderPane rootLayout;
	private AnchorPane navigationLayout;
	private NavigationLayoutController navigationLayoutController;
	
	public Pair<String, String> emailPassword;
	
	public Totals overviewTotals = new Totals();
	public ObservableList<PieChart.Data> overviewChartData = FXCollections.observableArrayList();
	
	private ObservableList<Transaction> transactionData = FXCollections.observableArrayList();
	
	private ObservableList<Debt> debtData = FXCollections.observableArrayList();

	public BudgetManager() throws Exception {
		
		//Establish connection to database
		sql = new MySqlConnect();
		
		// Email accessed by "emailPassword.getKey()", password accessed by "emailPassword.getValue()"
		try {
			emailPassword = loginDialog();
		} catch (Exception e) {
			Platform.exit();
			System.exit(0);
		}
		
		//Insert user input into User object
		user.setEmail(emailPassword.getKey());
		user.setPass_word(emailPassword.getValue());
		
		//Verify if User exists
		while(!sql.validUser(user.getEmail(), user.getPass_word())){
			//Create new user
			if(!sql.available(user.getEmail())){
				try {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Username invalid/unavailable");
					alert.showAndWait();
					emailPassword = loginDialog();
				} catch (Exception e) {
					Platform.exit();
					System.exit(0);
				}
				
				//Insert user input into User object
				user.setEmail(emailPassword.getKey());
				user.setPass_word(emailPassword.getValue());
			}else{
				sql.insertUser(user);
			}
		}
		
		//ArrayList to hold all data from database
		ArrayList<Expense> expense;
		ArrayList<Income> income;
		ArrayList<Debt> debt;
		
		//get all information from database
		expense = sql.selectExpense(user);
		income = sql.selectIncome(user);
		debt = sql.selectDebt(user);
		
		System.out.println("Gathering data for User: " + user.getEmail() + "...");
		
		//Put all expenses into transactionData
		if(expense != null)
			for(int i = 0; i < expense.size(); i++){
				transactionData.add(new Expense(expense.get(i).getValue(), 
						(LocalDate.of(expense.get(i).getDate().getYear(),
								expense.get(i).getDate().getMonth(),
								expense.get(i).getDate().getDayOfMonth())),
								expense.get(i).getDescription(),
								expense.get(i).getReoccuring(),
								expense.get(i).getCategory()
						));
			}
		
		//Put all income into transactionData
		if(income != null)
			for(int i = 0; i < income.size(); i++){
				transactionData.add(new Income(income.get(i).getValue(), 
						(LocalDate.of(income.get(i).getDate().getYear(),
								income.get(i).getDate().getMonth(),
								income.get(i).getDate().getDayOfMonth())),
								income.get(i).getDescription(),
								income.get(i).getReoccuring(),
								null
						));
			}
		
		//Put all debts into debtData
		if(debt != null)
			for(int i = 0; i < debt.size(); i++){
				debtData.add(new Debt(debt.get(i).getName(), debt.get(i).getRate(),
						debt.get(i).getBalance(), debt.get(i).getPayment()));
			}
		
		//close connection to the database
		sql.MySQLDisconnect();
		
		debtSummary = new ArrayList<DebtLogSummary>();
		if(snowballMethod(debtSummary, 1320)){
			for(int i = 0; i < debtSummary.size(); i++){
				System.out.println(debtSummary.get(i).getDebt().getName());
			}
		}
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Budget Manager");
		
		initRootLayout();
		
		initNavigationLayout();
	}
	
	public void stop() throws Exception {
		if (emailPassword != null) {
			//Connect to database
			sql = new MySqlConnect();
			//Clear all old entries from the database related to User
			sql.deleteAllTransactions(user);
			sql.deleteAllDebts(user);
			//Get all new updates entries from the User and copy to the database
			System.out.println("Copying data to the database...");
			for (Transaction transactionData : transactionData) {
				if (transactionData instanceof Expense) {
					sql.insertExpense(user, (Expense) transactionData);
				}

				if (transactionData instanceof Income) {

					sql.insertIncome(user, (Income) transactionData);
				}
			}
			for (Debt debtData : debtData) {
				sql.insertDebt(user, debtData);
			}
			//disconnect from the database
			sql.MySQLDisconnect();
		}
	}

	/**
	 * Initializes the root layout container.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from FXML file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(BudgetManager.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the navigation layout inside the root layout.
	 */
	public void initNavigationLayout() {
		try {
			// Load navigation layout from FXML file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(BudgetManager.class.getResource("view/NavigationLayout.fxml"));
			navigationLayout = (AnchorPane) loader.load();
			
			// Set navigation layout into the center of the root layout
			rootLayout.setCenter(navigationLayout);
			
			// Load the navigation layout controller.
			navigationLayoutController = (NavigationLayoutController) loader.getController();
			navigationLayoutController.setMainApp(this);
			navigationLayoutController.handleOverviewDatePicker();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Shows dialog for email password pair.
	 * 
	 * @return Pair<String, String>
	 */
	public Pair<String, String> loginDialog() {
		Dialog<Pair<String, String>> loginDialog = new Dialog<>();
		loginDialog.setTitle("Login");
		
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
		
		// Create the email and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField email = new TextField();
		email.setPromptText("Email");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Email:"), 0, 0);
		grid.add(email, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);
		
		// Enable/Disable login button depending on whether a email was entered.
		Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Input validation
		email.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		loginDialog.getDialogPane().setContent(grid);
		
		// Request focus on the email field by default.
		Platform.runLater(() -> email.requestFocus());

		// Convert the result to a email-password-pair when the login button is clicked.
		loginDialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(email.getText(), password.getText());
		    }
		    return null;
		});

		Optional<Pair<String, String>> result = loginDialog.showAndWait();
		
		if(result.get().getKey().equals("")) { return null; }
		
		return result.get();
	}
	
	/**
	 * Calculates totals for incomes, expenses, and subexpenses.
	 * @throws Exception 
	 */
	public void calcOverviewTotals(LocalDate start, LocalDate end) throws Exception {
		Double incomeTotal = 0.0, expenseTotal = 0.0, miscellaneousTotal = 0.0, housingTotal = 0.0,
				billTotal = 0.0, foodTotal = 0.0, recreationTotal = 0.0, savingsTotal = 0.0, netTotal = 0.0;
		
		if(start.isAfter(end)) {
			throw new Exception("Invalid date range");
		}
		
		for(Transaction transaction: transactionData) {
			if((transaction.getDate().isEqual(start) || transaction.getDate().isAfter(start))
					&& (transaction.getDate().isEqual(end) || transaction.getDate().isBefore(end))) {
				
				if(transaction instanceof Income) {
					incomeTotal += transaction.getValue();
				}
				else if(transaction instanceof Expense) {
					if(((Expense) transaction).getCategory() == ExpenseCategory.MISCELLANEOUS) {
						miscellaneousTotal += transaction.getValue();
					}
					else if(((Expense) transaction).getCategory() == ExpenseCategory.HOUSING) {
						housingTotal += transaction.getValue();
					}
					else if(((Expense) transaction).getCategory() == ExpenseCategory.BILL) {
						billTotal += transaction.getValue();
					}
					else if(((Expense) transaction).getCategory() == ExpenseCategory.FOOD) {
						foodTotal += transaction.getValue();
					}
					else if(((Expense) transaction).getCategory() == ExpenseCategory.RECREATION) {
						recreationTotal += transaction.getValue();
					}
					else if(((Expense) transaction).getCategory() == ExpenseCategory.SAVINGS) {
						savingsTotal += transaction.getValue();
					}
				}
			}
		}
		
		expenseTotal = miscellaneousTotal + housingTotal + billTotal + foodTotal + recreationTotal + savingsTotal;
		netTotal = incomeTotal + expenseTotal;
		
		// Update 
		overviewTotals.setBillTotal(billTotal);
		overviewTotals.setExpenseTotal(expenseTotal);
		overviewTotals.setFoodTotal(foodTotal);
		overviewTotals.setHousingTotal(housingTotal);
		overviewTotals.setIncomeTotal(incomeTotal);
		overviewTotals.setMiscellaneousTotal(miscellaneousTotal);
		overviewTotals.setNetTotal(netTotal);
		overviewTotals.setRecreationTotal(recreationTotal);
		overviewTotals.setSavingsTotal(savingsTotal);
		
		// Update pie chart data
		overviewChartData.clear();
		ArrayList<Double> breakdownList = new ArrayList<Double>();
		breakdownList.add((miscellaneousTotal/expenseTotal*100));
		breakdownList.add((housingTotal/expenseTotal*100));
		breakdownList.add((billTotal/expenseTotal*100));
		breakdownList.add((foodTotal/expenseTotal*100));
		breakdownList.add((recreationTotal/expenseTotal*100));
		breakdownList.add((savingsTotal/expenseTotal*100));
		ArrayList<String> categoriesList = new ArrayList<String>();
		categoriesList.add("Miscellaneous");
		categoriesList.add("Housing");
		categoriesList.add("Bills");
		categoriesList.add("Food");
		categoriesList.add("Recreation");
		categoriesList.add("Savings");
		
		int i = 0;
		for(Double percentage: breakdownList) {
			if (percentage != 0) {
				overviewChartData.add(new PieChart.Data(categoriesList.get(i), percentage));
			}
			++i;
		}
	}
	
	public Boolean avalancheMethod(ArrayList <DebtLogSummary> debtSummary, double pool){
		int offset = 0; //this is used to offset when we want to apply more payment for the next bill
		int index = 0;//index for the next maximum rate
		
		//This loop puts all debts in the arraylist to a log
		//the log hold the payment summary, debt information and payoff date
		for(int i = 0; i < debtData.size(); i++){
			DebtLogSummary dls = new DebtLogSummary();//new log
			dls.setDebt(debtData.get(i));//insert the debt in the log
			pool -= dls.getMinPayment();
			debtSummary.add(dls);//add the debt log to array list of debt logs
		}
		
		//If pool is less than accumulated min payments
		if(pool < 0)
			return false;
	
		//Do the summary for the upper bound (minimum payments)
		for(int i = 0; i < debtSummary.size(); i++){			
			debtSummary.get(i).worstCasePayoff();
		}
		
		//Sort the the bill by projected payoff date
		Collections.sort(debtSummary);
	
		//loop to get new payoffdate
		for(int i = 0; i < debtSummary.size(); i++){
			
			//find max interest rate, this will go ahead and do the first calculation again
			double max = 0;	
			for(int j = 0; j < debtSummary.size(); j++)
				if(max < debtSummary.get(j).getRate()){
					max = debtSummary.get(j).getRate();
					index = j;
				}
			
			//Set the minimum payment to to the min + extra
			debtSummary.get(index).setMinPayment(debtSummary.get(index).getMinPayment() + pool);
			
			//start the calculation at the offset date
			debtSummary.get(index).payoffSooner(debtSummary.get(index), offset);
		
			//sort the list again by payoff date
			Collections.sort(debtSummary);
			
			//index i is always the next projected bill to be paid off
			//so accumulate the minimum payment to the pool
			pool = debtSummary.get(i).getMinPayment();
			//get the new offset from the ith bill
			offset = debtSummary.get(i).getPayoffDate();
			//set the iths bills rate to zero for the finding max technique
			debtSummary.get(i).setRate(0);
		}	
		return true;
	}
	
	public Boolean snowballMethod(ArrayList <DebtLogSummary> debtSummary, double pool){
		int offset = 0; //this is used to offset when we want to apply more payment for the next bill
		int index = 0;//index for the next maximum rate
		
		//This loop puts all debts in the arraylist to a log
		//the log hold the payment summary, debt information and payoff date
		for(int i = 0; i < debtData.size(); i++){
			DebtLogSummary dls = new DebtLogSummary();//new log
			dls.setDebt(debtData.get(i));//insert the debt in the log
			pool -= dls.getMinPayment();
			debtSummary.add(dls);//add the debt log to array list of debt logs
		}
		
		//If pool is less than accumulated min payments
		if(pool < 0)
			return false;
	
		//Do the summary for the upper bound (minimum payments)
		for(int i = 0; i < debtSummary.size(); i++){			
			debtSummary.get(i).worstCasePayoff();
		}
		
		//Sort the the bill by projected payoff date
		Collections.sort(debtSummary);
	
		//loop to get new payoffdate
		for(int i = 0; i < debtSummary.size(); i++){
			
			//find min balance, this will go ahead and do the first calculation again
			double min = debtSummary.get(i).getBalance();	
			for(int j = 0; j < debtSummary.size(); j++)
				if(min > debtSummary.get(j).getBalance()){
					min = debtSummary.get(j).getBalance();
					index = j;
				}
			
			//Set the minimum payment to to the min + extra
			debtSummary.get(index).setMinPayment(debtSummary.get(index).getMinPayment() + pool);
			
			//start the calculation at the offset date
			debtSummary.get(index).payoffSooner(debtSummary.get(index), offset);
		
			//sort the list again by payoff date
			Collections.sort(debtSummary);
			
			//index i is always the next projected bill to be paid off
			//so accumulate the minimum payment to the pool
			pool = debtSummary.get(i).getMinPayment();
			//get the new offset from the ith bill
			offset = debtSummary.get(i).getPayoffDate();
			//set the iths bills balance to zero for the finding max technique
			debtSummary.get(i).setBalance(Integer.MAX_VALUE);
		}	
		return true;
	}
	
	/**
	 * Adds an Expense object to the transactionData list.
	 */
	public void addTransaction(Expense expense) {
		transactionData.add(expense);
	}
	
	/**
	 * Adds an Expense object to the transactionData list.
	 */
	public void addTransaction(Income income) {
		transactionData.add(income);
	}
	
	/**
	 * Returns the main stage.
	 * @return	The main stage.
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Returns the data as an observable list of Transactions.
	 * @return
	 */
	public ObservableList<Transaction> getTransactionData() {
		return transactionData;
	}
	
	public void addDebt(Debt debt) {
		debtData.add(debt);
	}
	
	/**
	 * Returns the data as an observable list of Debts.
	 * @param 
	 */
	public ObservableList<Debt> getDebtData() {
		return debtData;
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
