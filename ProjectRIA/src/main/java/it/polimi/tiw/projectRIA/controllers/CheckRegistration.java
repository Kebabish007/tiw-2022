package it.polimi.tiw.projectRIA.controllers;

import java.io.IOException;




import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.*;


import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.dao.UserDAO;

import it.polimi.tiw.projectRIA.utils.ConnectionHandler;
import java.util.regex.*;

@WebServlet("/CheckRegistration")
@MultipartConfig
public class CheckRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


	public CheckRegistration() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = null;
		String password = null;
		String passwordRepeat = null;
		String email = null;
		String name = null;
		String surname = null;
		String checkEmail = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		
		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			passwordRepeat = StringEscapeUtils.escapeJava(request.getParameter("passwordRepeat"));
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
			if (username == null||surname==null ||name==null|| password == null||passwordRepeat == null ||email==null|| username.isEmpty() || password.isEmpty() || name.isEmpty()||surname.isEmpty()||email.isEmpty()||passwordRepeat.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing or empty credential value");
			return;
		}
		Pattern pattern = Pattern.compile(checkEmail);  
		Matcher matcher = pattern.matcher(email); 
				
		if(!matcher.matches())
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Email not valid");
			return;
		}
		else {
			if(!password.equals(passwordRepeat))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Password and repeat password are not equal");
				return;
			}
			else
			{
				UserDAO userDao = new UserDAO(connection);
				User user = new User();
				user.setEmail(email);
				user.setName(name);
				user.setSurname(surname);
				user.setUsername(username);
				try {
				if(!userDao.insertUser(user, password)) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Username already taken. Please choose another one");
					return;
				}
				else{
					response.setStatus(HttpServletResponse.SC_OK);	
				}
				}
				catch(Exception e)
				{
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("An error occured while processing your request!");
					return;
				}
			}
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}