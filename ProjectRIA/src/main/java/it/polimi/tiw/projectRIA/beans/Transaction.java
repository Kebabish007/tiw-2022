package it.polimi.tiw.projectRIA.beans;

import java.sql.Date;

public class Transaction {

	
	private int sourceAccount;
	private int destinationAccount;
	
	private int id;
	private java.sql.Date date;
	private float amount;
	private String description;
	
	public Transaction() {
	}
	
	public Transaction(int sourceAccount,int destinationAccount,int id,Date date,float amount,String description) {
		this.sourceAccount = sourceAccount;
		this.setDestinationAccount(destinationAccount);
		this.id = id;
		this.setDate(date);
		this.amount = amount;
		this.setDescription(description);
	}
	
	public void setSourceAccount(int sourceAccount)
	{
		
		this.sourceAccount = sourceAccount;
	}
	
	public int getSourceAccount() {
		return sourceAccount;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(java.util.Date date) {
		this.date = new java.sql.Date(date.getTime());
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getDestinationAccount() {
		return destinationAccount;
	}


	public void setDestinationAccount(int destinationAccount) {
		this.destinationAccount = destinationAccount;
	}
}
