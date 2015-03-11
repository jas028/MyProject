package budgetmanager;

import java.io.IOException;
import java.time.LocalDate;

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
		transactionData.add(new Expense(-1.00, (LocalDate.now()), "transaction 1", true, null));
		transactionData.add(new Expense(-2.00, (LocalDate.now()), "transaction 2", false, null));
		transactionData.add(new Expense(-500.00, (LocalDate.now()), "transaction 3", true, null));
		transactionData.add(new Expense(-1000.10, (LocalDate.now()), "transaction 4", false, null));
		transactionData.add(new Expense(-12.00, (LocalDate.now()), "transaction 5 testing width of transaction description in table view", false, null));
		transactionData.add(new Income(12.99, (LocalDate.now()), "transaction 6", false, null));
		transactionData.add(new Income(3.25, (LocalDate.now()), "transaction 7", true, null));
		transactionData.add(new Income(23.65, (LocalDate.now()), "transaction 8", true, null));
		transactionData.add(new Income(234.97, (LocalDate.now()), "transaction 9", false, null));
		transactionData.add(new Income(34.40, (LocalDate.now()), "transaction 10", true, null));
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

	public static void main(String[] args) {
		launch(args);
	}
}
