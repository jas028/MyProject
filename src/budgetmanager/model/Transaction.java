package budgetmanager.model;

import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	private final BooleanProperty reoccuring;
	
	public Transaction() {
		this(0.00, null, null, false);
	}
	
	/**
	 * Constructor with initial data.
	 * 
	 * @param date
	 * @param expense
	 * @param description
	 */
	public Transaction(Double expense, LocalDateTime date, String description, boolean reoccuring) {
		this.expense = new SimpleDoubleProperty(expense);
		this.date = new SimpleObjectProperty<LocalDateTime>(date);
		this.description = new SimpleStringProperty(description);
		this.reoccuring = new SimpleBooleanProperty(reoccuring);
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
	
	public boolean getReoccuring() {
		return reoccuring.get();
	}
	
	public void setReoccuring(boolean reoccuring) {
		this.reoccuring.set(reoccuring);
	}
	
	public BooleanProperty getReoccuringProperty() {
		return reoccuring;
	}
}
