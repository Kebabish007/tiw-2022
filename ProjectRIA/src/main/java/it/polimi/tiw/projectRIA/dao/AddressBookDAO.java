package it.polimi.tiw.projectRIA.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import it.polimi.tiw.projectRIA.beans.AddressBook;


public class AddressBookDAO{
	private Connection con;

	public AddressBookDAO(Connection connection) {
		this.con = connection;
	}
	
	public boolean insertNewContact(int source,int destination) {
		String sqlAccount = "INSERT INTO address_book(numAccountSource,numAccountDestination) VALUES (?,?)";
		try (PreparedStatement pstatement = con.prepareStatement(sqlAccount);) {
			pstatement.setInt(1, source);
			pstatement.setInt(2, destination);
			pstatement.execute();
					}
		catch(Exception e) {
			return false;
		}
		return true;	
	}
	public AddressBook getContactsFromUsername(String username) throws SQLException {
		String query = "SELECT  b.idUser,ab.numAccountDestination FROM account as a INNER JOIN address_book as ab ON a.id = ab.numAccountSource INNER JOIN account as b ON b.id=ab.numAccountDestination WHERE a.idUser = ? ORDER BY ab.numAccountDestination";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null; 
				else {
					
					AddressBook addressBook = new AddressBook();
					while(result.next()){
						if(!addressBook.getContacts().containsKey(result.getString(1))) {
							addressBook.getContacts().put(result.getString(1), new HashSet<Integer>());
						}
						addressBook.getContacts().get(result.getString(1)).add(result.getInt(2));						
					}
					return addressBook;
				}
				
			}
		}
	}
	public boolean existsContactEntry(int idCurrentAccount, int idDestinationAccount) throws SQLException{

		boolean exist = false;

		
		String query = "SELECT * FROM address_book WHERE numAccountSource= ? AND numAccountDestination = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idCurrentAccount);
			pstatement.setInt(2, idDestinationAccount);
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) 
					exist= true;
			}	
		}
		return exist;
	}
	
	
	
	
}