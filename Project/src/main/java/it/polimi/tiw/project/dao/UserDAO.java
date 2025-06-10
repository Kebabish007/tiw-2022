package it.polimi.tiw.project.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.project.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	
	
	public boolean insertUser(User user,String password) throws SQLException {
		String sqlUser = "INSERT INTO user(username,name,surname,email,password) VALUES(?,?,?,?,?)";
		String sqlAccount = "INSERT INTO account(balance,idUser) VALUES (0,?)";
		boolean result = true;
		try {
			con.setAutoCommit(false);
			PreparedStatement pstatement = con.prepareStatement(sqlUser);
			PreparedStatement astatement = con.prepareStatement(sqlAccount);
			pstatement.setString(1, user.getUsername());
			pstatement.setString(2, user.getName());
			pstatement.setString(3, user.getSurname());
			pstatement.setString(4, user.getEmail());
			pstatement.setString(5, password);
			
			pstatement.execute();
			
			astatement.setString(1, user.getUsername());
			astatement.execute();
			
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
	
	
	public User checkLogin(String usr, String pwd) throws SQLException {
		String query = "SELECT  username, name, surname, email, password FROM user  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usr);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null;
				else {
					result.next();
					User user = new User();
					user.setEmail(result.getString("email"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
	
	
	
}
