package no.steria.kata.javaee;

import java.io.IOException;

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

        resp.getWriter().append("<form method='post' action='create.html'>")//
                .append("<input type='text' name='full_name' value='' />")//
                .append("<input type='submit' name='create' value='Create the person' />")//
                .append("</form>");
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
