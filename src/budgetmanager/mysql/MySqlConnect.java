package budgetmanager.mysql;
import java.sql.*;
import java.util.ArrayList;

//import com.mysql.jdbc.Util;

import budgetmanager.model.*;

public class MySqlConnect {
	
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	
	//constructor to make connection to database
	public MySqlConnect() throws Exception{
		try{
			//uses jar file to make connection to database;
			//connect to the server
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/my_database";
			String user = "root";
			String password = "Wrigley1!";
			Class.forName(driver);
			//Right now this has no database in it. I've emailed the server admin to figure out why the packet is too big
			//When I just try to connect with no other information.
			con = DriverManager.getConnection(url,user,password);
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
			PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS users(email varchar(255), "
					+ "pass_word varchar(255))");
			create.executeUpdate();
			String query = "INSERT INTO users(email, pass_word)"
					+ " values(?, ?)";

			//set insert variables
			pst = con.prepareStatement(query);
			pst.setString(1, user.getEmail());
			pst.setString(2, user.getPass_word());

			//execute statement
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
		finally{
			System.out.println("New user inserted");
		}
	}
	
	//insert a new loan
	public void insertLoan(String email, Debt debt) throws Exception{
		try{
			pst = con.prepareStatement("CREATE TABLE IF NOT EXISTS loans(email varchar(255), "
					+ "name varchar(255), balance double, rate double, payment double)");
			//set MySql insert statement
			pst.executeUpdate();
			
			String query = "INSERT INTO loans(email, name, balance, rate, payment)"
					+ " VALUES(?, ?, ?, ?, ?)";
			
			//set insert variables
			pst = con.prepareStatement(query);
			pst.setString (1, email);
			pst.setString (2, debt.getName());
			pst.setDouble (3, debt.getBalance());
			pst.setDouble (4, debt.getRate());
			pst.setDouble (5, debt.getPayment());

			//execute statement
			pst.executeUpdate();

		}catch(Exception ex){System.out.println("Error: " + ex);}
		finally{
			System.out.println("New loan inserted");
		}
	}
	
	//insert a new transaction
	public void insertIncome(String email, Income income) throws Exception{
		java.util.Date utilDate = new java.util.Date();
	    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		try{
			pst = con.prepareStatement("CREATE TABLE IF NOT EXISTS transactions(email varchar(255), "
					+ "amount double, date date, description varchar(255), reoccuring boolean)");
			//create new table if it doesn't exist
			pst.executeUpdate();
			
			String query = "INSERT INTO transactions(email, amount, date, description, reoccuring)"
					+ "VALUES(?,?,?,?,?)";
			
			//set insert variables
			pst = con.prepareStatement(query);
			pst.setString (1, email);
			pst.setDouble (2, income.getValue());
			pst.setDate (3, sqlDate);
			pst.setString   (4, income.getDescription());
			pst.setBoolean (5, income.getReoccuring());

			//execute statement
			pst.executeUpdate();

		}catch(Exception ex){System.out.println("Error: " + ex);}
		finally{
			System.out.println("New expense inserted");
		}		
	}
	
	public void insertExpense(String email, Expense expense){
		java.util.Date utilDate = new java.util.Date();
	    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		try{
			pst = con.prepareStatement("CREATE TABLE IF NOT EXISTS transactions(email varchar(255), "
					+ "amount double, date date, description varchar(255), reoccuring boolean)");
			pst.executeUpdate();
			//removed date
			String query = "INSERT INTO transactions(email, amount, date, description, reoccuring)"
					+ "VALUES(?,?,?,?,?)";
			pst = con.prepareStatement(query);
			pst.setString(1,email);
			pst.setDouble(2,expense.getValue());
			pst.setDate(3, sqlDate);
			pst.setString(4, expense.getDescription());
			pst.setBoolean(5, expense.getReoccuring());
			pst.execute();
		}catch(Exception ex){
			System.out.println("Error: " + ex);
		}finally{
			System.out.println("new expense inserted");
		}
	}
	//find if user is in the system
	public Boolean validUser(String email, String pass_word) throws Exception{
		try{
			//MySql select statement
			String query = "SELECT * FROM users WHERE email = ? AND pass_word = ?";
			
			//set variables
			pst = con.prepareStatement(query);
			pst.setString(1, email);
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
	public User selectUser(String email, String pass_word) throws Exception{
		User user = new User();
		try{
			//MySql select statement with username condition
			//Changed this to a PreparedStatement because of SQLException
			//pst is prepared statement
			pst = con.prepareStatement("SELECT * FROM users WHERE email = ? and pass_word = ?");
			
			//set variables
			pst.setString(1, email);
			pst.setString(2, pass_word);
			
			//Execute PreparedStatement to get all user info.
			rs = pst.executeQuery();
			
			//Loop to get all relevant data from table if it exists
			//Returns user upon completion
			while(rs.next() != false){
				System.out.print(rs.getString("email") + " ");
				user.setEmail(rs.getString("email"));
				System.out.print(rs.getString("pass_word") + " ");
				user.setPass_word(rs.getString("pass_word"));
				return user;
			}
			//If email or password are incorrect user is informed
			System.out.print("invalid username and/or password");
			return null;
		}catch(SQLException ex){
			System.out.println(ex);
			return null;
		}
	}
	
	//get list of all debts from user
	public ArrayList<Debt> selectLoan(String email) throws Exception{
		ArrayList<Debt> debtList = new ArrayList<Debt>();
		Debt debt = new Debt();
		try{
			pst = con.prepareStatement("SELECT * FROM loans WHERE email = ?");
			pst.setString(1, email);
			rs = pst.executeQuery();
			
			while(rs.next() != false){
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
	public ArrayList<Expense> selectExpense(String email) throws Exception{
		ArrayList<Expense> expenseList = new ArrayList<Expense>();
		Expense expense = new Expense();
		try{
			pst = con.prepareStatement("SELECT * FROM transactions WHERE email = ?");
			pst.setString(1, email);
			rs = pst.executeQuery();
			
			while(rs.next() != false){
				System.out.print(rs.getDate("date").toLocalDate());
				expense.setValue(rs.getDouble("amount"));
				expense.setDate(rs.getDate("date").toLocalDate());
				expense.setDescription(rs.getString("description"));
				expense.setReoccuring(rs.getBoolean("reoccuring"));
				expenseList.add(expense);
			}
			
			return expenseList;
			
		}catch(Exception ex){
			System.out.println("Error: " + ex);
		}
		return null;
	}
	
	public ArrayList<Income> selectIncome(String email) throws Exception{
		ArrayList<Income> incomeList = new ArrayList<Income>();
		Income income = new Income();
		try{
			pst = con.prepareStatement("SELECT * FROM transactions WHERE email = ?");
			pst.setString(1, email);
			rs = pst.executeQuery();
			
			while(rs.next() != false){
				System.out.print(rs.getDate("date").toLocalDate());
				income.setValue(rs.getDouble("amount"));
				income.setDate(rs.getDate("date").toLocalDate());
				income.setDescription(rs.getString("description"));
				income.setReoccuring(rs.getBoolean("reoccuring"));
				incomeList.add(income);
			}
			return incomeList;
		}catch(Exception ex){
			System.out.println("Error: " + ex);
		}
		return null;
	}
	
	//delete a user and all its information
	public void deleteUser(String email) throws Exception{
		try{
			String query = "DELETE FROM users WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, email);
			
			pst.executeUpdate();
			
			query = "DELETE FROM transactions WHERE user_name = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, email);
			
			pst.executeUpdate();
			
			query = "DELETE FROM loans WHERE user_name = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, email);
			
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}	
	}
	
	//delete a single income
	public void deleteIncome(String email, Income income) throws Exception{
		java.util.Date utilDate = new java.util.Date();
	    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		try{
			String query = "DELETE FROM transactions WHERE email = ? and description = ?";
			pst = con.prepareStatement(query);
			pst.setString(1,email);
			pst.setString(2, income.getDescription());
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}	
	}
	//delete a single expense
	public void deleteExpense(String email, Expense expense) throws Exception{
		java.util.Date utilDate = new java.util.Date();
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		try{
			String query = "DELETE FROM transactions WHERE email = ? and description = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, email);
			pst.setString(2, expense.getDescription());
			pst.executeUpdate();
		}catch(Exception ex){
			System.out.println("Error: " + ex);
		}
	}
	
	//delete a single loan using the primary key
	public void deleteLoans(String email, Debt debt) throws Exception{
		try{
			String query = "DELETE FROM loans WHERE email = ?";// and name = ?";
			pst = con.prepareStatement(query);
			pst.setString(1,email);
			//pst.setString(2, debt.getName());
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
