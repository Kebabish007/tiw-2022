package it.polimi.tiw.projectRIA.controllers;

import java.io.IOException;




import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.dao.AccountDAO;
import it.polimi.tiw.projectRIA.utils.ConnectionHandler;


@WebServlet("/CreateNewAccount")
public class CreateNewAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateNewAccount() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	
		HttpSession session = request.getSession();

		User user = (User) session.getAttribute("user");
		AccountDAO accountDAO = new AccountDAO(connection);
		
		if(!accountDAO.insertNewAccount(user))
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Not possible to insert new Account");
			return;
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