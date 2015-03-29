package budgetmanager.mysql;
import java.sql.*;
import java.util.ArrayList;

import budgetmanager.model.*;

public class MySqlConnect {
	
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	
	//constructor to make connection to database
	public MySqlConnect() throws Exception{
		try{
			//uses jar file to make connection to database
			Class.forName("com.mysql.jdbc.Driver");
			//connect to the server
			con = DriverManager.getConnection("jdbc:mysql://localhost::3306/financialsystem", "root", "Tywin0002");
			
		//if exception
		}catch(Exception ex){System.out.println("Error: " + ex);}
		finally{
			System.out.println("Connected to database");
		}
	}
	
	//insert a new user
	public void insertUser(User user) throws Exception{
		try{
			//set MySql insert statement
			String query = "INSERT INTO users(f_name, l_name, email, user_name, pass_word)"
					+ " values(?, ?, ?, ?, ?)";

			//set insert variables
			pst = con.prepareStatement(query);
			pst.setString(1, user.getF_name());
			pst.setString(2, user.getL_name());
			pst.setString(3, user.getEmail());
			pst.setString(4, user.getUser_name());
			pst.setString(5, user.getPass_word());

			//execute statement
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
		finally{
			System.out.println("New user inserted");
		}
	}
	
	//insert a new loan
	public void insertLoan(String user_name, Debt debt) throws Exception{
		//generate unique primary key
		int id = hashTable(user_name, debt.getName());
		
		try{
			//set MySql insert statement
			String query = "INSERT INTO loans(id, user_name, name, balance, rate, payment)"
					+ " VALUES(?, ?, ?, ?, ?, ?)";
			
			//set insert variables
			pst = con.prepareStatement(query);
			pst.setInt    (1, id);
			pst.setString (2, user_name);
			pst.setString (3, debt.getName());
			pst.setDouble (4, debt.getBalance());
			pst.setDouble (5, debt.getRate());
			pst.setDouble (6, debt.getPayment());

			//execute statement
			pst.execute();

		}catch(Exception ex){System.out.println("Error: " + ex);}
		finally{
			System.out.println("New loan inserted");
		}
	}
	
	//insert a new transaction
	public void insertTransaction(String user_name, Transaction transaction) throws Exception{
		//generate unique primary key
		int id = hashTable(user_name, transaction.getValue().toString());
		
		try{
			//set MySql insert statement
			String query = "INSERT INTO transactions(id, user_name, amount, date, description, reoccuring)"
					+ " VALUES(?, ?, ?, ?, ?, ?)";
			
			//set insert variables
			pst = con.prepareStatement(query);
			pst.setInt    (1, id);
			pst.setString (2, user_name);
			pst.setDouble (3, transaction.getValue());
			pst.setDate   (4, Date.valueOf(transaction.getDate()));
			pst.setString (5, transaction.getDescription());
			pst.setBoolean(6, transaction.getReoccuring());

			//execute statement
			pst.execute();

		}catch(Exception ex){System.out.println("Error: " + ex);}
		finally{
			System.out.println("New expense inserted");
		}		
	}
	
	//find if user is in the system
	public Boolean validUser(String user_name, String pass_word) throws Exception{
		try{
			//MySql select statement
			String query = "SELECT * FROM users WHERE user_name = ? AND pass_word = ?";
			
			//set variables
			pst = con.prepareStatement(query);
			pst.setString(1, user_name);
			pst.setString(2, pass_word);
			
			//execute select statement
			rs = pst.executeQuery();
			
			//find is there is a match
			if(rs.next())
				return true;
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
		return false;
	}
	
	//get all info for a user
	public User selectUser(String user_name) throws Exception{
		User user = new User();
		try{
			//MySql select statement with username condition
			String query = "SELECT * FROM users WHERE user_name = ?";
			
			//set variables
			pst = con.prepareStatement(query);
			pst.setString(1, user_name);
			
			//execute select statement
			rs = pst.executeQuery();
			
			//save user from the database
			user.setF_name(rs.getString("first_name"));
			user.setL_name(rs.getString("last_name"));
			user.setEmail(rs.getString("email"));
			user.setUser_name(rs.getString("user_name"));
			user.setPass_word(rs.getString("pass_word"));
			
			//return desired user
			return user;
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
		return null;
	}
	
	//get list of all debts from user
	public ArrayList<Debt> selectLoan(String user_name) throws Exception{
		ArrayList<Debt> debtList = new ArrayList<Debt>();
		Debt debt = new Debt();
		try{
			String query = "SELECT * FROM loans WHERE user_name = ?";
			
			pst = con.prepareStatement(query);
			pst.setString(1, user_name);
			
			rs = pst.executeQuery(query);
			
			while(rs.next()){
				debt.setName(rs.getString("name"));
				debt.setBalance(rs.getDouble("balance"));
				debt.setRate(rs.getDouble("rate"));
				debt.setPayment(rs.getDouble("payment"));
				
				debtList.add(debt);
			}

			return debtList;
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
		return null;
	}
	
	//get list of all transactions from user
	public ArrayList<Transaction> selectExpense(String user_name) throws Exception{
		ArrayList<Transaction> expenseList = new ArrayList<Transaction>();
		Transaction transaction = new Transaction();
		try{
			String query = "SELECT * FROM transactions WHERE user_name = ?";
			
			pst = con.prepareStatement(query);
			pst.setString(1, user_name);
			
			rs = pst.executeQuery(query);
			
			while(rs.next()){
				transaction.setValue(rs.getDouble("amount"));
				transaction.setDate(rs.getDate("date").toLocalDate());
				transaction.setDescription(rs.getString("description"));
				transaction.setReoccuring(rs.getBoolean("reoccuring"));
				
				expenseList.add(transaction);
			}
			
			return expenseList;
			
		}catch(Exception ex){
			System.out.println("Error: " + ex);
		}
		return null;
	}
	
	//delete a user and all its information
	public void deleteUser(String user_name) throws Exception{
		try{
			String query = "DELETE FROM users WHERE user_name = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user_name);
			
			pst.executeUpdate();
			
			query = "DELETE FROM transactions WHERE user_name = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user_name);
			
			pst.executeUpdate();
			
			query = "DELETE FROM loans WHERE user_name = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user_name);
			
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}	
	}
	
	//delete a single transaction using the primary key
	public void deleteTransaction(String user_name, Transaction transaction) throws Exception{
		int id = hashTable(user_name, transaction.getValue().toString());
		
		try{
			String query = "DELETE FROM transaction WHERE id = ?";
			pst = con.prepareStatement(query);
			pst.setInt(1, id);

			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}	
	}
	
	//delete a single loan using the primary key
	public void deleteLoans(String user_name, Debt debt) throws Exception{
		int id = hashTable(user_name, debt.getName());
		
		try{
			String query = "DELETE FROM loans WHERE id = ?";
			
			pst = con.prepareStatement(query);
			pst.setInt(1, id);
			
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}	
	}
	
	//generates a unique primary key
	private int hashTable(String str1, String str2){
		int x = 0;
		int y = 0;
		
		for(int i = 0; i < str1.length(); i++)
			x += str1.charAt(i);
		for(int i = 0; i < str2.length(); i++)
			y += str2.charAt(i);
		x = (x * y) % 1000;
		
		while(exist(x))
			x = rehash(y + 7, x + 13);
		
		return x;
		
	}
	
	//helper method to generate primary key
	private int rehash(int x, int y){
		return (x + y) % 1000;
	}
	
	//check if primary exists
	private Boolean exist(int id){
		try{
			String query = "SELECT COUNT(*) FROM users WHERE id = ?";
			pst = con.prepareStatement(query);
			pst.setInt(1, id);
			rs = pst.executeQuery();
			if(rs.next())
				return true;
		}catch(Exception ex){System.out.println("Error: " + ex);}
		
		return false;
	}
}
