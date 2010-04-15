package com.brodwall.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PersonTest {

	@Test
	public void shouldGetName() throws Exception {
		assertThat(Person.withName("Johannes").getName()).isEqualTo("Johannes");
	}

}
