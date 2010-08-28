package no.steria.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

	private static final long serialVersionUID = 7250610346500297236L;
	private PersonDao personDao;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		showCreateForm(resp.getWriter(), null, "");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String fullName = req.getParameter("full_name");
		String errorMessage = validateFullName(fullName);
		
		if (errorMessage != null) {
			resp.setContentType("text/html");
			showCreateForm(resp.getWriter(), errorMessage, fullName);
		} else {
			personDao.createPerson(Person.withName(fullName));
			resp.sendRedirect("/");
		}
	}

	private String validateFullName(String fullName) {
		if (fullName.equals("")) {
			return "You must provide a name";
		} else if (containsIllegalCharacters(fullName)) {
			return "Illegal character in name";
		}
		return null;
	}

	private boolean containsIllegalCharacters(String fullName) {
		for (char c : fullName.toCharArray()) {
			if (!Character.isLetter(c) && c != ' ') return true;
		}
		return false;
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		personDao.beginTransaction();
		boolean commit = false;
		try {
			super.service(req, resp);
			commit = true;
		} finally {
			personDao.endTransaction(commit);
		}
	}

	private void showCreateForm(PrintWriter writer, String errorMessage, String fullNameValue) {
		writer.append("<html><body>");
		if (errorMessage != null) writer.append("<p class='errorMessage'>" + errorMessage + "</p>");
		writer.append("<form action='createPerson.html' method='post'>")
			.append("<input type='text' name='full_name' value='")
			.append(htmlEscape(fullNameValue))
			.append("'/>")
			.append("<input type='submit' name='createPerson' value='Create person'/>")
			.append("</form>")
			.append("</body></html>");
	}

	private String htmlEscape(String string) {
		return string.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
}
