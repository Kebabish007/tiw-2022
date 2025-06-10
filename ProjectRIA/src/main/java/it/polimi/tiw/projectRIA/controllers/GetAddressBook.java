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

import com.google.gson.Gson;

import it.polimi.tiw.projectRIA.beans.AddressBook;
import it.polimi.tiw.projectRIA.beans.User;
import it.polimi.tiw.projectRIA.dao.AddressBookDAO;
import it.polimi.tiw.projectRIA.utils.ConnectionHandler;

@WebServlet("/GetAddressBook")
@MultipartConfig

public class GetAddressBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetAddressBook() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		AddressBookDAO addressBookDAO = new AddressBookDAO(connection);
		AddressBook addressBook= null;
		try {
			addressBook = addressBookDAO.getContactsFromUsername(user.getUsername());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to connect with database to find addressBook!");
			return;
		}

				
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		if(addressBook!=null)
			response.getWriter().println(new Gson().toJson(addressBook.getContacts()));
		else
			response.getWriter().println(new Gson().toJson(null));
	}


public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
