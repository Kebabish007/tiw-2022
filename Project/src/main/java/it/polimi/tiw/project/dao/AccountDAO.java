package it.polimi.tiw.project.dao;

import java.sql.Connection;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.project.beans.Account;
import it.polimi.tiw.project.beans.Transaction;
import it.polimi.tiw.project.beans.User;

public class AccountDAO {
	private Connection con;

	public AccountDAO(Connection connection) {
		this.con = connection;
	}

	public ArrayList<Account> getAccountsFromIdUser(String idUser) throws SQLException {
		String query = "SELECT  id, balance, idUser FROM account WHERE idUser = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, idUser);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null; 
				else {
					ArrayList<Account> accounts= new ArrayList<Account>();
					
					 while (result.next()) {
					      accounts.add(new Account(result.getInt("id"), result.getFloat("balance"),idUser));
					    }
					 
					return accounts;
				}
				
			}
		}
	}
	
	public Account getAccountFromId(int id) throws SQLException {
		String query = "SELECT  id,balance,idUser FROM account WHERE id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null; 
				else {
					if(result.next()){
					Account account = new Account(id,result.getFloat(2),result.getString(3)); 
					return  account;
					}
				}
				
			}
		}
		return null;
	}

	public boolean updateAccounts(Account source,Account destination,Transaction transaction) throws SQLException {
		String sqlTo = "UPDATE account set balance = balance + ? WHERE id = ?";
		String sqlFrom = "UPDATE account set balance = balance - ? WHERE id = ?";
		String insertSql = "INSERT INTO transaction(sourceAccount,destinationAccount,date,amount,description) VALUES(?,?,?,?,?)";
		boolean result = true;
		
		
		try {
			
			con.setAutoCommit(false);
			
			PreparedStatement pstatement = con.prepareStatement(sqlTo);
			PreparedStatement astatement = con.prepareStatement(sqlFrom);
			PreparedStatement tstatement = con.prepareStatement(insertSql); 
			
			pstatement.setFloat(1, transaction.getAmount());
			pstatement.setInt(2, destination.getId());			
			pstatement.execute();
			
			astatement.setFloat(1, transaction.getAmount());
			astatement.setInt(2, source.getId());
			astatement.execute();
			
			
			tstatement.setInt(1, transaction.getSourceAccount());
			tstatement.setInt(2, transaction.getDestinationAccount());
			tstatement.setDate(3, transaction.getDate());
			tstatement.setFloat(4, transaction.getAmount());
			tstatement.setString(5, transaction.getDescription());
			tstatement.execute();
			
			con.commit();
			
		}
		catch(Exception e) {
			result = false;
			con.rollback();
		}
		finally {
			con.setAutoCommit(true);
		}
		return result;
	}
	
	
	public boolean insertNewAccount(User user) {
		String sqlAccount = "INSERT INTO account(balance,idUser) VALUES (0,?)";
		try (PreparedStatement pstatement = con.prepareStatement(sqlAccount);) {
			pstatement.setString(1, user.getUsername());
			
			pstatement.execute();
					}
		catch(Exception e) {
			return false;
		}
		return true;	
	}
	public boolean addMoney(int id,float num) {
		String sql = "UPDATE account set balance = balance + ? WHERE id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(sql);) {
			pstatement.setFloat(1, num);
			pstatement.setInt(2, id);			
			pstatement.execute();
		}
		catch(Exception e) {
			return false;
		}
		return true;	
	}
	
	
	
	
	
}
