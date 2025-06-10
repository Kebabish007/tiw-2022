package it.polimi.tiw.project.controllers;

import java.io.IOException;



import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.UserDAO;

import it.polimi.tiw.project.utils.ConnectionHandler;
import java.util.regex.*;

@WebServlet("/CheckRegistration")
public class CheckRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public CheckRegistration() {
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

		String path=null;
		String username = null;
		String password = null;
		String passwordRepeat = null;
		String email = null;
		String name = null;
		String surname = null;
		String checkEmail = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		
		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			passwordRepeat = StringEscapeUtils.escapeJava(request.getParameter("passwordRepeat"));
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
			if (username == null||surname==null ||name==null|| password == null||passwordRepeat == null ||email==null|| username.isEmpty() || password.isEmpty() || name.isEmpty()||surname.isEmpty()||email.isEmpty()||passwordRepeat.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}
		}
		catch (Exception e) {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
		return;
		}
		Pattern pattern = Pattern.compile(checkEmail);  
		Matcher matcher = pattern.matcher(email); 
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if(!matcher.matches())
		{
			path = "/WEB-INF/Registration";
			ctx.setVariable("error", "Email not valid");
			templateEngine.process(path, ctx, response.getWriter());
		}
		else {
			if(!password.equals(passwordRepeat))
			{
				path = "/WEB-INF/Registration";
				ctx.setVariable("error", "Password and repeat password does not match");
				templateEngine.process(path, ctx, response.getWriter());
			}
			else
			{
				UserDAO userDao = new UserDAO(connection);
				User user = new User();
				user.setEmail(email);
				user.setName(name);
				user.setSurname(surname);
				user.setUsername(username);
				try {
				if(!userDao.insertUser(user, password)) {
					path = "/WEB-INF/Registration";
					ctx.setVariable("error", "Username already taken. Please choose another one");
					templateEngine.process(path, ctx, response.getWriter());
				}
				else{
					path = "/index.html";
					ctx.setVariable("msg", "Log in with your new credentials");
					templateEngine.process(path, ctx, response.getWriter());
				}
				}
				catch(Exception e)
				{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "An error occured while processing your request!");
					return;
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