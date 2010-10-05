package no.steria.kata.javaee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {

    @SuppressWarnings("unused")
    @Id @GeneratedValue
    private Long id;

    private String name;

    public static Person withName(String name) {
        Person person = new Person();
        person.name = name;
        return person;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        return nullSafeEquals(name, ((Person)obj).name);
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : -1;
    }

    private boolean nullSafeEquals(String a, String b) {
        return a != null ? a.equals(b) : b == null;
    }

    @Override
    public String toString() {
        return "Person<" + getName() + ">";
    }

}
