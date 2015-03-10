package budgetmanager.model;

import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import budgetmanager.util.ExpenseCategory;

/**
 * Model class for an expense.
 */
public class Expense extends Transaction {
	
	private final ObjectProperty<ExpenseCategory> category;
	
	public Expense() {
		this(0.0, null, null, false, null);
	}
	
	public Expense(Double expense, LocalDateTime date, String description, boolean reoccuring, ExpenseCategory category) {
		super(expense, date, description, reoccuring);
		this.category = new SimpleObjectProperty<ExpenseCategory>(category);
	}
	
	public ExpenseCategory getCategory() {
		return category.get();
	}
	
	public void setCategory(ExpenseCategory category) {
		this.category.set(category);
	}
	
	public ObjectProperty<ExpenseCategory> getCategoryProperty() {
		return category;
	}
}
