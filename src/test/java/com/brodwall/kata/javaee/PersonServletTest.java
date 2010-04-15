package com.brodwall.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

public class PersonServletTest {

	private PersonServlet personServlet = new PersonServlet();
	private HttpServletRequest req = mock(HttpServletRequest.class);
	private HttpServletResponse resp = mock(HttpServletResponse.class);
	private StringWriter pageSource = new StringWriter();

	@Before
	public void setupServlet() throws IOException {
		when(resp.getWriter()).thenReturn(new PrintWriter(pageSource));
	}

	@Test
	public void shouldDisplayCreateForm() throws Exception {
		httpRequest("GET", "/create.html");

		personServlet.service(req, resp);

		verify(resp).setContentType("text/html");
		assertThat(pageSource.toString())
			.contains("<form method='post' action='create.html'")
			.contains("<input type='text' name='full_name' value=''")
			.contains("<input type='submit' name='create' value='Create person'");
		DocumentHelper.parseText(pageSource.toString());
	}

	private void httpRequest(String method, String pathInfo) {
		when(req.getMethod()).thenReturn(method);
		when(req.getPathInfo()).thenReturn(pathInfo);
	}

	@Test
	public void shouldCreatePerson() throws Exception {
		PersonDao personDao = mock(PersonDao.class);
		personServlet.setPersonDao(personDao);

		httpRequest("POST", "/create.html");
		when(req.getParameter("full_name")).thenReturn("Johannes Brodwall");

		personServlet.service(req, resp);

		verify(personDao).createPerson(Person.withName("Johannes Brodwall"));
		verify(resp).sendRedirect(anyString());
	}

	@Test
	public void shouldDisplaySearchForm() throws Exception {
		httpRequest("GET", "/find.html");

		personServlet.service(req, resp);

		verify(resp).setContentType("text/html");
		assertThat(pageSource.toString())
			.contains("<form method='get' action='find.html'")
			.contains("<input type='text' name='name_query' value=''")
			.contains("<input type='submit' name='find' value='Search'");
		DocumentHelper.parseText(pageSource.toString());
	}

}
