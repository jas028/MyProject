package budgetmanager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import budgetmanager.model.*;
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
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	private AnchorPane navigationLayout;
	private NavigationLayoutController navigationLayoutController;
	
	public Pair<String, String> emailPassword;
	
	public Totals overviewTotals = new Totals();
	public ObservableList<PieChart.Data> overviewChartData = FXCollections.observableArrayList();
	
	private ObservableList<Transaction> transactionData = FXCollections.observableArrayList();
	
	private ObservableList<Debt> debtData = FXCollections.observableArrayList();

	public BudgetManager() {
		
		// Email accessed by "emailPassword.getKey()", password accessed by "emailPassword.getValue()"
		try {
			emailPassword = loginDialog();
		} catch (Exception e) {
			Platform.exit();
		}
		
		// Sample transaction data.
		transactionData.add(new Expense(-200.00, (LocalDate.now()), "Camping Trip", false, ExpenseCategory.RECREATION));
		transactionData.add(new Expense(-100.00, (LocalDate.now()), "Savings Deposit", false, ExpenseCategory.SAVINGS));
		transactionData.add(new Expense(-120.42, (LocalDate.now()), "Cable Bill", false, ExpenseCategory.BILL));
		transactionData.add(new Expense(-125.00, (LocalDate.now()), "Groceries", false, ExpenseCategory.FOOD));
		transactionData.add(new Expense(-779.51, (LocalDate.now()), "New Laptop", false, ExpenseCategory.MISCELLANEOUS));
		transactionData.add(new Expense(-650.00, (LocalDate.now()), "Rent", true, ExpenseCategory.HOUSING));
		transactionData.add(new Income(2250.75, (LocalDate.now()), "Paycheck", false, null));
		transactionData.add(new Income(1264.43, (LocalDate.of(2015, 3, 5)), "Tax Return", false, null));
		transactionData.add(new Income(367.72, (LocalDate.of(2015, 3, 1)), "Paycheck", true, null));
		transactionData.add(new Expense(-98.10, (LocalDate.of(2015, 2, 20)), "Electricity", false, ExpenseCategory.BILL));
		
		// Sample debt data
		debtData.add(new Debt("Car Payment", 1.5, 2000.00, 98.00));
		debtData.add(new Debt("Student Loan", 1.25, 32000.00, 350.00));
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Budget Manager");
		
		initRootLayout();
		
		initNavigationLayout();
	}
	
	@Override
	public void stop() {
		// Insert database saving here
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

		// Do some validation (using the Java 8 lambda syntax).
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

	public static void main(String[] args) {
		launch(args);
	}
}
