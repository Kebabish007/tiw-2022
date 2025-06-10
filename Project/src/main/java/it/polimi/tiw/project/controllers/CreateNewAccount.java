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


import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AccountDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;


@WebServlet("/CreateNewAccount")
public class CreateNewAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public CreateNewAccount() {
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

		HttpSession session = request.getSession();

		User user = (User) session.getAttribute("user");
		AccountDAO accountDAO = new AccountDAO(connection);
		
		if(accountDAO.insertNewAccount(user))
		{
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/Home");
			rd.forward(request, response);
		}
		else
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not Possible to insert new account");
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