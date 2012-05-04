package no.steria.turnkey.person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class HashMapPersonDao implements PersonDao {

    private int index;
    private Map<Integer,Person> dataStore = new HashMap<>();
    private ThreadLocal<Map<Integer, Person>> transactionState = new ThreadLocal<>();

    public HashMapPersonDao(String dataSource) {
        try {
            new InitialContext().lookup(dataSource);
        } catch (NamingException e) {
            throw new RuntimeException("Please ensure that the datasource is available: " + dataSource);
        }
    }

    @Override
    public void createPerson(Person person) {
        this.transactionState.get().put(index++, person);
    }

    @Override
    public List<Person> findPeople(String nameQuery) {
        ArrayList<Person> result = new ArrayList<>(dataStore.values());
        result.addAll(transactionState.get().values());
        if (nameQuery != null) filter(result, nameQuery);
        return result;
    }

    protected void filter(ArrayList<Person> result, String nameQuery) {
        for (Iterator<Person> iterator = result.iterator(); iterator.hasNext();) {
            Person person = iterator.next();
            if (!person.getName().toUpperCase().contains(nameQuery.toUpperCase())) {
                iterator.remove();
            }
        }
    }

    @Override
    public void beginTransaction() {
        transactionState.set(new HashMap<Integer,Person>());
    }

    @Override
    public void endTransaction(boolean commit) {
        if (commit) {
            dataStore.putAll(transactionState.get());
        }
        transactionState.set(null);
    }

}
