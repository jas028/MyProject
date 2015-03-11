package budgetmanager;

import java.io.IOException;
import java.time.LocalDate;

import budgetmanager.model.*;
import budgetmanager.view.NavigationLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main class execution is invoked upon.
 *
 */
public class BudgetManager extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	private AnchorPane navigationLayout;
	private NavigationLayoutController navigationLayoutController;
	
	private ObservableList<Transaction> transactionData = FXCollections.observableArrayList();
	
	private ObservableList<Debt> debtData = FXCollections.observableArrayList();

	public BudgetManager() {
		// Sample transaction data.
		transactionData.add(new Expense(-7.50, (LocalDate.now()), "George's Grill", false, null));
		transactionData.add(new Expense(-5.00, (LocalDate.now()), "Parking", false, null));
		transactionData.add(new Expense(-650.00, (LocalDate.of(2015, 3, 8)), "Rent", true, null));
		transactionData.add(new Income(1264.43, (LocalDate.of(2015, 3, 5)), "Tax Return", false, null));
		transactionData.add(new Income(367.48, (LocalDate.of(2015, 3, 1)), "Paycheck", true, null));
		transactionData.add(new Expense(-98.10, (LocalDate.of(2015, 2, 20)), "Electricity", false, null));
		
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
		} catch(IOException e) {
			e.printStackTrace();
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
