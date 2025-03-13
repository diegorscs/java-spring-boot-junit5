package io.github.diegorscs.service.impl;

import io.github.diegorscs.exceptions.ResourceAlreadyExistsException;
import io.github.diegorscs.exceptions.ResourceNotFoundException;
import io.github.diegorscs.model.Person;
import io.github.diegorscs.repository.PersonRepository;
import io.github.diegorscs.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public List<Person> findAll() {
        log.info("Finding all people!");
        return personRepository.findAll();
    }

    @Override
    public Person findById(Long id) {
        log.info("Finding one person!");
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
    }

    @Override
    public Person create(Person person) {
        log.info("Creating one person!");
        Optional<Person> entity = personRepository.findByEmail(person.getEmail());

        if (entity.isPresent()) {
            throw new ResourceAlreadyExistsException("Person already exist with given e-Mail: " + person.getEmail());
        }

        return personRepository.save(person);
    }

    @Override
    public Person update(Long id, Person person) {
        log.info("Updating one person!");
        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        entity.setEmail(person.getEmail());
        return personRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting one person!");
        personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        personRepository.deleteById(id);
    }

    @Override
    public Person findByEmail(String email) {
        log.info("Finding one person by email!");
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this e-Mail: " + email));
    }

    @Override
    public List<Person> findByLikeName(String name) {
        return personRepository.findByLikeName(name);
    }
}
