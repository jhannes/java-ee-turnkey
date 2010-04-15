package com.brodwall.kata.javaee;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

	private static final long serialVersionUID = 6628439558603357450L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html");

		resp.getWriter().println("<form method='post' action='create.html'>");
		resp.getWriter().println("<input type='text' name='full_name' value=''/>");
		resp.getWriter().println("<input type='submit' name='create' value='Create person'/>");
		resp.getWriter().println("</form>");

	}

}
