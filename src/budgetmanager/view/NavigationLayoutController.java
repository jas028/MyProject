package budgetmanager.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import budgetmanager.BudgetManager;
import budgetmanager.model.Debt;
import budgetmanager.model.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;

public class NavigationLayoutController {
	
	@FXML
	private TableView<Transaction> transactionTable;
	@FXML
	private TableColumn<Transaction, Number> expenseColumn;
	@FXML
	private TableColumn<Transaction, LocalDate> dateColumn;
	@FXML
	private TableColumn<Transaction, String> descriptionColumn;
	
	//--- Savings Calculator Variables
	@FXML
	private TextField savingsCalGoal;
	@FXML
	private TextField savingsCalAllotment;
	@FXML
	private Label calcResultLabel;

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
	private TableView<Debt> debtCalTable;
	@FXML
	private TableColumn<Debt, String> debtColName;
	@FXML
	private TableColumn<Debt, Number> debtColBalance;
	@FXML
	private TableColumn<Debt, Number> debtColPayment;
	@FXML
	private TableColumn<Debt, Number> debtColRate;
	
	// Overview Variables
	@FXML
	private Label loginNameLabel;
	@FXML
	private Label totalIncomeLabel;
	@FXML
	private Label totalExpendituresLabel;
	@FXML
	private Label netLabel;
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;
	
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
		expenseColumn.setCellValueFactory(cellData -> cellData.getValue().getValueProperty());
		
		// Custom rendering of the date column table cell.
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");
		dateColumn.setCellFactory(column -> {
		    return new TableCell<Transaction, LocalDate>() {
		        @Override
		        protected void updateItem(LocalDate item, boolean empty) {
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
						
						// Color expenses red and incomes green
						if(item.doubleValue() < 0) {
							setTextFill(Color.RED);
						}
						else {
							setTextFill(Color.GREEN);
						}
					}
				}
			};
		});
		
		// Initialize the debt table with data.
		debtColName.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
		debtColBalance.setCellValueFactory(cellData -> cellData.getValue().getBalanceProperty());
		debtColPayment.setCellValueFactory(cellData -> cellData.getValue().getPaymentProperty());
		debtColRate.setCellValueFactory(cellData -> cellData.getValue().getRateProperty());
		
		// Custom rendering of the expense column table cell.
		debtColBalance.setCellFactory(column -> {
			return new TableCell<Debt, Number>() {
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

		// Custom rendering of the expense column table cell.
		debtColPayment.setCellFactory(column -> {
			return new TableCell<Debt, Number>() {
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

		// Custom rendering of the expense column table cell.
		NumberFormat rateFormatter = new DecimalFormat("###,##0.000");
		debtColRate.setCellFactory(column -> {
			return new TableCell<Debt, Number>() {
				@Override
				protected void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						// Format value.
						setText(rateFormatter.format(item)+"%");
					}
				}
			};
		});
	}
	
	@FXML
	public void handleOverviewDatePicker() {
		try {
			
			budgetManager.calcOverviewTotals(startDatePicker.getValue(), endDatePicker.getValue());
			totalIncomeLabel.setText(Double.toString(budgetManager.overviewTotals.getIncomeTotal()));
			totalExpendituresLabel.setText(Double.toString(budgetManager.overviewTotals.getExpenseTotal()));
			netLabel.setText(Double.toString(budgetManager.overviewTotals.getNetTotal()));
			
		} catch(NullPointerException e) {
			
		}
	}
	
	/**
	 * Called when the user adds a transaction.
	 */
	@FXML
	public void handleAddTransaction() {
		TransactionEditDialogController dialog = new TransactionEditDialogController();
		dialog.setMainApp(budgetManager);
		dialog.showAndWait();
	}
	
	/**
	 * Called when the user edits a transaction.
	 */
	@FXML
	public void handleEditTransaction() {
		int selectedIndex = transactionTable.getSelectionModel().getSelectedIndex();
		if(selectedIndex >= 0) {
			TransactionEditDialogController dialog = new TransactionEditDialogController();
			dialog.setMainApp(budgetManager);
			dialog.setSelectedTransaction(transactionTable.getItems().get(selectedIndex));
			dialog.disableTypeChoice();
			dialog.showAndWait();
			dialog.enableTypeChoice();
		}
		else {
			// Nothing selected
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Transaction Edit Error");
			alert.setContentText("You must select a transaction to edit");
			
			alert.showAndWait();
		}
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
			// Nothing selected
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Transaction Delete Error");
			alert.setContentText("You must select a transaction to delete");
			
			alert.showAndWait();
		}
	}
	
	/**
	 * Called by the main application to give a reference of itself.
	 * 
	 * @param budgetApp
	 */
	public void setMainApp(BudgetManager budgetManager) {
		this.budgetManager = budgetManager;
		
		// Add observable list data to the tables.
		transactionTable.setItems(budgetManager.getTransactionData());
		debtCalTable.setItems(budgetManager.getDebtData());
	}
	
	/**
	 * Calculates months required to reach savings goal.
	 */
	@FXML
	public void handleCalcSavingsGoal() {
		calcResultLabel.setText(null);
	    try {
			double total = Double.parseDouble(savingsCalGoal.getText());
			double allotment = Double.parseDouble(savingsCalAllotment.getText());	

			if(allotment > 0.00 && total > 0.00) {
				Double temp = new Double(Math.ceil(total / allotment));
			    int numMonths = temp.intValue();
			    NumberFormat numberFormatter = new DecimalFormat("$###,###.00");
			    calcResultLabel.setText("It will take you " + numMonths + " months to meet your savings goal of " + numberFormatter.format(total));
			}
			else {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Savings Calculation Error");
			alert.setContentText("You must provide valid numeric values");
			
			alert.showAndWait();
		} catch(NullPointerException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Savings Calculation Error");
			alert.setContentText("You must complete all fields");
			
			alert.showAndWait();
		}
	}
	
	@FXML
	public void handleAddSavingsTransaction() {
		try {
			TransactionEditDialogController dialog = new TransactionEditDialogController();
			dialog.setMainApp(budgetManager);
			dialog.setSavingsCalcInfo(Double.parseDouble(savingsCalAllotment.getText()), true);
			dialog.disableTypeChoice();
			dialog.showAndWait();
			dialog.enableTypeChoice();
		} catch (NullPointerException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Savings Calculation Error");
			alert.setContentText("You must complete all fields");
			
			alert.showAndWait();
		}
	}
	
	@FXML
	public void handleAddDebt() {
		Double debtBalance, debtPayment, debtRate;
		
		try {
			debtBalance = Double.parseDouble(debtCalBalance.getText());
			if(debtBalance <= 0) {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Debt Tracker Error");
			alert.setContentText("Debt balances must be nonnegative monetary values");
			
			alert.showAndWait();
			return;
		}
		
		try {
			debtPayment = Double.parseDouble(debtCalPayment.getText());
			if(debtPayment <= 0) {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Debt Tracker Error");
			alert.setContentText("Debt payments must be nonnegative monetary values");
			
			alert.showAndWait();
			return;
		}
		
		try {
			debtRate = Double.parseDouble(debtCalRate.getText());
			if(debtRate <= 0) {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Debt Tracker Error");
			alert.setContentText("Debt rates must be nonnegative values");
			
			alert.showAndWait();
			return;
		}
		
		budgetManager.addDebt(new Debt(debtCalName.getText(), debtRate, debtBalance, debtPayment));
		debtCalName.clear();
		debtCalRate.clear();
		debtCalBalance.clear();
		debtCalPayment.clear();
	}
	
	@FXML
	public void handleDeleteDebt() {
		int selectedIndex = debtCalTable.getSelectionModel().getSelectedIndex();
		if(selectedIndex >= 0) {
			debtCalTable.getItems().remove(selectedIndex);
		}
		else {
			// Nothing selected
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Debt Delete Error");
			alert.setContentText("You must select a debt to delete");
			
			alert.showAndWait();
		}
	}
	
	@FXML
	public void handleDebtPayoffCalc() {
		// TODO: Debt calculations?
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText("Debt Payoff Calculation");
		alert.setContentText("This feature coming is coming soon");
		
		alert.showAndWait();
	}
}
