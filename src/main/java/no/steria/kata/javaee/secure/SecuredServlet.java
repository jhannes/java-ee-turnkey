package no.steria.kata.javaee.secure;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecuredServlet extends HttpServlet {
	
	private static final long serialVersionUID = 7002663346187460882L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		resp.getWriter().print("Hello, " + req.getUserPrincipal().getName());
	}
	

}
