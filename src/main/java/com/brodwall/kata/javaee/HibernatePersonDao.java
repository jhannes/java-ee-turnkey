package com.brodwall.kata.javaee;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;

public class HibernatePersonDao implements PersonDao {

	private SessionFactory sessionFactory;

	public HibernatePersonDao(String dataSourceJdbcUrl) {
		AnnotationConfiguration cfg = new AnnotationConfiguration()
			.setProperty(Environment.DATASOURCE, dataSourceJdbcUrl)
			.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread")
			.setProperty(Environment.HBM2DDL_AUTO, "update")
		;
		cfg.addAnnotatedClass(Person.class);
		sessionFactory = cfg.buildSessionFactory();
	}

	@Override
	public void createPerson(Person person) {
		getSession().save(person);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> findPeople(String nameQuery) {
		return getSession().createCriteria(Person.class).list();
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void beginTransaction() {
		getSession().beginTransaction();
	}

	@Override
	public void endTransaction(boolean commit) {
		if (commit) {
			getSession().getTransaction().commit();
		} else {
			getSession().getTransaction().rollback();
		}
	}

}
