package budgetmanager.model;

import java.time.LocalDateTime;

/**
 * Model class for income.
 */
public class Income extends Transaction {
	
	public Income() {
		this(0.0, null, null, false);
	}
	
	public Income(Double expense, LocalDateTime date, String description, boolean reoccuring) {
		super(expense, date, description, reoccuring);
	}
	
}
