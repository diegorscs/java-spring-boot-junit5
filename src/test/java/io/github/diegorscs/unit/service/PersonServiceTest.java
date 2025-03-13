package io.github.diegorscs.unit.service;

import io.github.diegorscs.exceptions.ResourceAlreadyExistsException;
import io.github.diegorscs.exceptions.ResourceNotFoundException;
import io.github.diegorscs.model.Gender;
import io.github.diegorscs.model.Person;
import io.github.diegorscs.repository.PersonRepository;
import io.github.diegorscs.sample.PersonSample;
import io.github.diegorscs.service.impl.PersonServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    @InjectMocks
    private PersonServiceImpl personService;

    @Test
    void shouldReturnPersonObject_whenSavePerson() {
        Person personToBeSaved = PersonSample.createPerson();
        given(personRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(personRepository.save(personToBeSaved)).willReturn(personToBeSaved);

        Person savedPerson = personService.create(personToBeSaved);

        assertThat(savedPerson)
                .isNotNull()
                .extracting(Person::getFirstName, Person::getLastName,
                        Person::getAddress, Person::getGender, Person::getEmail)
                .doesNotContainNull()
                .containsExactly(personToBeSaved.getFirstName(), personToBeSaved.getLastName(),
                        personToBeSaved.getAddress(), personToBeSaved.getGender(), personToBeSaved.getEmail());
    }

    @Test
    void shouldThrowResourceAlreadyExists_whenSavePerson() {
        Person person = PersonSample.createPerson();
        given(personRepository.findByEmail(anyString())).willReturn(Optional.of(person));

        assertThatThrownBy(() -> personService.create(person))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Person already exist with given e-Mail: " + person.getEmail());
        verify(personRepository, never()).save(person);
    }

    @Test
    void shouldReturnPersonsList_whenFindAll() {
        List<Person> personListMock = PersonSample.createPersonList();
        given(personRepository.findAll()).willReturn(personListMock);

        List<Person> personList = personService.findAll();

        assertThat(personList)
                .hasSize(3)
                .extracting(Person::getFirstName, Person::getLastName, Person::getAddress, Person::getGender, Person::getEmail)
                .containsExactlyInAnyOrder(
                        tuple(personListMock.get(0).getFirstName(), personListMock.get(0).getLastName(), personListMock.get(0).getAddress(), personListMock.get(0).getGender(), personListMock.get(0).getEmail()),
                        tuple(personListMock.get(1).getFirstName(), personListMock.get(1).getLastName(), personListMock.get(1).getAddress(), personListMock.get(1).getGender(), personListMock.get(1).getEmail()),
                        tuple(personListMock.get(2).getFirstName(), personListMock.get(2).getLastName(), personListMock.get(2).getAddress(), personListMock.get(2).getGender(), personListMock.get(2).getEmail())
                );
    }

    @Test
    void shouldReturnEmptyList_whenFindAll() {
        given(personRepository.findAll()).willReturn(Collections.emptyList());

        List<Person> personList = personService.findAll();

        assertThat(personList).isEmpty();
        verify(personRepository).findAll();
    }

    @Test
    void shouldReturnAPerson_whenFindById() {
        Person person = PersonSample.createPerson();
        given(personRepository.findById(anyLong())).willReturn(Optional.of(person));

        Person foundPerson = personService.findById(1L);

        assertThat(foundPerson)
                .isNotNull()
                .extracting(Person::getFirstName, Person::getLastName, Person::getAddress, Person::getGender, Person::getEmail)
                .doesNotContainNull()
                .containsExactly(person.getFirstName(), person.getLastName(), person.getAddress(), person.getGender(), person.getEmail());
    }

    @Test
    void shouldThrowResourceNotFoundException_whenFindById() {
        given(personRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> personService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No records found for this ID!");
    }

    @Test
    void shouldUpdatedPerson_whenUpdatePerson() {
        Person personToUpdate = PersonSample.createPerson();
        given(personRepository.findById(anyLong())).willReturn(Optional.of(personToUpdate));
        personToUpdate.setFirstName("Diego");
        personToUpdate.setLastName("Ruescas");
        personToUpdate.setAddress("São Paulo - Brasil");
        personToUpdate.setGender(Gender.MALE);
        personToUpdate.setEmail("diego@email.com");
        given(personRepository.save(any(Person.class))).willReturn(personToUpdate);

        Person updatedPerson = personService.update(1L, personToUpdate);

        assertThat(updatedPerson)
                .isNotNull()
                .extracting(Person::getFirstName, Person::getLastName, Person::getAddress, Person::getGender, Person::getEmail)
                .doesNotContainNull()
                .containsExactly("Diego", "Ruescas", "São Paulo - Brasil", Gender.MALE, "diego@email.com");
    }

    @Test
    void shouldThrowResourceNotFoundException_whenUpdate() {
        Person personToUpdate = PersonSample.createPerson();
        given(personRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> personService.update(1L, personToUpdate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No records found for this ID!");
    }

    @Test
    void shouldDoNothing_whenDelete() {
        Person personToDelete = PersonSample.createPerson();
        given(personRepository.findById(anyLong())).willReturn(Optional.of(personToDelete));
        willDoNothing().given(personRepository).deleteById(anyLong());

        personService.delete(1L);

        verify(personRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldThrowResourceNotFoundException_whenDelete() {
        given(personRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> personService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No records found for this ID!");
    }

    @Test
    void shouldReturnAPerson_whenFindByEmail() {
        Person person = PersonSample.createPerson();
        given(personRepository.findByEmail(anyString())).willReturn(Optional.of(person));

        Person foundPerson = personService.findByEmail(person.getEmail());

        assertThat(foundPerson)
                .isNotNull()
                .extracting(Person::getFirstName, Person::getLastName, Person::getAddress, Person::getGender, Person::getEmail)
                .doesNotContainNull()
                .containsExactly(person.getFirstName(), person.getLastName(), person.getAddress(), person.getGender(), person.getEmail());
    }

    @Test
    void shouldThrowResourceNotFoundException_whenFindByEmail() {
        given(personRepository.findByEmail(anyString())).willReturn(Optional.empty());

        assertThatThrownBy(() -> personService.findByEmail("email"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No records found for this e-Mail: email");
    }

    @Test
    void shouldReturnAPerson_whenLikeName() {
        Person person = PersonSample.createPerson();
        given(personRepository.findByLikeName(anyString())).willReturn(List.of(person));

        List<Person> foundPerson = personService.findByLikeName(person.getFirstName());

        assertThat(foundPerson)
                .isNotEmpty()
                .first()
                .usingRecursiveComparison()
                .isEqualTo(person);
    }

    @Test
    void shouldReturnEmptyList_whenLikeName() {
        given(personRepository.findByLikeName(anyString())).willReturn(Collections.emptyList());

        List<Person> foundPerson = personService.findByLikeName("name");

        assertThat(foundPerson)
                .as("Verify no person is found with the search term")
                .isEmpty();
    }


}
