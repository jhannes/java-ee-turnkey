package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.Test;
import org.mortbay.jetty.plus.naming.EnvEntry;

public class HibernatePersonDaoTest {

    @Test
    public void shouldFindCreatedPeople() throws Exception {
        PersonDao personDao = createPersonDao();

        personDao.beginTransaction();
        Person person = Person.withName("Darth");
        personDao.createPerson(person);
        assertThat(personDao.findPeople(null)).contains(person);
    }

    private PersonDao createPersonDao() throws NamingException {
        String jndiDataSource = "jdbc/testDs";

        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:test");
        dataSource.setUser("sa");
        new EnvEntry(jndiDataSource, dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "create");

        return new HibernatePersonDao(jndiDataSource);
    }

}
