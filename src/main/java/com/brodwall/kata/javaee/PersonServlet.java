package com.brodwall.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

	private static final long serialVersionUID = 6628439558603357450L;
	private PersonDao personDao;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");

		PrintWriter writer = resp.getWriter();
		if (req.getPathInfo().equals("/create.html")) {
			writer.println("<form method='post' action='create.html'>");
			writer.println("<input type='text' name='full_name' value=''/>");
			writer.println("<input type='submit' name='create' value='Create person'/>");
			writer.println("</form>");
		} else {
			String nameQuery = req.getParameter("name_query");
			List<Person> people = personDao.findPeople(nameQuery);
			if (nameQuery == null) nameQuery = "";

			writer.println("<html>");
			writer.println("<form method='get' action='find.html'>");
			writer.println("<input type='text' name='name_query' value='" + nameQuery + "'/>");
			writer.println("<input type='submit' name='find' value='Search'/>");
			writer.println("</form>");

			writer.println("<ul>");
			for (Person person : people) {
				writer.println("<li>" + person.getName() + "</li>");
			}
			writer.println("</ul>");
			writer.println("</html>");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		personDao.createPerson(Person.withName(req.getParameter("full_name")));
		resp.sendRedirect("/");
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

}
