package it.polimi.tiw.projectRIA.dao;

import java.sql.Connection;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.projectRIA.beans.Transaction;


public class TransactionDAO {
	private Connection con;

	public TransactionDAO(Connection connection) {
		this.con = connection;
	}

	
	public ArrayList<Transaction> getTransactions(int idUser) throws SQLException {
		String query = "SELECT  sourceAccount, destinationAccount, id,date,amount,description FROM transaction WHERE destinationAccount = ? OR sourceAccount=? order by id desc";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idUser);
			pstatement.setInt(2, idUser);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null; 
				else {
					ArrayList<Transaction> transactions= new ArrayList<Transaction>();
					
					 while (result.next()) {
						 transactions.add(new Transaction(result.getInt("sourceAccount"),result.getInt("destinationAccount"),result.getInt("id"),result.getDate("date"),result.getInt("amount"),result.getString("description")));
					    }
					return transactions;
				}
				
			}
		}
	}
}