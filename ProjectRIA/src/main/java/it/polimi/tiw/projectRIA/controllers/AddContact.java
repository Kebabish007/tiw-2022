package it.polimi.tiw.projectRIA.controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.beans.Account;
import it.polimi.tiw.projectRIA.dao.AccountDAO;
import it.polimi.tiw.projectRIA.dao.AddressBookDAO;
import it.polimi.tiw.projectRIA.utils.ConnectionHandler;

@WebServlet("/AddContact")
@MultipartConfig
public class AddContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
 
    public AddContact() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		this.connection = ConnectionHandler.getConnection(servletContext);
    }
    

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int source = -1;
		int account = -1;
		String username = null; 
		boolean find=false;
		try {
			source = Integer.parseInt(request.getParameter("sourceAccount"));

			username = StringEscapeUtils.unescapeJava(request.getParameter(("destinationUser")));

			account = Integer.parseInt(request.getParameter("destinationId"));
		}
		catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Missing parameter");
			return;
		}
		
		if((account==-1) ||(username == null)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Missing parameter");
			return;
		}
		
		
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		
		
		
		AccountDAO accountDAO = new AccountDAO(connection);
		ArrayList<Account> accountsDestination = null;
		ArrayList<Account> accountsSource = null;
		try {
			accountsDestination = accountDAO.getAccountsFromIdUser(username);
			accountsSource = accountDAO.getAccountsFromIdUser(user.getUsername());
		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;		
		}
		
		if((accountsSource == null) ||(accountsDestination == null)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Chosen parameters are inconsistent with the database info, retry");
			return;
		}
		for(Account a : accountsDestination) {
			if(a.getId() == account) {
				find = true;
			} 
		}
		if(!find) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Chosen parameters are inconsistent with the database info, retry");
			return;
		}
		find = false;
		for(Account a : accountsSource) {
			if(a.getId() == source) {
				find = true;
			} 
		}
		if(!find) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Chosen parameters are inconsistent with the database info, retry");
			return;
		}

		
		
		AddressBookDAO addressBookDAO = new AddressBookDAO(connection);
		boolean alreadyPresent = false;
		try {
			alreadyPresent = addressBookDAO.existsContactEntry(source, account);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error while process request to database");
			return;		
		}
		
		if(alreadyPresent) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Contact entry already exists");
			return;
		}
		
		
		if(!addressBookDAO.insertNewContact(source, account)) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to add new contact");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);	
		
	
	}
    @Override
    public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
