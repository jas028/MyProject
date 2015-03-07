package budgetmanager.model;

import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import budgetmanager.util.Category;

/**
 * Model class for an expense.
 */
public class Expense extends Transaction {
	
	private final ObjectProperty<Category> category;
	
	public Expense() {
		this(0.0, null, null, false, null);
	}
	
	public Expense(Double expense, LocalDateTime date, String description, boolean reoccuring, Category category) {
		super(expense, date, description, reoccuring);
		this.category = new SimpleObjectProperty<Category>(category);
	}
	
	public Category getCategory() {
		return category.get();
	}
	
	public void setCategory(Category category) {
		this.category.set(category);
	}
	
	public ObjectProperty<Category> getCategoryProperty() {
		return category;
	}
}
