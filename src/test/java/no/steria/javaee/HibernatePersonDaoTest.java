package no.steria.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.plus.naming.EnvEntry;

public class HibernatePersonDaoTest {
	
	private static final boolean ROLLBACK = false;
	private static final boolean COMMIT = true;
	private PersonDao personDao;



	@Test
	public void shouldFindSavedPeople() throws Exception {
		Person luke = Person.withName("Luke Skywalker");
		personDao.beginTransaction();
		personDao.createPerson(luke);
		personDao.endTransaction(COMMIT);
		
		personDao.beginTransaction();
		personDao.endTransaction(ROLLBACK);
		
		personDao.beginTransaction();
		assertThat(personDao.findPeople(null)).contains(luke);
		personDao.endTransaction(COMMIT);		
	}
	
	@Test
	public void shouldFindBySubstring() throws Exception {
		Person luke = Person.withName("Luke Skywalker");
		Person annie = Person.withName("Anakin Skywalker");
		Person jarjar = Person.withName("Jar Jar Binks");
		
		personDao.beginTransaction();
		
		personDao.createPerson(luke);
		personDao.createPerson(annie);
		personDao.createPerson(jarjar);
		
		assertThat(personDao.findPeople("sky")).containsExactly(luke,annie);
	}
	
	@Test
	public void shouldRollBack() throws Exception {
		Person jarjar = Person.withName("Jar Jar Binks");
		personDao.beginTransaction();
		personDao.createPerson(jarjar);
		personDao.endTransaction(ROLLBACK);
		
		personDao.beginTransaction();
		assertThat(personDao.findPeople(null)).excludes(jarjar);
		personDao.endTransaction(COMMIT);		
	}
	

	
	@Before
	public void createPersonDao() throws NamingException {
		System.setProperty(Environment.HBM2DDL_AUTO, "create");
		jdbcDataSource dataSource = new jdbcDataSource();
		dataSource.setDatabase("jdbc:hsqldb:mem:testing");
		dataSource.setUser("sa");
		new EnvEntry("jdbc/daoTestDs", dataSource);
		personDao = new HibernatePersonDao("jdbc/daoTestDs");
	}

}
