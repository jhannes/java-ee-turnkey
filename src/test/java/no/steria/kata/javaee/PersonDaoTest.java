package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import no.steria.kata.javaee.HibernatePersonDao;
import no.steria.kata.javaee.Person;
import no.steria.kata.javaee.PersonDao;

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

	@Test
	public void shouldFindPeopleByNameSubstring() throws Exception {
		Person person = Person.withName("Johannes Brodwall");
		Person notFoundPerson = Person.withName("Something Else");
		personDao.createPerson(person);
		personDao.createPerson(notFoundPerson);
		assertThat(personDao.findPeople("brodw"))
			.contains(person)
			.excludes(notFoundPerson);
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
