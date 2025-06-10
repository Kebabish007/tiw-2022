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

import com.google.gson.Gson;

import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.dao.UserDAO;

import it.polimi.tiw.projectRIA.utils.ConnectionHandler;


@WebServlet("/CheckCredentials")
@MultipartConfig
public class CheckCredentials extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckCredentials() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = null;
		String password = null;
		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing or empty credential value");
			return;
		}
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			user = userDao.checkLogin(username, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}

		
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Incorrect username or password");
			return;
		} else {
			request.getSession().setAttribute("user", user);
			response.setStatus(HttpServletResponse.SC_OK);	
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(new Gson().toJson(user));
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