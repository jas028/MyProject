/**
 * Class that establishes a connection to the MySQL database on a University of Arkansas
 * server. A SSH client must be used to connect to the server, so a tunnel connection is
 * to access the database.
 * 
 * Class also contains the methods to make MySQL requests and disconnect from the server
 * and the database.
 */

//Packages
package budgetmanager.mysql;

//Libraries
import java.sql.*;
import budgetmanager.util.ExpenseCategory;
import budgetmanager.model.*;
import java.util.ArrayList;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class MySqlConnect {
	
	private Connection con;//sql connection variable
	private PreparedStatement pst;//sql request variable
	private ResultSet rs;//database results variable
	private Session session = null; //SSH session variable
	
	//***********************************************Server Connection*******************************************************
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Constructor to establish a new connection to database using a tunnel to SSH
	 * @throws JSchException
	 */
	public MySqlConnect() throws JSchException{
		//Information needed to connect to ssh comp.uark.edu
		String host = "comp.uark.edu";
		String remote_host = "localhost";
		int sshPort = 22;
		String user = "jmhernan";
		String password = "Tywin0002";
		
		//ports needed for portForwarding
		int local_port = 3306;
		int remote_port = 3306;
		
		//Information needed to connect to MySql on ssh server
		String dbUserName = "jmhernan";
		String dbPassword = "Group8";
		String serverURL = "jdbc:mysql://" + remote_host + ":" + remote_port + "/" + dbUserName;
		String driverName = "com.mysql.jdbc.Driver";
		
		try{
			System.out.println("Connecting to " + host + "...");
			
			//JSch object to create SSH session
			JSch jsch = new JSch();
			
			//create session to SSH client
			session = jsch.getSession(user, host, sshPort);
			
			//configuration for SSH client
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("ConnectionAttempts","3");
			
			//set SSH password, configuration
			session.setPassword(password);
			session.setConfig(config);
			
			//connect to SSH
			session.connect();
			System.out.println("Connected to server ssh " + host);
			
			//portForwarding
			System.out.println("Creating Tunnel: Forwarding local port:" + local_port + " to remote port:" + remote_port + "...");
			int assignedPort = session.setPortForwardingL(local_port, remote_host, remote_port);
			System.out.println("Tunnel created");
			
			//Connect to MySQL database
			System.out.println("Connecting to " + dbUserName + "@" + remote_host + ":" + assignedPort + "...");
			Class.forName(driverName);
			
			//connect to the server
			con = DriverManager.getConnection(serverURL, dbUserName, dbPassword);
			System.out.println("Connected to database " + dbUserName + "@" + remote_host);

		}catch(Exception ex){System.out.println("Error: " + ex);}
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Disconnects to the SSH server and MySQL database
	 * @throws SQLException
	 */
	public void MySQLDisconnect() throws SQLException, JSchException{
		try{
			//disconnect from SSH
			if(session.isConnected()){
				session.disconnect();
				System.out.println("Disconnected form SSH client");
			}
			
			//disconnect from MySQL
			if(!con.isClosed()){
				con.close();
				System.out.println("Disconnected from database");
			}
			
		}catch(Exception e){System.out.println("Error: " + e);}
	}
	
	//***********************************************INSERT Requests*********************************************************
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to INSERT a new user
	 * @param user
	 * @throws Exception
	 */
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
			System.out.println("New user inserted");
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to INSERT a Debt
	 * @param email
	 * @param debt
	 * @throws Exception
	 */
	public void insertDebt(User user, Debt debt) throws Exception{
		try{
			pst = con.prepareStatement("CREATE TABLE IF NOT EXISTS debts(email varchar(255), "
					+ "name varchar(255), balance double, rate double, payment double, password varchar(255))");
			//set MySql insert statement
			pst.executeUpdate();
			
			String query = "INSERT INTO debts(email, name, balance, rate, payment, password)"
					+ " VALUES(?, ?, ?, ?, ?, ?)";
			
			//set insert variables
			pst = con.prepareStatement(query);
			pst.setString (1, user.getEmail());
			pst.setString (2, debt.getName());
			pst.setDouble (3, debt.getBalance());
			pst.setDouble (4, debt.getRate());
			pst.setDouble (5, debt.getPayment());
			pst.setString (6, user.getPass_word());

			//execute statement
			pst.executeUpdate();

		}catch(Exception ex){System.out.println("Error: " + ex);}
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to INSERT an Income
	 * @param user
	 * @param income
	 * @throws Exception
	 */
	public void insertIncome(User user, Transaction income) throws Exception{
		try{
			pst = con.prepareStatement("CREATE TABLE IF NOT EXISTS incomes(email varchar(255), "
					+ "amount double, date date, description varchar(255), reoccuring boolean, password varchar(255))");
			//create new table if it doesn't exist
			pst.executeUpdate();
			
			String query = "INSERT INTO incomes(email, amount, date, description, reoccuring, password)"
					+ "VALUES(?,?,?,?,?,?)";
			
			//set insert variables
			pst = con.prepareStatement(query);
			pst.setString (1, user.getEmail());
			pst.setDouble (2, income.getValue());
			pst.setDate   (3, Date.valueOf(income.getDate()));
			pst.setString (4, income.getDescription());
			pst.setBoolean(5, income.getReoccuring());
			pst.setString (6, user.getPass_word());

			//execute statement
			pst.executeUpdate();

		}catch(Exception ex){System.out.println("Error: " + ex);}	
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to INSERT a new Expense
	 * @param user
	 * @param expense
	 */
	public void insertExpense(User user, Expense expense){
		try{
			pst = con.prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ "expenses(email varchar(255), "
					+ "amount double, date date, description varchar(255), "
					+ "reoccuring boolean, category varchar(255), password varchar(255))");
			pst.executeUpdate();
			
			//removed date
			String query = "INSERT INTO expenses(email, amount, date, "
					+ "description, reoccuring, category, password)"
					+ "VALUES(?,?,?,?,?,?,?)";
			pst = con.prepareStatement(query);
			pst.setString(1,user.getEmail());
			pst.setDouble(2,expense.getValue());
			pst.setDate(3, Date.valueOf(expense.getDate()));
			pst.setString(4, expense.getDescription());
			pst.setBoolean(5, expense.getReoccuring());
			pst.setString(6, expense.getCategory().toString());
			pst.setString(7, user.getPass_word());
			pst.execute();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
	}
	
	//***********************************************SELECT Requests*********************************************************
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to check if a user exists. Returns Boolean
	 * @param email
	 * @param pass_word
	 * @return
	 * @throws Exception
	 */
	
	public Boolean available(String email) throws Exception{
		try{
			String query = "SELECT * FROM users WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setString(1,email);
			rs = pst.executeQuery();
			if(rs.next()){
				return false;
			}
		}catch(Exception ex){
			System.out.print("Error: " + ex);
		}
		return true;
	}
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
			if(rs.next()){
				System.out.println("Valid user");
				return true;
			}
		}catch(Exception ex){System.out.println("Error: " + ex);}
		System.out.println("User does not exist");
		return false;
	}	
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to get User email and Password.Returns User.
	 * @param email
	 * @param pass_word
	 * @return
	 * @throws Exception
	 */
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
		}catch(SQLException ex){System.out.println(ex);}
		return null;
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to get all Debts from user. Returns ArrayList of user Debt
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Debt> selectDebt(User user) throws Exception{
		//Create Debt ArrayList
		ArrayList<Debt> debtList = new ArrayList<Debt>();
		
		//MySql request to select all Debts from User
		try{
			pst = con.prepareStatement("SELECT * FROM debts WHERE email = ? AND password = ?");
			pst.setString(1, user.getEmail());
			pst.setString(2, user.getPass_word());
			rs = pst.executeQuery();
			
			//Loop through all the information pulled and insert fields into the object
			while(rs.next() != false){
				Debt debt = new Debt();
				debt.setName(rs.getString("name"));
				debt.setBalance(rs.getDouble("balance"));
				debt.setRate(rs.getDouble("rate"));
				debt.setPayment(rs.getDouble("payment"));
				
				//push Debt object to Debt ArrayList
				debtList.add(debt);
			}
			//return List
			return debtList;
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
		return null;
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to get all Expenses from user. Returns ArrayList of Expenses
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Expense> selectExpense(User user) throws Exception{
		
		//Create return object
		ArrayList<Expense> expenseList = new ArrayList<Expense>();
		
		//MySQL request to select all Expenses from User
		try{
			pst = con.prepareStatement("SELECT * FROM expenses WHERE email = ? and password = ?");
			pst.setString(1, user.getEmail());
			pst.setString(2, user.getPass_word());
			rs = pst.executeQuery();
			
			//Loop through all pulled information and add fields to objects
			while(rs.next() != false){
				Expense expense = new Expense();
				expense.setValue(rs.getDouble("amount"));
				expense.setDate(rs.getDate("date").toLocalDate());
				expense.setDescription(rs.getString("description"));
				expense.setReoccuring(rs.getBoolean("reoccuring"));
				
				//Get the ExpenseCategory
				if(rs.getString("category").equals("BILL")){expense.setCategory(ExpenseCategory.BILL);}	
				if(rs.getString("category").equals("RECREATION")){expense.setCategory(ExpenseCategory.RECREATION);}
				if(rs.getString("category").equals("FOOD")){expense.setCategory(ExpenseCategory.FOOD);}
				if(rs.getString("category").equals("SAVINGS")){expense.setCategory(ExpenseCategory.SAVINGS);}
				if(rs.getString("category").equals("MISCELLANEOUS")){expense.setCategory(ExpenseCategory.MISCELLANEOUS);}
				if(rs.getString("category").equals("HOUSING")){expense.setCategory(ExpenseCategory.HOUSING);}
				
				//Push Expense to Expense ArrayList
				expenseList.add(expense);
			}
			
			//Return ArrayList
			return expenseList;
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
		return null;
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to get all Incomes from user. Returns ArrayList of Incomes
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Income> selectIncome(User user) throws Exception{
		
		//Create Income ArrayList
		ArrayList<Income> incomeList = new ArrayList<Income>();
		
		//MySQL request to select all Incomes from User
		try{
			pst = con.prepareStatement("SELECT * FROM incomes WHERE email = ? and password = ?");
			pst.setString(1, user.getEmail());
			pst.setString(2, user.getPass_word());
			rs = pst.executeQuery();
			
			//loop through all information pulled and add fields to object
			while(rs.next() != false){
				Income income = new Income();
				income.setValue(rs.getDouble("amount"));
				income.setDate(rs.getDate("date").toLocalDate());
				income.setDescription(rs.getString("description"));
				income.setReoccuring(rs.getBoolean("reoccuring"));
				
				//Push income to Income ArrayList
				incomeList.add(income);
			}
			
			//Return Income ArrayList
			return incomeList;
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
		return null;
	}
	
	//***********************************************DELETE Requests**********************************************************
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to delete all User information from database, User, Expense, Income, Debts.
	 * @param email
	 * @throws Exception
	 */
	public void deleteUser(User user) throws Exception{
		try{
			//variable to hole request
			String query;
			
			//MySQL request to DELETE User from user table
			query = "DELETE FROM users WHERE email = ? AND password = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user.getEmail());
			pst.setString(2, user.getPass_word());
			pst.executeUpdate();
			
			//MySQL request to Delete User from expenses table
			query = "DELETE FROM expenses WHERE email = ? And password = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user.getEmail());
			pst.setString(2, user.getPass_word());
			pst.executeUpdate();
			
			//MySQL request to DELETE USER from incomes table
			query = "DELETE FROM incomes WHERE email = ? And password = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user.getEmail());
			pst.setString(2, user.getPass_word());
			pst.executeUpdate();
			
			//MySQL request to DELETE USER from incomes table
			query = "DELETE FROM debts WHERE email = ? AND password = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user.getEmail());
			pst.setString(2, user.getPass_word());
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}	
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to delete all User Expenses and Incomes
	 * @param email
	 */
	public void deleteAllTransactions(User user){
		//Variable to hold request
		String query;
		
		//Clear expenses and incomes from database
		try{
			//MySQL request to DELETE expenses
			query = "DELETE FROM expenses WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user.getEmail());
			pst.executeUpdate();
			
			//MySQL request to DELETE incomes
			query = "DELETE FROM incomes WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user.getEmail());
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
	}
	
	//---------------------------------------------------------------------------------------------------------
	/**
	 * Method to delete all User Debts
	 * @param user
	 */
	public void deleteAllDebts(User user){
		//Variable to hold request
		String query;
		
		//Clear debts from database
		try{
			//MySQL request to DELETE debts
			query = "DELETE FROM debts WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setString(1, user.getEmail());
			pst.executeUpdate();
			
		}catch(Exception ex){System.out.println("Error: " + ex);}
	}
	
	//************************************************Helper Functions********************************************************
	
	/*//delete a single income
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
	}*/
	
}
