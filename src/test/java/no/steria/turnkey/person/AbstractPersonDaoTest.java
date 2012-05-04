package no.steria.turnkey.person;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractPersonDaoTest {

    private PersonDao personDao;

    @Test
    public void shouldFindCreatedPeople() throws Exception {
        personDao.beginTransaction();
        Person person = Person.withName("Darth", "Vader");
        personDao.createPerson(person);
        assertThat(personDao.findPeople(null)).contains(person);
    }

    @Test
    public void shouldLimitFindToQuery() throws Exception {
        personDao.beginTransaction();
        Person matchingPerson = Person.withName("Darth", "Vader");
        Person nonMatchingPerson = Person.withName("Anakin", "Skywalker");
        personDao.createPerson(matchingPerson);
        personDao.createPerson(nonMatchingPerson);

        assertThat(personDao.findPeople("vader")) //
        .contains(matchingPerson) //
        .excludes(nonMatchingPerson);
        assertThat(personDao.findPeople("darth")) //
        .contains(matchingPerson) //
        .excludes(nonMatchingPerson);
    }

    @Test
    public void shouldCommitOrRollback() throws Exception {
        personDao.beginTransaction();
        Person commitedPerson = Person.withName("Darth", "Vader");
        personDao.createPerson(commitedPerson);
        personDao.endTransaction(true);

        personDao.beginTransaction();
        Person uncommitedPerson = Person.withName("Jar Jar", "Binks");
        personDao.createPerson(uncommitedPerson);
        personDao.endTransaction(false);

        personDao.beginTransaction();
        assertThat(personDao.findPeople(null))
        .contains(commitedPerson)
        .excludes(uncommitedPerson);
    }

    @Before
    public void setupPersonDao() throws Exception {
        personDao = createPersonDao();
    }

    protected abstract PersonDao createPersonDao() throws Exception;


}
