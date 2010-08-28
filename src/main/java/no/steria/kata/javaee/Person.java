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

	private <T> boolean nullSafeEquals(T a, T b) {
		return a != null ? a.equals(b) : b == null;
	}
	
	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : -1;
	}
	
	@Override
	public String toString() {
		return "Person<" + name + ">";
	}

}
