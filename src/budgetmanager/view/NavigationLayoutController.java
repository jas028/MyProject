package budgetmanager.view;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import budgetmanager.BudgetManager;
import budgetmanager.model.Debt;
import budgetmanager.model.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class NavigationLayoutController {
	
	@FXML
	private TableView<Transaction> transactionTable;
	@FXML
	private TableColumn<Transaction, Number> expenseColumn;
	@FXML
	private TableColumn<Transaction, LocalDateTime> dateColumn;
	@FXML
	private TableColumn<Transaction, String> descriptionColumn;
	
	//--- Savings Calculator Variables
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

	//--- Debt Payoff Calculator Variables
	@FXML
	private TextField debtCalName;
	@FXML
	private TextField debtCalBalance;
	@FXML
	private TextField debtCalPayment;
	@FXML
	private TextField debtCalRate;
	@FXML
	private Button debtCalAddButton;
	@FXML
	private Button debtCalPayoffButton;
	@FXML
	private TableView<Debt> debtCalTableView;
	@FXML
	private TableColumn<Debt, String> debtColName;
	@FXML
	private TableColumn<Debt, Number> debtColBalance;
	@FXML
	private TableColumn<Debt, Number> debtColPayment;
	@FXML
	private TableColumn<Debt, Number> debtColRate;
	
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
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");
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
	 * Called when the user deletes a transaction.
	 */
	@FXML
	public void handleDeleteTransaction() {
		int selectedIndex = transactionTable.getSelectionModel().getSelectedIndex();
		if(selectedIndex >= 0) {
			transactionTable.getItems().remove(selectedIndex);
		}
		else {
			// TODO: Alert dialog box.
		}
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
	
	/**
	 * Calculates months required to reach savings goal.
	 */
	@FXML
	public void handleCalcSavingsGoal() {
		String result;
		
	    try {
	    	String description = savingsCalDescription.getText();
			double total = Double.parseDouble(savingsCalGoal.getText());
			double allotment = Double.parseDouble(savingsCalAllotment.getText());	

			if(allotment > 0.00 && total > 0.00) {
				Double temp = new Double(Math.ceil(total / allotment));
			    int numMonths = temp.intValue();
			    result = "It will take you " + numMonths + " months to meet your savings goal of $" + total + ".";
			    savingsCalResult.setText(result);
			}
			else {
				result = "Invalid value";
			}
			savingsCalResult.setText(result);
		} catch (NumberFormatException e) {
			result = "You must complete the required fields";
			savingsCalResult.setText(result);
		}
	}
	
	@FXML
	public void debtPayoffCalc(){
		String loanName = debtCalName.getText();
		double balance = 0;
		double rate = 0;
		double payment = 0;
		
		balance = Double.parseDouble(debtCalBalance.getText());
		rate = Double.parseDouble(debtCalRate.getText());
		payment = Double.parseDouble(debtCalPayment.getText());
		
		//ObservableList<Debt> debtData = FXCollections.observableArrayList();
		//debtData.add(new Debt(loanName, balance, rate, payment));

		//System.out.print(loanName + " " + balance + " " + rate + " " + payment);
		
		//transactionData.add(new Transaction(1.00, (LocalDateTime.now()), "transaction 1"));

	}
}
