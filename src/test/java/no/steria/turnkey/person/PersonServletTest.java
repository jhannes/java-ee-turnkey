package no.steria.turnkey.person;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.joda.time.DateMidnight;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

public class PersonServletTest {

    private PersonServlet servlet = new PersonServlet();
    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse resp = mock(HttpServletResponse.class);
    private StringWriter htmlSource = new StringWriter();
    private PersonDao personDao = mock(PersonDao.class);

    @Test
    public void shouldDisplayCreatePage() throws Exception {
        getRequest("/createPerson.html");
        servlet.service(req, resp);

        assertThat(htmlSource.toString()) //
            .contains("<form method='post' action='createPerson.html'") //
            .contains("<input type='text' name='first_name' value=''") //
            .contains("<input type='text' name='last_name' value=''") //
            .contains("<input type='text' name='birth_date' value=''") //
            .contains("<input type='submit' name='createPerson' value='Create person'") //
        ;
    }

    @Test
    public void shouldCreatePerson() throws Exception {
        postCreateFormWithParameters();
        servlet.service(req, resp);

        InOrder order = inOrder(personDao);
        order.verify(personDao).beginTransaction();
        order.verify(personDao).createPerson(Person.withName("Darth", "Vader"));
        order.verify(personDao).endTransaction(true);
    }

    @Test
    public void shouldCreatePersonWithBirthDate() throws Exception {
        postCreateFormWithParameters();
        when(req.getParameter("birth_date")).thenReturn("31.12.2001");

        servlet.service(req, resp);
        ArgumentCaptor<Person> argumentCaptor = ArgumentCaptor.forClass(Person.class);
        verify(personDao).createPerson(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getBirthDate()).isEqualTo(new DateMidnight(2001, 12, 31));
    }

    @Test
    public void shouldValidateBirthDateFormat() throws Exception {
        postCreateFormWithParameters();
        when(req.getParameter("birth_date")).thenReturn("xx.12.2001");

        servlet.service(req, resp);
        verify(personDao, never()).createPerson(any(Person.class));
        assertThat(htmlSource.toString()) //
            .contains("<div class='error'>Birth date must be on the format 'dd.mm.yyyy'</div>") //
            .contains("name='birth_date' value='xx.12.2001'") //
            ;
    }

    @Test
    public void shouldValidateFirstNameIsGiven() throws Exception {
        postCreateFormWithParameters();
        when(req.getParameter("first_name")).thenReturn("");

        servlet.service(req, resp);
        verify(personDao, never()).createPerson(any(Person.class));

        assertThat(htmlSource.toString()).contains("<div class='error'>First name must be given</div>");
    }

    @Test
    public void shouldValidateLastNameIsGiven() throws Exception {
        postCreateFormWithParameters();
        when(req.getParameter("last_name")).thenReturn("");

        servlet.service(req, resp);
        verify(personDao, never()).createPerson(any(Person.class));

        assertThat(htmlSource.toString()) //
            .contains("<form ") //
            .contains("<div class='error'>Last name must be given</div>")
            ;
    }

    @Test
    public void shouldValidateNameCannotContainHtmlCharacters() throws Exception {
        postCreateFormWithParameters();
        when(req.getParameter("first_name")).thenReturn("<&");
        when(req.getParameter("last_name")).thenReturn(">");

        servlet.service(req, resp);
        verify(personDao, never()).createPerson(any(Person.class));

        assertThat(htmlSource.toString()) //
            .contains("<form ") //
            .contains("name='first_name' value='&lt;&amp;'") //
            .contains("name='last_name' value='&gt;'") //
            .contains("<div class='error'>First name contains illegal characters</div>")
            .contains("<div class='error'>Last name contains illegal characters</div>")
            ;
    }

    @Test
    public void shouldRollbackOnError() throws Exception {
        getRequest("/findPeople.html");
        IllegalAccessError thrown = new IllegalAccessError();
        doThrow(thrown)
            .when(personDao).findPeople(anyString());

        try {
            servlet.service(req, resp);
        } catch (IllegalAccessError caught) {
            assertThat(caught).isEqualTo(thrown);
        }

        verify(personDao).endTransaction(false);
    }


    @Test
    public void shouldDisplayFindPage() throws Exception {
        getRequest("/findPeople.html");
        servlet.service(req, resp);

        assertThat(htmlSource.toString()) //
            .contains("<form method='get' action='findPeople.html'") //
            .contains("<input type='text' name='name_query' value=''") //
            .contains("<input type='submit' name='findPeople' value='Find people'") //
        ;
    }

    @Test
    public void shouldSearchForPeople() throws Exception {
        getRequest("/findPeople.html");
        when(req.getParameter("name_query")).thenReturn("vader");

        servlet.service(req, resp);

        verify(personDao).findPeople("vader");
    }

    @Test
    public void shouldDisplaySearchResult() throws Exception {
        Person person = Person.withName("Darth", "Vader");
        person.setBirthDate(new DateMidnight(2001, 12, 31));
        List<Person> people = Arrays.asList(person, Person.withName("Luke", "Skywalker"));
        when(personDao.findPeople(anyString())).thenReturn(people);
        getRequest("/findPeople.html");

        servlet.service(req, resp);

        assertThat(htmlSource.toString()) //
            .contains("<li>Darth Vader (born 31.12.2001)</li>") //
            .contains("<li>Luke Skywalker</li>") //
        ;
    }

    @Test
    public void shouldEchoNameQuery() throws Exception {
        getRequest("/findPeople.html");
        when(req.getParameter("name_query")).thenReturn("vader");

        servlet.service(req, resp);

        assertThat(htmlSource.toString()) //
            .contains("name='name_query' value='vader'") //
            ;
    }

    private void getRequest(String pathInfo) {
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn(pathInfo);
    }

    private void postCreateFormWithParameters() {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("first_name")).thenReturn("Darth");
        when(req.getParameter("last_name")).thenReturn("Vader");
        when(req.getParameter("birth_date")).thenReturn("");
    }

    @Before
    public void setupServlet() throws IOException {
        when(resp.getWriter()).thenReturn(new PrintWriter(htmlSource));
        servlet.setPersonDao(personDao);
    }

    @After
    public void shouldBeValidHtml() throws DocumentException {
        if (htmlSource.toString().length() > 0) {
            verify(resp).setContentType("text/html");
            DocumentHelper.parseText(htmlSource.toString());
        }
    }
}
