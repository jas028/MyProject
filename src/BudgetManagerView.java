//Builds the basic frame and handles user interactions

import java.awt.event.ActionListener;

import javax.swing.*;

public class BudgetManagerView extends JFrame {
	
	//JFrame elements
	private JTextField 	num1 = new JTextField(10);
	private JTextField 	num2 = new JTextField(10);
	private JTextField	calcResult = new JTextField(10);
	private JLabel		plusLabel = new JLabel("+");
	private JButton		calcButton = new JButton("=");
	
	BudgetManagerView() {
		
		//Sets up view and adds components
		JPanel budgetPanel = new JPanel();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(900, 600);
		
		budgetPanel.add(num1);
		budgetPanel.add(plusLabel);
		budgetPanel.add(num2);
		budgetPanel.add(calcButton);
		budgetPanel.add(calcResult);
		
		this.add(budgetPanel);
	}
	
	public int getNum1() {
		
		return Integer.parseInt(num1.getText());		
	}
	
	public int getNum2() {
		
		return Integer.parseInt(num2.getText());
	}
	
	
	public void setCalcResult(int result) {
		
		calcResult.setText(Integer.toString(result));
	}
	
	//listen for action event from calcButton
	public void addCalcListener(ActionListener calcListener) {
		
		calcButton.addActionListener(calcListener);
	}

	public void displayErrorMessage(String message) {
		
		JOptionPane.showMessageDialog(this, message);
	}
}
