package com.brodwall.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentHelper;
import org.junit.Test;

public class PersonServletTest {

	@Test
	public void shouldDisplayCreateForm() throws Exception {
		PersonServlet personServlet = new PersonServlet();

		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp = mock(HttpServletResponse.class);

		when(req.getMethod()).thenReturn("GET");

		StringWriter pageSource = new StringWriter();
		when(resp.getWriter()).thenReturn(new PrintWriter(pageSource));

		personServlet.service(req, resp);

		verify(resp).setContentType("text/html");
		assertThat(pageSource.toString())
			.contains("<form method='post' action='create.html'")
			.contains("<input type='text' name='full_name' value=''")
			.contains("<input type='submit' name='create' value='Create person'");
		DocumentHelper.parseText(pageSource.toString());
	}

}
