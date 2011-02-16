package no.steria.kata.javaee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.joda.time.DateMidnight;

@Entity
public class Person {

    @SuppressWarnings("unused")
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    private DateMidnight birthDate;

    public static Person withName(String firstName, String lastName) {
        Person person = new Person();
        person.firstName = firstName;
        person.lastName = lastName;
        return person;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        return nullSafeEquals(getName(), ((Person)obj).getName());
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public DateMidnight getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(DateMidnight birthDate) {
        this.birthDate = birthDate;
    }

    public String getDescription() {
        if (birthDate != null) return getName() + " (born " + getBirthDate().toString("dd.MM.yyyy") + ")";
        return getName();
    }

    public int getAge() {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet");
    }
}
