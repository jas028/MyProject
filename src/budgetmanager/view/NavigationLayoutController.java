package budgetmanager.view;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import com.jcraft.jsch.JSchException;

import budgetmanager.BudgetManager;
import budgetmanager.model.Debt;
import budgetmanager.model.DebtLog;
import budgetmanager.model.DebtLogSummary;
import budgetmanager.model.Transaction;
import budgetmanager.model.User;
import budgetmanager.mysql.MySqlConnect;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NavigationLayoutController {
	
	@FXML
	private TableView<Transaction> transactionTable;
	@FXML
	private TableColumn<Transaction, Number> expenseColumn;
	@FXML
	private TableColumn<Transaction, LocalDate> dateColumn;
	@FXML
	private TableColumn<Transaction, String> descriptionColumn;
	
	//--- Change Password Variables
	@FXML
	private TextField currentPassword;
	@FXML
	private TextField newPassword;
	@FXML
	private TextField confirmNewPassword;
	
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
	private TextField debtPoolField;
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
	@FXML
	private PieChart overviewChart;
	
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
		NumberFormat numberFormatter = new DecimalFormat("$###,##0.00");
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
						setText(rateFormatter.format(item.doubleValue()*100)+"%");
					}
				}
			};
		});
		
		// Set default start and end dates for overview tab 
		startDatePicker.setValue(LocalDate.now());
		endDatePicker.setValue(startDatePicker.getValue().plusMonths(1));
	}
	
	@FXML
	public void handleOverviewDatePicker() {
		try {
			
			// Set email
			if(loginNameLabel.getText().equals("")) {
				loginNameLabel.setText(budgetManager.emailPassword.getKey());
			}
			
			// Clear old fields
			totalIncomeLabel.setText(null);
			totalExpendituresLabel.setText(null);
			netLabel.setText(null);
			
			// Calculate totals needed for overview fields
			budgetManager.calcOverviewTotals(startDatePicker.getValue(), endDatePicker.getValue());
			
			// Set and format
			NumberFormat numberFormatter = new DecimalFormat("$###,##0.00");
			totalIncomeLabel.setText(numberFormatter.format(budgetManager.overviewTotals.getIncomeTotal()));
			totalExpendituresLabel.setText(numberFormatter.format(budgetManager.overviewTotals.getExpenseTotal()));
			netLabel.setText(numberFormatter.format(budgetManager.overviewTotals.getNetTotal()));
			
			// Update pie chart
			overviewChart.setData(budgetManager.overviewChartData);
		} catch(NullPointerException e) {
			
		} catch(Exception e) {
			// Invalid date range
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Date Range Error");
			alert.setContentText("You must select a valid date range");

			alert.showAndWait();
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
	
	public void handleChangePassword() throws JSchException{
		User user = budgetManager.user;
		try{
			if(newPassword.getText().equals(confirmNewPassword.getText()) == false){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error in change password");
				alert.setHeaderText("New passwords do not match");
				alert.setContentText("New password fields do not match");
				
				alert.showAndWait();
			}else{
				MySqlConnect sql = new MySqlConnect();
				if(sql.validUser(user.getEmail(), currentPassword.getText())){
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Confirm Change Password");
					alert.setHeaderText("Password will be changed");
					alert.setContentText("Are you sure?");
					
					Optional <ButtonType> result = alert.showAndWait();
					if(result.get() == ButtonType.OK){
						sql.changePassword(user, newPassword.getText());
					}
				}else{
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Incorrect Password");
					alert.setHeaderText("Incorrect Password");
					alert.setContentText("User Password Inavlid");
					
					alert.showAndWait();
				}
				
				sql.MySQLDisconnect();
			}
			
		}catch(Exception e){
			System.out.println("Error in handleChangePassword: " + e);
		}
	}
	/**
	 * Called when user deletes their account
	 * @throws Exception 
	 * @throws SQLException 
	 */
	@FXML
	public void handleDeleteUser() throws Exception{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm User Delete");
		alert.setHeaderText("User account will be deleted");
		alert.setContentText("Are you sure you want to delete this account?");
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == ButtonType.OK){
			MySqlConnect sql = new MySqlConnect();
			User user = budgetManager.user;
			
			sql.deleteUser(user);
			sql.MySQLDisconnect();
			
			alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("User Deleted");
			alert.setHeaderText(null);
			alert.setContentText("User has been deleted\n");
			alert.showAndWait();
			
			System.exit(0);
			sql.MySQLDisconnect();
		}else{
			alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Deletion Cancelled");
			alert.setHeaderText(null);
			alert.setContentText("The account was not deleted");
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
			debtRate = debtRate/100;
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
		try {
			Double pool = Double.parseDouble(debtPoolField.getText());

			ArrayList<DebtLogSummary> debtLogSummaryList = new ArrayList<DebtLogSummary>();
			
			if(!budgetManager.getDebtSummary(debtLogSummaryList, pool)) {
				throw new NumberFormatException();
			}
			
			// Create line chart dialog
			final NumberAxis xAxis = new NumberAxis();
		    final NumberAxis yAxis = new NumberAxis();
		    xAxis.setLabel("Months");
		    yAxis.setLabel("Principle remaining");
		    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		    lineChart.setTitle("Principle/Month");
		    
		    Scene scene = new Scene(lineChart, 800, 600);
		    
		    for(DebtLogSummary debtLogSummary : debtLogSummaryList) {
			    XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
			    series.setName(debtLogSummary.getDebt().getName());
			    for(DebtLog debtLog : debtLogSummary.getDebtLogSummary()) {
			    	series.getData().add(new XYChart.Data<Number, Number>(debtLog.getMonth(), debtLog.getPrinciple()));
			    }
			    lineChart.getData().add(series);
		    }

		    Stage lineChartDialog = new Stage();
		    lineChartDialog.initStyle(StageStyle.UTILITY);
		    lineChartDialog.setScene(scene);
		    lineChartDialog.show();
			
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Debt Calculate Error");
			alert.setContentText("Debt payment pool values must be monetary values greater than or equal to the sum of all minimum payment values");
			
			alert.showAndWait();
		}
	}
}
