package it.polimi.tiw.projectRIA.filters;

import java.io.IOException;



import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



public class UserNotLogged implements Filter {
	
    public UserNotLogged() {
    }
    
    public void init(FilterConfig fConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession s = req.getSession();
		
		if(!s.isNew() && s.getAttribute("user") != null) {
			res.sendRedirect(request.getServletContext().getContextPath()+"/Home.html");
			return;
		}
		

		chain.doFilter(request, response);
		
	
	}
	
	public void destroy() {
	}
}
