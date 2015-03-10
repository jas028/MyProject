package budgetmanager.model;

import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import budgetmanager.util.IncomeCategory;

/**
 * Model class for income.
 */
public class Income extends Transaction {
	
	private final ObjectProperty<IncomeCategory> category;
	
	public Income() {
		this(0.0, null, null, false, null);
	}
	
	public Income(Double expense, LocalDateTime date, String description, boolean reoccuring, IncomeCategory category) {
		super(expense, date, description, reoccuring);
		this.category = new SimpleObjectProperty<IncomeCategory>(category);
	}
	
	public IncomeCategory getCategory() {
		return category.get();
	}
	
	public void setCategory(IncomeCategory category) {
		this.category.set(category);
	}
	
	public ObjectProperty<IncomeCategory> getCategoryProperty() {
		return category;
	}
}
