package budgetmanager.model;

import java.time.LocalDateTime;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a transaction.
 */
public class Transaction {

	private final DoubleProperty expense;
	private final ObjectProperty<LocalDateTime> date;
	private final StringProperty description;
	
	public Transaction() {
		this(0.00, null, null);
	}
	
	/**
	 * Constructor with initial data.
	 * 
	 * @param date
	 * @param expense
	 * @param description
	 */
	public Transaction(Double expense, LocalDateTime date, String description) {
		this.expense = new SimpleDoubleProperty(expense);
		this.date = new SimpleObjectProperty<LocalDateTime>(date);
		this.description = new SimpleStringProperty(description);
	}
	
	public Double getExpense() {
		return expense.get();
	}
	
	public void setExpense(Double expense) {
		this.expense.set(expense);
	}
	
	public DoubleProperty getExpenseProperty() {
		return expense;
	}
	
	public LocalDateTime getDate() {
		return date.get();
	}
	
	public void setDate(LocalDateTime date) {
		this.date.set(date);
	}
	
	public ObjectProperty<LocalDateTime> getDateProperty() {
		return date;
	}
	
	public String getDescription() {
		return description.get();
	}
	
	public void setDescription(String description) {
		this.description.set(description);
	}
	
	public StringProperty getDescriptionProperty() {
		return description;
	}
}
