package it.polimi.tiw.projectRIA.beans;

public class Account {

	private int id;
	private float balance;
	private String username;
	
	
	public Account(int id,float balance,String username) {
		this.id = id;
		this.balance = balance;
		this.username = username;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}
}
