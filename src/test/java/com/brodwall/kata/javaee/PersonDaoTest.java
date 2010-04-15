package com.brodwall.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PersonDaoTest {

	@Test
	public void shouldFindCreatedPeople() throws Exception {
		PersonDao personDao = createPersonDao();
		Person person = Person.withName("Johannes Brodwall");
		personDao.createPerson(person);
		assertThat(personDao.findPeople(null)).contains(person);
	}

	private PersonDao createPersonDao() {
		// TODO Auto-generated method stub
		return null;
	}

}
