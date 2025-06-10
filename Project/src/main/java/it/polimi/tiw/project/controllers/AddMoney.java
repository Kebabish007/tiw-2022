package it.polimi.tiw.project.controllers;

import java.io.IOException;



import java.sql.Connection;
import java.sql.SQLException;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.beans.Account;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AccountDAO;



import it.polimi.tiw.project.utils.ConnectionHandler;


@WebServlet("/AddMoney")
public class AddMoney extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public AddMoney() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		float number = -1;
		int idAccount = -1;
		
		
		HttpSession session = request.getSession();
		try {
			
			
			number = Float.parseFloat(request.getParameter("number"));
			idAccount = Integer.parseInt(request.getParameter("idAccount"));
			if (idAccount <= 0 || number<=0 ) {
			
				throw new Exception("Not valid input");
			}

		} catch (Exception e) {	
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not valid input");
			return;
		}
		

		
					
		AccountDAO accountDAO = new AccountDAO(connection);
		Account account = null;
		try { 
			account= accountDAO.getAccountFromId(idAccount);
			if(account == null)
				throw new Exception();
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect id account");
				return;
		}
		
		User user = (User) session.getAttribute("user");
		
		
		if(user.getUsername().compareTo(account.getUsername())!=0)
		{
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "id account does not corrispond to your!");
			return;
		}
		else
		{
			
			if(accountDAO.addMoney(idAccount, number)) {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Home");
				rd.forward(request, response);
			}
			else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not add money!");
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