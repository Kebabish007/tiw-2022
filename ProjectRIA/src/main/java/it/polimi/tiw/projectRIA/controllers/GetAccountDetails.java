package it.polimi.tiw.projectRIA.controllers;

import java.io.IOException;



import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.google.gson.Gson;

import it.polimi.tiw.projectRIA.beans.Account;
import it.polimi.tiw.projectRIA.beans.Transaction;
import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.dao.AccountDAO;
import it.polimi.tiw.projectRIA.dao.TransactionDAO;
import it.polimi.tiw.projectRIA.utils.ConnectionHandler;

@WebServlet("/GetAccountDetails")
@MultipartConfig

public class GetAccountDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetAccountDetails() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		Integer accountId = null;
		try {
			accountId = Integer.parseInt(request.getParameter("accountid"));
			
			
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		
		
		User user = (User) session.getAttribute("user");
		AccountDAO accountDAO = new AccountDAO(connection);
		Account account = null;
		try {
			account = accountDAO.getAccountFromId(accountId);
			if(account == null){
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("The account you want to view does not exist!");
				return;
			}
			
			if(user.getUsername().compareTo(account.getUsername()) != 0) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("User not allowed");
				return;			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Resource not found");
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
				
		HashMap<String,Object> obj = new HashMap<String,Object>();
		
		obj.put("transactions",  transactions);
		obj.put("accounts", accounts);
				
				
		String json = new Gson().toJson(obj);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
