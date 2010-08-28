package no.steria.kata.javaee;


public interface PersonDao {

	void createPerson(Person person);

	void beginTransaction();

	void endTransaction(boolean commit);

}
