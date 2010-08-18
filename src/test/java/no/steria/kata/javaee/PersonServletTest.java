package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

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
    private StringWriter html = new StringWriter();

    @Test
    public void shouldShowCreateForm() throws Exception {
        when(req.getMethod()).thenReturn("GET");

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
    @Test
    public void shouldShowSearchForm() throws Exception {
        httpRequest("GET", "/find.html");

        servlet.service(req, resp);

        assertThat(html.toString()).contains("<form method='get' action='find.html'");
        assertThat(html.toString()).contains("<input type='text' name='name_query' value=''");
        assertThat(html.toString()).contains("<input type='submit' name='find' value='Search for person'");

        DocumentHelper.parseText(html.toString());
    }

    @Test
    public void shouldSearchForPeople() throws Exception {
        httpRequest("GET", "/find.html");
        when(req.getParameter("name_query")).thenReturn("hvasomhelst");

        servlet.service(req, resp);

        verify(personDao).findPeople("hvasomhelst");
    }

    @Test
    public void shouldDisplaySearchResults() throws Exception {
        httpRequest("GET", "/find.html");

        when(personDao.findPeople(anyString())).thenReturn(Arrays.asList(Person.withName("foo"), Person.withName("Bar")));

        servlet.service(req, resp);

        assertThat(html.toString())
            .contains("<ul>")
            .contains("<li>foo</li>")
            .contains("<li>Bar</li>")
            .contains("</ul>");
    }

    @Test
    public void shouldEchoSearchQuery() throws Exception {
        httpRequest("GET", "/find.html");
        when(req.getParameter("name_query")).thenReturn("hvasomhelst");

        servlet.service(req, resp);

        assertThat(html.toString())//
            .contains("<form")//
            .contains("value='hvasomhelst'");

    }

    private void httpRequest(String method, String pathInfo) {
        when(req.getMethod()).thenReturn(method);
        when(req.getPathInfo()).thenReturn(pathInfo);
    }

    @Before
    public void setupServlet() throws IOException {
        when(resp.getWriter()).thenReturn(new PrintWriter(html));
        servlet.setPersonDao(personDao);
    }
}
