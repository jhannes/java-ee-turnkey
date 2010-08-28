package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class PersonServletTest {
	
	private PersonServlet servlet = new PersonServlet();
	private HttpServletRequest req = mock(HttpServletRequest.class);
	private HttpServletResponse resp = mock(HttpServletResponse.class);
	private PersonDao personDao = mock(PersonDao.class);
	private StringWriter htmlSource = new StringWriter();

	@Test
	public void shouldShowCreatePage() throws Exception {
		getRequest("/createPerson.html");
		servlet.service(req, resp);
		
		verify(resp).setContentType("text/html");
		assertThat(htmlSource.toString())
			.contains("<form action='createPerson.html' method='post'")
			.contains("<input type='text' name='full_name' value=''")
			.contains("<input type='submit' name='createPerson' value='Create person'");
	}

	@Test
	public void shouldCreatePerson() throws Exception {
		when(req.getMethod()).thenReturn("POST");
		when(req.getParameter("full_name")).thenReturn("Darth Vader");
		
		servlet.service(req, resp);
		
		InOrder order = inOrder(personDao);
		
		order.verify(personDao).beginTransaction();
		order.verify(personDao).createPerson(Person.withName("Darth Vader"));
		order.verify(personDao).endTransaction(true);

		verify(resp).sendRedirect("/");
	}
	
	@Test
	public void shouldRollbackOnError() throws Exception {
		when(req.getMethod()).thenReturn("POST");
		when(req.getParameter("full_name")).thenReturn("Darth Vader");
		doThrow(new RuntimeException()).when(personDao).createPerson(any(Person.class));
		try {
			servlet.service(req, resp);
		} catch (Exception e) {
		}
		
		verify(personDao).endTransaction(false);
	}

	@Test
	public void shouldValidateNameIsRequired() throws Exception {
		when(req.getMethod()).thenReturn("POST");
		when(req.getParameter("full_name")).thenReturn("");
		
		servlet.service(req, resp);
		verify(personDao, never()).createPerson(any(Person.class));
		verify(resp).setContentType("text/html");
		assertThat(htmlSource.toString())
			.contains("<form")
			.contains("You must provide a name");
	}
	
	@Test
	public void shouldNotAllowSpecialCharactersInName() throws Exception {
		when(req.getMethod()).thenReturn("POST");
		when(req.getParameter("full_name")).thenReturn("<");
		
		servlet.service(req, resp);
		verify(personDao, never()).createPerson(any(Person.class));
		assertThat(htmlSource.toString())
			.contains("Illegal character in name")
			.contains("<input type='text' name='full_name' value='&lt;'");
	}
	
	private void getRequest(String action) {
		when(req.getMethod()).thenReturn("GET");
		when(req.getPathInfo()).thenReturn(action);
	}

	@After
	public void assertValidHtml() throws DocumentException {
		if (htmlSource.toString().length() > 0) {
			DocumentHelper.parseText(htmlSource.toString());
		}
	}

	@Before
	public void setupServlet() throws IOException {
		servlet.setPersonDao(personDao);
		when(resp.getWriter()).thenReturn(new PrintWriter(htmlSource));
	}

}
