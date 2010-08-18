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
        try {
            super.service(req, resp);
        } finally {
            personDao.endTransaction(true);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        PrintWriter writer = resp.getWriter();
        writer.append("<html>");
        if ("/find.html".equals(req.getPathInfo())) {
            String nameQuery = req.getParameter("name_query");

            showFindView(writer, nameQuery, personDao.findPeople(nameQuery));

        } else {
            showCreateView(writer);
        }
        writer.append("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.createPerson(Person.withName(req.getParameter("full_name")));
        resp.sendRedirect("/");
    }

    private void showFindView(PrintWriter writer, String nameQuery, List<Person> people) {
        showFindForm(writer, nameQuery);
        showFindResult(writer, people);
    }

    private void showFindResult(PrintWriter writer, List<Person> people) {
        writer.append("<ul>");
        for (Person person : people) {
            writer.append("<li>").append(person.getName()).append("</li>");
        }
        writer.append("</ul>");
    }

    private void showFindForm(PrintWriter writer, String nameQuery) {
        writer.append("<form method='get' action='find.html'>")//
                .append("<input type='text' name='name_query' value='" + (nameQuery != null ? nameQuery : "") + "' />")//
                .append("<input type='submit' name='find' value='Search for person' />")//
                .append("</form>");
    }

    private void showCreateView(PrintWriter writer) {
        writer.append("<form method='post' action='create.html'>")//
                .append("<input type='text' name='full_name' value='' />")//
                .append("<input type='submit' name='create' value='Create the person' />")//
                .append("</form>");
    }

    @Override
    public void init() throws ServletException {
        setPersonDao(new HibernatePersonDao("jdbc/personDs"));
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
