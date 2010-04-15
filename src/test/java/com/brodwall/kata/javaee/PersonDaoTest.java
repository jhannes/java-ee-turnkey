package com.brodwall.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.plus.naming.EnvEntry;

public class PersonDaoTest {

	private PersonDao personDao;

	@Test
	public void shouldFindCreatedPeople() throws Exception {
		Person person = Person.withName("Johannes Brodwall");
		personDao.createPerson(person);
		assertThat(personDao.findPeople(null)).contains(person);
	}

	@Before
	public void createPersonDao() throws NamingException {
		jdbcDataSource dataSource = new jdbcDataSource();
		dataSource.setDatabase("jdbc:hsqldb:mem:personDaoTest");
		dataSource.setUser("sa");
		new EnvEntry("jdbc/personDs", dataSource);
		personDao = new HibernatePersonDao("jdbc/personDs");
		personDao.beginTransaction();
	}

	@After
	public void rollback() {
		personDao.endTransaction(false);
	}

}
