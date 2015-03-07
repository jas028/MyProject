package budgetmanager.view;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import budgetmanager.BudgetManager;
import budgetmanager.model.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class NavigationLayoutController {
	
	@FXML
	private TableView<Transaction> transactionTable;
	@FXML
	private TableColumn<Transaction, Number> expenseColumn;
	@FXML
	private TableColumn<Transaction, LocalDateTime> dateColumn;
	@FXML
	private TableColumn<Transaction, String> descriptionColumn;
	
	//---
	@FXML
	private TextField savingsCalDescription;
	@FXML
	private TextField savingsCalGoal;
	@FXML
	private TextField savingsCalAllotment;
	@FXML
	private Label savingsCalResult;
	@FXML
	private Button savingsCalButton;
	
	// Reference to main app.
	private BudgetManager budgetManager;
	
	/**
	 * Initializes the controller class.  Automatically called after the fxml file is loaded.
	 */
	@FXML
	private void initialize() {
		// Initialize the transaction table with the data.
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
		expenseColumn.setCellValueFactory(cellData -> cellData.getValue().getExpenseProperty());
		
		// Custom rendering of the date column table cell.
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("hh:mm MM/dd/yyyy");
		dateColumn.setCellFactory(column -> {
		    return new TableCell<Transaction, LocalDateTime>() {
		        @Override
		        protected void updateItem(LocalDateTime item, boolean empty) {
		            super.updateItem(item, empty);

		            if (item == null || empty) {
		                setText(null);
		                setStyle("");
		            } else {
		                // Format date.
		                setText(dateFormatter.format(item));
		            }
		        }
		    };
		});
		
		// Custom rendering of the expense column table cell.
		NumberFormat numberFormatter = new DecimalFormat("$###,###.00");
		expenseColumn.setCellFactory(column -> {
			return new TableCell<Transaction, Number>() {
				@Override
				protected void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						// Format value.
						setText(numberFormatter.format(item));
					}
				}
			};
		});
	}
	
	/**
	 * Called by the main application to give a reference of itself.
	 * 
	 * @param budgetApp
	 */
	public void setMainApp(BudgetManager budgetManager) {
		this.budgetManager = budgetManager;
		
		// Add observable list data to the table.
		transactionTable.setItems(budgetManager.getTransactionData());
	}
	
	@FXML
	public void savingsCalcMonths(){
		String description = savingsCalDescription.getText();
		double total = 0;
		double allotment = 1;
		
	    total = Double.parseDouble(savingsCalGoal.getText());
		allotment = Double.parseDouble(savingsCalAllotment.getText());	
	
		if(allotment > 0.00 && total > 0.00){
			Double temp = new Double(Math.ceil(total / allotment));
		    int numMonths = temp.intValue();
		    String result = "It will take you " + numMonths + " months to meet your savings goal of $" + total + ".";
		    savingsCalResult.setText(result);
		}
		
	}
}