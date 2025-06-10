package it.polimi.tiw.project.controllers;

import java.io.IOException;




import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.beans.Account;
import it.polimi.tiw.project.beans.Transaction;
import it.polimi.tiw.project.dao.AccountDAO;


import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.beans.User;


@WebServlet("/CheckTransaction")
public class CheckTransaction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public CheckTransaction() {
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

		
		
		
		
		String destinationUser = null;
		int destinationAccount = -1;
		int numAccount;
		String description = null;
		float amount = -1;
		try {
			
			destinationUser = StringEscapeUtils.escapeJava(request.getParameter("idUser"));
			destinationAccount = Integer.parseInt(request.getParameter("idAccount"));
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			amount = Float.parseFloat(request.getParameter("amount"));
			numAccount = Integer.parseInt(request.getParameter("idThis"));
			
			
			if (destinationUser == null || description==null ||destinationUser.isEmpty() ||description.isEmpty()) {
			
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}
		

		String path;
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		HttpSession session = request.getSession();		
		User user = (User) session.getAttribute("user");
		
		AccountDAO accountDAO = new AccountDAO(connection);
		Account source = null;
		
		try {
			source =accountDAO.getAccountFromId(numAccount);
			
			if(source == null || !source.getUsername().equals(user.getUsername())) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account source is not correct");
				return;
			}
		}catch(Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to check source account credentials");
			return;
		}
		
		
		
		if(amount<=0) {
			
			ctx.setVariable("error", "Amount can not be 0 or negative!");
			ctx.setVariable("sourceAccount", numAccount);
			path = "/WEB-INF/Error";
			templateEngine.process(path, ctx, response.getWriter());
			
		}
		else {
			
			
			
			
			if(destinationAccount<=0) {
				ctx.setVariable("error", "Error bank account not found!");
				ctx.setVariable("sourceAccount", numAccount);
				path = "/WEB-INF/Error";
				templateEngine.process(path, ctx, response.getWriter());				
			}
			else {

				if(numAccount==destinationAccount) {
					ctx.setVariable("error", "The destination account can not be the source account!");
					ctx.setVariable("sourceAccount", numAccount);
					path = "/WEB-INF/Error";
					templateEngine.process(path, ctx, response.getWriter());	
				}
				else
				{
					Account accountDestination = null;
					try 	{
						accountDestination= accountDAO.getAccountFromId(destinationAccount);
						} catch (Exception e) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to check destination account credentials");
						return;
					}
					
					if(accountDestination == null) {
						ctx.setVariable("error", "Account Destination not found!");
						ctx.setVariable("sourceAccount", numAccount);
						path = "/WEB-INF/Error";
						templateEngine.process(path, ctx, response.getWriter());	
						return;
					}
					
					if((destinationUser.compareTo(accountDestination.getUsername())!=0)){
						ctx.setVariable("error", "Error bank account does not correspond!");
						ctx.setVariable("sourceAccount", numAccount);
						path = "/WEB-INF/Error";
						templateEngine.process(path, ctx, response.getWriter());	
					}
					else  {
					
						
						
						float moneyBeforeS=-1;
						float moneyBeforeD=-1;
						if(source.getBalance()-amount<0){
							ctx.setVariable("error", "Not enough money in the bank!!");
							ctx.setVariable("sourceAccount", numAccount);
							path = "/WEB-INF/Error";
							templateEngine.process(path, ctx, response.getWriter());
						}
						else{						
							
							
							moneyBeforeS = source.getBalance();
							moneyBeforeD= accountDestination.getBalance();;
							
							Transaction transaction = new Transaction();
							Date date=new Date();
							transaction.setAmount(amount);
							transaction.setDate(date);
							transaction.setDescription(description);
							transaction.setSourceAccount(source.getId());
							transaction.setDestinationAccount(accountDestination.getId());
							
							try {
								if(!accountDAO.updateAccounts(source, accountDestination, transaction)) {
									response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Some error occurred with the transaction process. Please try again later!");
									return;
								}
							}
							catch (Exception e) {
								response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Some error occurred with the transaction process. Please try again later!");
								return;
							}	
							
							
							
							ctx.setVariable("destinationUser",  destinationUser);
							ctx.setVariable("destinationId", destinationAccount);
							ctx.setVariable("description", description);
							ctx.setVariable("amount", amount);
	
							ctx.setVariable("moneyBeforeTransactionS", moneyBeforeS);
							ctx.setVariable("moneyAfterTransactionS", moneyBeforeS-amount);
							
							ctx.setVariable("moneyBeforeTransactionD", moneyBeforeD);
							ctx.setVariable("moneyAfterTransactionD", moneyBeforeD+amount);
							
							ctx.setVariable("sourceAccount", numAccount);
							ctx.setVariable("sourceUser", source.getUsername());
	
							
							
							path = "/WEB-INF/ConfirmTransaction";
							templateEngine.process(path, ctx, response.getWriter());
						
						}
					}
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