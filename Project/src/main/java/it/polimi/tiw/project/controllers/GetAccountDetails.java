package it.polimi.tiw.project.controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.beans.Account;
import it.polimi.tiw.project.beans.Transaction;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AccountDAO;
import it.polimi.tiw.project.dao.TransactionDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;

@WebServlet("/GetAccountDetails")
public class GetAccountDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GetAccountDetails() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		HttpSession session = request.getSession();
		
		Integer accountId = null;
		try {
			accountId = Integer.parseInt(request.getParameter("accountid"));
			
			
		} catch (NumberFormatException | NullPointerException e) {
			
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		
		User user = (User) session.getAttribute("user");
		AccountDAO accountDAO = new AccountDAO(connection);
		Account account = null;
		try {
			account = accountDAO.getAccountFromId(accountId);
			if(account == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The account you want to view does not exist!");
				return;
			}
			
			if(user.getUsername().compareTo(account.getUsername()) != 0) {
				
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}
		
		
		
		TransactionDAO transactionDAO = new TransactionDAO(connection);
		ArrayList<Transaction> transactions = null;
		ArrayList<Account> accounts = new ArrayList<Account>();

		try {
			transactions = transactionDAO.getTransactions(accountId);
		} catch (Exception e) {
			transactions = null;
		}
		
		if(transactions!=null) {
			try {
				Account accountToAdd = null;
				boolean exist = false;
				for(Transaction t : transactions) {
					exist = false;
					if(t.getSourceAccount()== accountId) {
						accountToAdd = accountDAO.getAccountFromId(t.getDestinationAccount());
					}
					else {
						accountToAdd = accountDAO.getAccountFromId(t.getSourceAccount());
					}
					for(Account a: accounts) {
						if(a.getId() == accountToAdd.getId()) {
							exist = true;
						}
					}
					if(!exist) {
						accounts.add(accountToAdd);
					}
				}
			}
			catch(Exception e) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
		}
		
		
		
		String path = "/WEB-INF/AccountDetails.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("transactions", transactions);
		ctx.setVariable("id", accountId);
		ctx.setVariable("balance", account.getBalance());
		ctx.setVariable("accounts", accounts);
		templateEngine.process(path, ctx, response.getWriter());
		

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
