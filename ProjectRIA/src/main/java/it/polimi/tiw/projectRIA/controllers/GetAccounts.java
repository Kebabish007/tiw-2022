package it.polimi.tiw.projectRIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.projectRIA.beans.Account;
import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.dao.AccountDAO;
import it.polimi.tiw.projectRIA.utils.ConnectionHandler;

@WebServlet("/GetAccounts")
@MultipartConfig

public class GetAccounts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetAccounts() {
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
		ArrayList<Account> accounts = new ArrayList<Account>();
		try {
			accounts = accountDAO.getAccountsFromIdUser(user.getUsername());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to find any accounts!");
			return;
		}

				
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(new Gson().toJson(accounts));
		
	}


public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
