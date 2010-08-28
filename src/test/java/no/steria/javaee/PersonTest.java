package no.steria.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PersonTest {

	@Test
	public void factoryShoulReturnPerson() throws Exception {
		String personName = "Darth Vader";
		assertThat(Person.withName(personName).getName()).isEqualTo(personName);
	}
	
	@Test
	public void shouldBeEqualWhenNameIsEqual() throws Exception {
		assertThat(Person.withName("Darth"))
			.isEqualTo(Person.withName("Darth"))
			.isNotEqualTo(Person.withName("Anakin"))
			.isNotEqualTo(Person.withName(null))
			.isNotEqualTo(new Object())
			.isNotEqualTo(null);
		
		assertThat(Person.withName(null))
			.isEqualTo(Person.withName(null))
			.isNotEqualTo(Person.withName("Darth"));
	}
	
	
	@Test
	public void shouldBeEqualHashcodeWithEqualNames() throws Exception {
		assertThat(Person.withName("Darth").hashCode()).as("hashCode")
			.isEqualTo(Person.withName("Darth").hashCode())
			.isNotEqualTo(Person.withName("Anakin").hashCode())
			.isNotEqualTo(Person.withName(null).hashCode());
	}
}
