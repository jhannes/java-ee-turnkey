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
			showCreateForm(writer);
		} else {
			String nameQuery = req.getParameter("name_query");
			List<Person> people = personDao.findPeople(nameQuery);
			if (nameQuery == null) nameQuery = "";

			showSearchPage(writer, nameQuery, people);
		}
	}

	private void showCreateForm(PrintWriter writer) {
		writer.println("<form method='post' action='create.html'>");
		writer.println("<input type='text' name='full_name' value=''/>");
		writer.println("<input type='submit' name='create' value='Create person'/>");
		writer.println("</form>");
	}

	private void showSearchPage(PrintWriter writer, String nameQuery,
			List<Person> people) {
		writer.println("<html>");
		showSearchForm(writer, nameQuery);
		showSearchResults(writer, people);
		writer.println("</html>");
	}

	private void showSearchForm(PrintWriter writer, String nameQuery) {
		writer.println("<form method='get' action='find.html'>");
		writer.println("<input type='text' name='name_query' value='" + nameQuery + "'/>");
		writer.println("<input type='submit' name='find' value='Search'/>");
		writer.println("</form>");
	}

	private void showSearchResults(PrintWriter writer, List<Person> people) {
		writer.println("<ul>");
		for (Person person : people) {
			writer.println("<li>" + person.getName() + "</li>");
		}
		writer.println("</ul>");
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
