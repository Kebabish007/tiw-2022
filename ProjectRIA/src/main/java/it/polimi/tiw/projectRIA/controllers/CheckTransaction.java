package it.polimi.tiw.projectRIA.controllers;

import java.io.IOException;





import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.*;
import com.google.gson.Gson;

import it.polimi.tiw.projectRIA.beans.Account;
import it.polimi.tiw.projectRIA.beans.Transaction;
import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.dao.AccountDAO;


import it.polimi.tiw.projectRIA.utils.ConnectionHandler;


@WebServlet("/CheckTransaction")
@MultipartConfig

public class CheckTransaction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckTransaction() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

				
		String destinationUser = null;
		int destinationAccount = -1;
		int numAccount;
		String description = null;
		int amount = -1;
		HttpSession session = request.getSession();		
		User user = (User) session.getAttribute("user");
		try {
			
			destinationUser = StringEscapeUtils.escapeJava(request.getParameter("idUser"));
			destinationAccount = Integer.parseInt(request.getParameter("idAccount"));
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			amount = Integer.parseInt(request.getParameter("amount"));
			numAccount = Integer.parseInt(request.getParameter("idThis"));
			
			if (destinationUser == null || description==null ||destinationUser.isEmpty() ||description.isEmpty()) {
			
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {	
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing credential value");
			return;
		}
		AccountDAO accountDAO = new AccountDAO(connection);
		Account source = null;
		try {
			source =accountDAO.getAccountFromId(numAccount);
			
			if(source == null || !source.getUsername().equals(user.getUsername())) {
				
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Account source is not correct");
				return;
			}
			
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to check source account credentials");
			return;
		}

		
		if(amount<=0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Amount can not be 0 or negative");
			return;
			
		}
		else {
			
			
			
			if(destinationAccount<=0) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Error bank account not found!");
				return;			
			}
			else {

				if(numAccount==destinationAccount) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("The destination account can not be the source account!");
					return;			
				}
				else
				{
					Account accountDestination = null;
					try 	{
						accountDestination= accountDAO.getAccountFromId(destinationAccount);
						} catch (Exception e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.getWriter().println("Not possible to check destination account credentials");
						return;
					}
					
					if(accountDestination == null) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Account Destination not found!");
						return;
					}
					
					
					if((destinationUser.compareTo(accountDestination.getUsername())!=0)){
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Error bank account does not corrispond!");
						return;
					}
					else  {
						
						float moneyBeforeS=-1;
						float moneyBeforeD=-1;
						if(source.getBalance()-amount<0){
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							response.getWriter().println("Not enough money in this account!");
							return;
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
							
							
							HashMap<String,Object> obj = new HashMap<String,Object>();
							
							
							obj.put("destinationUser",  destinationUser);
							obj.put("destinationId", destinationAccount);
							obj.put("description", description);
							obj.put("amount", amount);
	
							obj.put("moneyBeforeTransactionS", moneyBeforeS);
							obj.put("moneyAfterTransactionS", moneyBeforeS-amount);
							
							obj.put("moneyBeforeTransactionD", moneyBeforeD);
							obj.put("moneyAfterTransactionD", moneyBeforeD+amount);
							
							obj.put("sourceAccount", numAccount);
							obj.put("sourceUser", source.getUsername());
	
							response.getWriter().write(new Gson().toJson(obj));
							
							
						
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