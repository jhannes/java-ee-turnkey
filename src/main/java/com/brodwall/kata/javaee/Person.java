package com.brodwall.kata.javaee;


public class Person {

	public static Person withName(String name) {
		Person person = new Person();
		person.name = name;
		return person;
	}

	private String name;

	public String getName() {
		return name;
	}

}
