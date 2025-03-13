package io.github.diegorscs.unit.repository;

import io.github.diegorscs.model.Gender;
import io.github.diegorscs.model.Person;
import io.github.diegorscs.repository.PersonRepository;
import io.github.diegorscs.sample.PersonSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@DataJpaTest
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
    }


    @Test
    void shouldReturnSavedPerson_whenSave() {
        Person personToBeSaved  = PersonSample.createPerson();

        Person savedPerson = personRepository.save(personToBeSaved );

        assertThat(savedPerson)
                .isNotNull()
                .extracting(Person::getId, Person::getFirstName, Person::getLastName,
                        Person::getAddress, Person::getGender, Person::getEmail)
                .doesNotContainNull()
                .containsExactly(savedPerson.getId(), personToBeSaved.getFirstName(), personToBeSaved.getLastName(),
                        personToBeSaved.getAddress(), personToBeSaved.getGender(), personToBeSaved.getEmail());

        Person foundPerson = personRepository.findById(savedPerson.getId())
                .orElseThrow(() -> new AssertionError("Person not found in database"));

        assertThat(foundPerson)
                .usingRecursiveComparison()
                .isEqualTo(savedPerson);
    }


    @Test
    void shouldReturnPersonList_whenFindAll() {
        Person personOne = PersonSample.createPerson();
        Person personTwo = new Person("Diego", "Ruescas", "São Paulo - Brasil", Gender.MALE, "diego@email.com");
        personRepository.saveAll(Arrays.asList(personOne, personTwo));

        List<Person> personList = personRepository.findAll();

        assertThat(personList)
                .hasSize(2)
                .extracting(Person::getFirstName, Person::getLastName, Person::getAddress, Person::getGender, Person::getEmail)
                .containsExactlyInAnyOrder(
                        tuple(personOne.getFirstName(), personOne.getLastName(), personOne.getAddress(), personOne.getGender(), personOne.getEmail()),
                        tuple(personTwo.getFirstName(), personTwo.getLastName(), personTwo.getAddress(), personTwo.getGender(), personTwo.getEmail())
                );
    }

    @Test
    void shouldReturnAPerson_whenFindById() {
        Person personToBeSaved  = PersonSample.createPerson();
        Person savedPerson = personRepository.save(personToBeSaved);

        Optional<Person> personById = personRepository.findById(savedPerson.getId());

        assertThat(personById)
                .isPresent()
                .contains(personToBeSaved );
    }

    @Test
    void shouldReturnEmptyOptional_whenFindByIdNotExists() {
        Optional<Person> personById = personRepository.findById(1L);

        assertThat(personById).isEmpty();
    }

    @Test
    void shouldReturnAPerson_whenFindByEmail() {
        Person personToBeSaved  = PersonSample.createPerson();
        personRepository.save(personToBeSaved );

        Optional<Person> personByEmail = personRepository.findByEmail(personToBeSaved .getEmail());

        assertThat(personByEmail).isPresent();
        personByEmail.ifPresent(foundPerson ->
                assertThat(foundPerson).usingRecursiveComparison().isEqualTo(personToBeSaved )
        );
    }

    @Test
    void shouldUpdateAPerson_whenUpdate() {
        Person personToBeSaved  = PersonSample.createPerson();
        Person savedPerson = personRepository.save(personToBeSaved);

        String firstNameUpdated = "Jhon";
        String lastNameUpdated = "Smith";
        String addressUpdated = "São Paulo - Brasil";
        Gender genderUpdated = Gender.MALE;
        String emailUpdated = "jhonsmith@email.com";

        Person personToUpdate = personRepository.findById(savedPerson .getId())
                .orElseThrow(() -> new AssertionError("Person not found for update"));

        personToUpdate.setFirstName(firstNameUpdated);
        personToUpdate.setLastName(lastNameUpdated);
        personToUpdate.setAddress(addressUpdated);
        personToUpdate.setGender(genderUpdated);
        personToUpdate.setEmail(emailUpdated);

        personRepository.save(personToUpdate);

        Person updatedPerson = personRepository.findById(savedPerson .getId())
                .orElseThrow(() -> new AssertionError("Person not found after update"));

        assertThat(updatedPerson)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(personToUpdate);
        assertThat(updatedPerson.getGender()).isEqualTo(genderUpdated);
    }


    @Test
    void shouldReturnAPerson_whenLikeName() {
        Person personToBeSaved = PersonSample.createPerson();
        Person savedPerson = personRepository.save(personToBeSaved);
        String searchTerm = "FirstName L";

        List<Person> personByLikeName = personRepository.findByLikeName(searchTerm);

        assertThat(personByLikeName)
                .isNotEmpty()
                .first()
                .usingRecursiveComparison()
                .isEqualTo(savedPerson);
    }

    @Test
    void shouldReturnEmpty_whenLikeNameNotExists() {
        String searchTerm = "FirstName L";

        List<Person> personByLikeName = personRepository.findByLikeName(searchTerm);

        assertThat(personByLikeName)
                .as("Verify no person is found with the search term")
                .isEmpty();
    }

    @Test
    void shouldDeleteAPerson_whenDeleteById() {
        Person personToBeSaved = PersonSample.createPerson();
        Person savedPerson = personRepository.save(personToBeSaved);

        long initialCount = personRepository.count();
        personRepository.deleteById(savedPerson.getId());

        assertThat(personRepository.existsById(savedPerson.getId())).isFalse();
        assertThat(personRepository.count()).isEqualTo(initialCount - 1);
    }
}
