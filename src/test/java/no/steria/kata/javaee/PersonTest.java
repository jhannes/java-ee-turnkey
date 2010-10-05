package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PersonTest {

    @Test
    public void shouldGetName() throws Exception {
        assertThat(Person.withName("Darth").getName()).isEqualTo("Darth");
    }

    @Test
    public void shouldBeEqualWhenNameIsEqual() throws Exception {
        assertThat(Person.withName("Darth")) //
            .isEqualTo(Person.withName("Darth")) //
            .isNotEqualTo(Person.withName("Anakin")) //
            .isNotEqualTo(Person.withName(null)) //
            .isNotEqualTo(new Object()) //
            .isNotEqualTo(null) //
        ;

        assertThat(Person.withName(null)) //
            .isEqualTo(Person.withName(null))
            .isNotEqualTo(Person.withName("Darth")) //
        ;
    }

}
