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
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projectRIA.beans.Account;
import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.dao.AccountDAO;



import it.polimi.tiw.projectRIA.utils.ConnectionHandler;


@WebServlet("/AddMoney")
@MultipartConfig
public class AddMoney extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddMoney() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		float number = -1;
		int idAccount = -1;
		
		
		
		HttpSession session = request.getSession();
		try {
			
			idAccount = Integer.parseInt(request.getParameter("id_account"));			
			number = Float.parseFloat(request.getParameter("number"));
			if (idAccount <= 0 || number<=0 ) {
			
				throw new Exception("Not valid input");
			}

		} catch (Exception e) {	
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Not invalid input");
			return;
		}
		

		
					
		AccountDAO accountDAO = new AccountDAO(connection);
		Account account = null;
		try { 
			account= accountDAO.getAccountFromId(idAccount);
			if(account == null)
				throw new Exception();
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Incorrect id account");
				return;
		}
		
		User user = (User) session.getAttribute("user");
		
		
		if(user.getUsername().compareTo(account.getUsername())!=0)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("id account does not corrispond to your!");
			return;
		}
		else
		{
			
			if(!accountDAO.addMoney(idAccount, number)) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Not possible add money!!");
				return;
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