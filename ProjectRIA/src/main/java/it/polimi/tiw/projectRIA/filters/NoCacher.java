package it.polimi.tiw.projectRIA.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


public class NoCacher implements Filter {
    public NoCacher() {
    }

    public void init(FilterConfig fConfig) throws ServletException {
	}
    
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 

	{
		HttpServletResponse res = (HttpServletResponse) response;
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
		res.setHeader("Pragma", "no-cache");
		res.setHeader("Expires", "0"); 
		chain.doFilter(request, response);
	}
	
	public void destroy() {
	}
}
