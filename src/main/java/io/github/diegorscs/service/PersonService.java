package io.github.diegorscs.service;

import io.github.diegorscs.model.Person;

import java.util.List;

public interface PersonService {
    List<Person> findAll();
    Person findById(Long id);
    Person create(Person person);
    Person update(Long id, Person person);
    void delete(Long id);
    Person findByEmail(String email);
    List<Person> findByLikeName(String name);
}
