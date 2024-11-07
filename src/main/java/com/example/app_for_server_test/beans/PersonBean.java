package com.example.app_for_server_test.beans;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.app_for_server_test.test_objects.Person;

import jakarta.faces.view.ViewScoped;

@Component
@ViewScoped
public class PersonBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2005938575818502919L;
	
	private List<Person> persons;

    public PersonBean() {
        persons = new ArrayList<>();
        persons.add(new Person("John", "Doe", 30));
        persons.add(new Person("Jane", "Doe", 28));
        persons.add(new Person("Mike", "Smith", 25));
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
