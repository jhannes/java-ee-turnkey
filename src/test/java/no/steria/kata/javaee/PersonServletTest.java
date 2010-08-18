package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class PersonServletTest {

    private PersonServlet servlet = new PersonServlet();
    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse resp = mock(HttpServletResponse.class);
    private PersonDao personDao = mock(PersonDao.class);

    @Test
    public void shouldShowCreateForm() throws Exception {
        when(req.getMethod()).thenReturn("GET");
        StringWriter html = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(html));

        servlet.service(req , resp);

        verify(resp).setContentType("text/html");

        assertThat(html.toString()).contains("form");
        assertThat(html.toString()).contains("<input type='text' name='full_name' value=''");
        assertThat(html.toString()).contains("<input type='submit' name='create' value='Create the person'");

        DocumentHelper.parseText(html.toString());
    }

    @Test
    public void shouldCreatePerson() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("full_name")).thenReturn("Hva som helst");

        servlet.service(req , resp);

        InOrder order = inOrder(personDao);
        order.verify(personDao).beginTransaction();
        order.verify(personDao).createPerson(Person.withName("Hva som helst"));
        order.verify(personDao).endTransaction(true);

        verify(resp).sendRedirect("/");
    }

    @Before
    public void setupServlet() {
        servlet.setPersonDao(personDao);
    }
}
