package budgetmanager;

import java.io.IOException;
import java.time.LocalDateTime;

import budgetmanager.model.*;
import budgetmanager.util.*;
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

	public BudgetManager() {
		// Sample transaction data.
		transactionData.add(new Transaction(1.00, (LocalDateTime.now()), "transaction 1", true));
		transactionData.add(new Transaction(2.00, (LocalDateTime.now()), "transaction 2", false));
		transactionData.add(new Transaction(500.00, (LocalDateTime.now()), "transaction 3", false));
		transactionData.add(new Transaction(1000.10, (LocalDateTime.now()), "transaction 4", false));
		transactionData.add(new Transaction(-12.00, (LocalDateTime.now()), "transaction 5 testing width of transaction description in table view", false));
		transactionData.add(new Transaction(12.99, (LocalDateTime.now()), "transaction 6", false));
		transactionData.add(new Transaction(3.25, (LocalDateTime.now()), "transaction 7", false));
		transactionData.add(new Transaction(23.65, (LocalDateTime.now()), "transaction 8", false));
		transactionData.add(new Transaction(234.97, (LocalDateTime.now()), "transaction 9", false));
		transactionData.add(new Transaction(34.40, (LocalDateTime.now()), "transaction 10", false));
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
	 * Creates a new Expense object and adds it to the transaction list.  Does no data validation.
	 */
	public void addTransaction(Double value, LocalDateTime date, String description, boolean reoccuring, ExpenseCategory category) {
		transactionData.add(new Expense(value, null, "", false, null));
	}
	
	/**
	 * Creates a new Income object and adds it to the transaction list.  Does no data validation.
	 */
	public void addTransaction(Double value, LocalDateTime date, String description, boolean reoccuring, IncomeCategory category) {
		transactionData.add(new Income(value, date, description, reoccuring, category));
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

	public static void main(String[] args) {
		launch(args);
	}
}
