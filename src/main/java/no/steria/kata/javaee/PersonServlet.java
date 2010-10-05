package no.steria.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

    private static final long serialVersionUID = 7744195856599544243L;
    private PersonDao personDao;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.beginTransaction();
        boolean commit = false;
        try {
            super.service(req, resp);
            commit = true;
        } finally {
            personDao.endTransaction(commit);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        if (req.getPathInfo().equals("/findPeople.html")) {
            String nameQuery = req.getParameter("name_query");
            List<Person> people = personDao.findPeople(nameQuery);
            showSearchPage(writer, nameQuery, people);
        } else {
            showCreatePage(writer, "", null, "", null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String firstNameErrorMessage = validateName(firstName);
        String lastNameErrorMessage = validateName(lastName);

        if (firstNameErrorMessage != null || lastNameErrorMessage != null) {
            resp.setContentType("text/html");
            showCreatePage(resp.getWriter(), firstName, firstNameErrorMessage, lastName, lastNameErrorMessage);
        } else {
            personDao.createPerson(Person.withName(firstName, lastName));
            resp.sendRedirect("/");
        }
    }

    private String validateName(String fullName) {
        String errorMessage = null;
        if (fullName == null || fullName.equals("")) {
            errorMessage = "must be given";
        } else if (containsIllegalCharacters(fullName)) {
            errorMessage = "contains illegal characters";
        }
        return errorMessage;
    }

    private void showCreatePage(PrintWriter writer, String firstName, String firstNameValidationError, String lastName, String lastNameValidationError) {
        writer.append("<html>");
        writer.append("<head><style>.error { color: red; }</style></head>");

        if (firstNameValidationError != null) {
            writer.append("<div class='error'>First name ").append(firstNameValidationError).append("</div>");
        }
        if (lastNameValidationError != null) {
            writer.append("<div class='error'>Last name ").append(lastNameValidationError).append("</div>");
        }
        writer //
            .append("<form method='post' action='createPerson.html'>") //
            .append("<label>First name:</label>")
            .append("<input type='text' name='first_name' value='" + htmlEscape(firstName) + "'/>") //
            .append("<label>Last name:</label>")
            .append("<input type='text' name='last_name' value='" + htmlEscape(lastName) + "'/>") //
            .append("<input type='submit' name='createPerson' value='Create person'/>") //
            .append("</form>");
        writer.append("</html>");
    }

    private String htmlEscape(String fullName) {
        if (fullName == null) return "";
        return fullName.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    private void showSearchPage(PrintWriter writer, String nameQuery, List<Person> people) {
        if (nameQuery == null) nameQuery = "";
        writer //
            .append("<html>") //
            .append("<form method='get' action='findPeople.html'>") //
            .append("<input type='text' name='name_query' value='" + nameQuery + "'/>") //
            .append("<input type='submit' name='findPeople' value='Find people'/>") //
            .append("</form>");

        writer.append("<ul>");
        for (Person person : people) {
            writer.append("<li>").append(person.getName()).append("</li>");
        }
        writer //
            .append("</ul>") //
            .append("</html>") //
            ;
    }

    private boolean containsIllegalCharacters(String fullName) {
        String illegals = "<>&";
        for (char illegal : illegals.toCharArray()) {
            if (fullName.contains(Character.toString(illegal))) return true;
        }
        return false;
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public void init() throws ServletException {
        setPersonDao(new HibernatePersonDao("jdbc/personDs"));
    }
}
