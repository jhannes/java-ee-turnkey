package com.brodwall.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PersonTest {

	@Test
	public void shouldGetName() throws Exception {
		assertThat(Person.withName("Johannes").getName()).isEqualTo("Johannes");
	}

	@Test
	public void shouldBaseEqualsOnName() throws Exception {
		assertThat(Person.withName("Johannes"))
			.isEqualTo(Person.withName("Johannes"))
			.isNotEqualTo(Person.withName("Something else"))
			.isNotEqualTo(new Object())
			.isNotEqualTo(Person.withName(null))
			.isNotEqualTo(null);

		assertThat(Person.withName(null))
			.isEqualTo(Person.withName(null))
			.isNotEqualTo(Person.withName("Johannes"));
	}

	@Test
	public void shouldBaseHashCodeOnName() throws Exception {
		assertThat(Person.withName("Johannes").hashCode()).as("hashCode")
			.isEqualTo(Person.withName("Johannes").hashCode())
			.isNotEqualTo(Person.withName("Something else").hashCode())
			.isNotEqualTo(Person.withName(null).hashCode());
	}
}
