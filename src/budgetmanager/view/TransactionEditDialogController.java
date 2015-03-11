package budgetmanager.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import budgetmanager.BudgetManager;
import budgetmanager.model.*;
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
		}
		else if(selectedTransaction instanceof Income) {
			typeChoice.getSelectionModel().selectLast();
		}
		
		reoccuringCheckBox.setSelected(selectedTransaction.getReoccuring());
		
		valueField.setText(Double.toString(selectedTransaction.getValue()));
		
		descriptionField.setText(selectedTransaction.getDescription());
		
		datePicker.setValue(selectedTransaction.getDate());
	}
	
	public void disableTypeChoice() {
		typeChoice.setDisable(true);
	}

	public void enableTypeChoice() {
		typeChoice.setDisable(false);
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
