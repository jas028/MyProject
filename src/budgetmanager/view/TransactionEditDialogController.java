package budgetmanager.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import budgetmanager.BudgetManager;
import budgetmanager.model.*;
import budgetmanager.util.ExpenseCategory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class TransactionEditDialogController extends Stage implements Initializable {
	
	@FXML
	private ChoiceBox<String> typeChoice;
	@FXML
	private ChoiceBox<String> categoryChoice;
	@FXML
	private TextField valueField;
	@FXML
	private TextField descriptionField;
	@FXML
	private CheckBox reoccuringCheckBox;
	@FXML
	private DatePicker datePicker;
	
	// Reference to main app.
	private BudgetManager budgetManager;
	
	private Transaction selectedTransaction;
	
	public TransactionEditDialogController() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TransactionEditDialog.fxml"));
		fxmlLoader.setController(this);
		
		try {
			setScene(new Scene((Parent) fxmlLoader.load()));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void handleOKButton() {
		try {
			// Data validation
			Double value = Double.parseDouble(valueField.getText());
			if(datePicker.getValue() == null) {
				throw new NullPointerException();
			}
			
			if(selectedTransaction == null) {
				// Add new transaction
				if (typeChoice.getValue().matches("Expense")) {
					if(value > 0) {
						value = 0-value;
					}
					Expense transaction = new Expense(value,
							datePicker.getValue(), descriptionField.getText(),
							reoccuringCheckBox.isSelected(), null);
					if(categoryChoice.getValue().matches("Housing")) {
						transaction.setCategory(ExpenseCategory.HOUSING);
					} else if(categoryChoice.getValue().matches("Bill")) {
						transaction.setCategory(ExpenseCategory.BILL);
					} else if(categoryChoice.getValue().matches("Recreation")) {
						transaction.setCategory(ExpenseCategory.RECREATION);
					} else if(categoryChoice.getValue().matches("Savings")) {
						transaction.setCategory(ExpenseCategory.SAVINGS);
					} else if(categoryChoice.getValue().matches("Food")) {
						transaction.setCategory(ExpenseCategory.FOOD);
					} else if(categoryChoice.getValue().matches("Miscellaneous")) {
						transaction.setCategory(ExpenseCategory.MISCELLANEOUS);
					}
					budgetManager.addTransaction(transaction);
				} else if (typeChoice.getValue().matches("Income")) {
					if(value < 0) {
						value = 0-value;
					}
					Income transaction = new Income(value,
							datePicker.getValue(), descriptionField.getText(),
							reoccuringCheckBox.isSelected(), null);
					budgetManager.addTransaction(transaction);
				}
			}
			else {
				// Correct negative values
				if(this.selectedTransaction instanceof Expense) {
					if(typeChoice.getValue().matches("Income")) {
						throw new ClassCastException();
					}
					if(value > 0) {
						value = 0-value;
					}
					
					// Edit selected Expense
					if(categoryChoice.getValue().matches("Housing")) {
						((Expense) this.selectedTransaction).setCategory(ExpenseCategory.HOUSING);
					} else if(categoryChoice.getValue().matches("Bill")) {
						((Expense) this.selectedTransaction).setCategory(ExpenseCategory.BILL);
					} else if(categoryChoice.getValue().matches("Recreation")) {
						((Expense) this.selectedTransaction).setCategory(ExpenseCategory.RECREATION);
					} else if(categoryChoice.getValue().matches("Savings")) {
						((Expense) this.selectedTransaction).setCategory(ExpenseCategory.SAVINGS);
					} else if(categoryChoice.getValue().matches("Food")) {
						((Expense) this.selectedTransaction).setCategory(ExpenseCategory.FOOD);
					} else if(categoryChoice.getValue().matches("Miscellaneous")) {
						((Expense) this.selectedTransaction).setCategory(ExpenseCategory.MISCELLANEOUS);
					}
				}
				else {
					if(typeChoice.getValue().matches("Expense")) {
						throw new ClassCastException();
					}
					if(value < 0) {
						value = 0-value;
					}
				}
				
				// Edit selected transaction
				this.selectedTransaction.setDate(datePicker.getValue());
				this.selectedTransaction.setDescription(descriptionField.getText());
				this.selectedTransaction.setReoccuring(reoccuringCheckBox.isSelected());
				this.selectedTransaction.setValue(value);
			}
			
			close();
		} catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Transaction Add/Edit Error");
			alert.setContentText("Please enter a numeric monetary value");
			
			alert.showAndWait();
		} catch(NullPointerException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Transaction Add/Edit Error");
			alert.setContentText("You must complete all forms");
			
			alert.showAndWait();
		} catch(ClassCastException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Transaction Edit Error");
			alert.setContentText("Cannot change transaction types");
			
			alert.showAndWait();
		}
	}
	
	@FXML
	public void handleCancelButton() {
		close();
	}
	
	public void setMainApp(BudgetManager budgetManager) {
		this.budgetManager = budgetManager;
	}
	
	/**
	 * Sets the selected transaction object and calls for initialization of fields.
	 */
	public void setSelectedTransaction(Transaction transaction) {
		this.selectedTransaction = transaction;
		initFields();
	}
	
	public void initFields() {
		if(selectedTransaction instanceof Expense) {
			typeChoice.getSelectionModel().selectFirst();
			handleTypeSelect();
			
			if(((Expense) this.selectedTransaction).getCategory() == ExpenseCategory.HOUSING) {
				categoryChoice.getSelectionModel().select("Housing");
			} else if(((Expense) this.selectedTransaction).getCategory() == ExpenseCategory.BILL) {
				categoryChoice.getSelectionModel().select("Bill");
			} else if(((Expense) this.selectedTransaction).getCategory() == ExpenseCategory.RECREATION) {
				categoryChoice.getSelectionModel().select("Recreation");
			} else if(((Expense) this.selectedTransaction).getCategory() == ExpenseCategory.FOOD) {
				categoryChoice.getSelectionModel().select("Food");
			} else if(((Expense) this.selectedTransaction).getCategory() == ExpenseCategory.SAVINGS) {
				categoryChoice.getSelectionModel().select("Savings");
			} else if(((Expense) this.selectedTransaction).getCategory() == ExpenseCategory.MISCELLANEOUS) {
				categoryChoice.getSelectionModel().select("Miscellaneous");
			}
		}
		else if(selectedTransaction instanceof Income) {
			typeChoice.getSelectionModel().selectLast();
			disableCategoryChoice();
		}
		
		reoccuringCheckBox.setSelected(selectedTransaction.getReoccuring());
		
		descriptionField.setText(selectedTransaction.getDescription());
		
		datePicker.setValue(selectedTransaction.getDate());
		
		double value = selectedTransaction.getValue();
		if(value < 0) {
			valueField.setText(Double.toString(0-value));
		}
		else {
			valueField.setText(Double.toString(value));
		}
	}
	
	public void disableTypeChoice() {
		typeChoice.setDisable(true);
	}

	public void enableTypeChoice() {
		typeChoice.setDisable(false);
	}
	
	public void disableCategoryChoice() {
		categoryChoice.setDisable(true);
	}

	public void enableCategoryChoice() {
		categoryChoice.setDisable(false);
	}
	
	@FXML
	public void handleTypeSelect() {
		if(typeChoice.getValue() != null && typeChoice.getValue().equals("Expense")) {
			categoryChoice.getItems().clear();
			categoryChoice.getItems().addAll("Housing", "Bill", "Recreation", "Food", "Savings", "Miscellaneous");
		}
		else {
			categoryChoice.getItems().clear();
		}
	}
	
	public void setSavingsCalcInfo(Double value, boolean reoccuring) {
		typeChoice.getSelectionModel().selectFirst();
		
		valueField.setText(Double.toString(value));
		
		reoccuringCheckBox.setSelected(reoccuring);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		typeChoice.getItems().addAll("Expense", "Income");
	}
}
