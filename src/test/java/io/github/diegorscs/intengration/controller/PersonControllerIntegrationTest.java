package io.github.diegorscs.intengration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.diegorscs.dto.GenderResponse;
import io.github.diegorscs.dto.PersonRequest;
import io.github.diegorscs.dto.PersonResponse;
import io.github.diegorscs.exceptions.ExceptionResponse;
import io.github.diegorscs.intengration.testcontainers.AbstractIntegrationTest;
import io.github.diegorscs.model.Gender;
import io.github.diegorscs.model.Person;
import io.github.diegorscs.repository.PersonRepository;
import io.github.diegorscs.sample.PersonSample;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class PersonControllerIntegrationTest extends AbstractIntegrationTest {

    private RequestSpecification requestSpecification;
    private final String CONTENT_TYPE = "application/json";

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void init() {
        requestSpecification = new RequestSpecBuilder()
                .setBasePath("/api/v1/persons")
                .setPort(port)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
        personRepository.deleteAllInBatch();
    }

    @Test
    void shouldReturnAPerson_whenCreate() throws JsonProcessingException {
        PersonRequest personToBeCreated = PersonSample.createPersonRequest();

        String responseBody = given()
                .spec(requestSpecification)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE)
                .body(personToBeCreated)
                .when()
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        PersonResponse createdPerson = objectMapper.readValue(responseBody, PersonResponse.class);

        assertThat(createdPerson)
                .isNotNull()
                .satisfies(person -> {
                    assertThat(person.getId()).isNotNull().isGreaterThan(0); // Confirma ID válido
                    assertThat(person.getFirstName()).isEqualTo(personToBeCreated.getFirstName());
                    assertThat(person.getLastName()).isEqualTo(personToBeCreated.getLastName());
                    assertThat(person.getAddress()).isEqualTo(personToBeCreated.getAddress());
                    assertThat(person.getGender()).isEqualTo(personToBeCreated.getGender());
                    assertThat(person.getEmail()).isEqualTo(personToBeCreated.getEmail());
                });
    }

    @Test
    void shouldReturnConflict_whenCreatingAPersonWithAnEmailThatAlreadyExists() throws JsonProcessingException {
        Person personToBeSaved = PersonSample.createPerson();
        personRepository.save(personToBeSaved);

        PersonRequest personRequest = PersonSample.createPersonRequest();
        personRequest.setEmail(personToBeSaved.getEmail());

        String responseBody = given()
                .spec(requestSpecification)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE)
                .body(personRequest)
                .when()
                .post()
                .then()
                .statusCode(409)
                .extract()
                .body()
                .asString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(responseBody, ExceptionResponse.class);

        assertThat(exceptionResponse)
                .extracting(ExceptionResponse::getMessage)
                .isEqualTo("Person already exist with given e-Mail: " + personRequest.getEmail());
    }


    @Test
    void shouldReturnUpdatedPerson_whenUpdate() throws JsonProcessingException {
        Person personToSave = PersonSample.createPerson();
        Person savedPerson = personRepository.save(personToSave);

        Long personId = savedPerson.getId();
        PersonRequest personToUpdate = PersonSample.createPersonRequest();

        String responseBody = given()
                .spec(requestSpecification)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE)
                .pathParam("id", personId)
                .body(personToUpdate)
                .when()
                .put("/{id}")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonResponse updatedPerson = objectMapper.readValue(responseBody, PersonResponse.class);

        assertThat(updatedPerson)
                .isNotNull()
                .extracting(
                        PersonResponse::getId,
                        PersonResponse::getFirstName,
                        PersonResponse::getLastName,
                        PersonResponse::getAddress,
                        PersonResponse::getGender,
                        PersonResponse::getEmail)
                .containsExactly(
                        personId,
                        personToUpdate.getFirstName(),
                        personToUpdate.getLastName(),
                        personToUpdate.getAddress(),
                        personToUpdate.getGender(),
                        personToUpdate.getEmail());

    }

    @Test
    void shouldReturnNotFound_whenUpdatingAPersonWithNonExistentId() throws JsonProcessingException {
        Person personToBeSaved = PersonSample.createPerson();
        personRepository.save(personToBeSaved);
        Long nonExistentId = 15L;

        PersonRequest personRequest = PersonSample.createPersonRequest();

        String responseBody = given()
                .spec(requestSpecification)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE)
                .pathParam("id", nonExistentId)
                .body(personRequest)
                .when()
                .put("{id}")
                .then()
                .statusCode(404)
                .extract()
                .body()
                .asString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(responseBody, ExceptionResponse.class);

        assertThat(exceptionResponse)
                .extracting(ExceptionResponse::getMessage)
                .isEqualTo("No records found for this ID!");
    }

    @Test
    void shouldReturnAPerson_whenFindById() throws JsonProcessingException {
        Person personToBeSaved = PersonSample.createPerson();
        Person savedPerson = personRepository.save(personToBeSaved);
        Long personId = savedPerson.getId();

        String responseBody = given()
                .spec(requestSpecification)
                .accept(CONTENT_TYPE)
                .pathParam("id", personId)
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonResponse foundPerson = objectMapper.readValue(responseBody, PersonResponse.class);


        assertThat(foundPerson)
                .isNotNull()
                .satisfies(person -> {
                    assertThat(person.getId()).isEqualTo(personId);
                    assertThat(person.getFirstName()).isEqualTo(savedPerson.getFirstName());
                    assertThat(person.getLastName()).isEqualTo(savedPerson.getLastName());
                    assertThat(person.getAddress()).isEqualTo(savedPerson.getAddress());
                    assertThat(person.getGender()).isEqualTo(savedPerson.getGender());
                    assertThat(person.getEmail()).isEqualTo(savedPerson.getEmail());
                });
    }

    @Test
    void shouldReturnNotFound_whenFindByIdWithNonExistentId() throws JsonProcessingException {
        Person personToBeSaved = PersonSample.createPerson();
        personRepository.save(personToBeSaved);
        Long nonExistentId = 15L;


        String responseBody = given()
                .spec(requestSpecification)
                .pathParam("id", nonExistentId)
                .when()
                .get("{id}")
                .then()
                .statusCode(404)
                .extract()
                .body()
                .asString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(responseBody, ExceptionResponse.class);

        assertThat(exceptionResponse)
                .extracting(ExceptionResponse::getMessage)
                .isEqualTo("No records found for this ID!");
    }

    @Test
    void shouldReturnAPersonsList_whenFindAll() throws JsonProcessingException {
        List<Person> personList = PersonSample.createPersonList();
        personRepository.saveAll(personList);

        String responseBody = given()
                .spec(requestSpecification)
                .accept(CONTENT_TYPE)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        List<PersonResponse> people = Arrays.asList(objectMapper.readValue(responseBody, PersonResponse[].class));

        assertThat(people)
                .hasSize(personList.size())
                .extracting(PersonResponse::getFirstName, PersonResponse::getLastName, PersonResponse::getAddress, PersonResponse::getGender, PersonResponse::getEmail)
                .containsExactlyInAnyOrder(
                        tuple(personList.get(0).getFirstName(), personList.get(0).getLastName(), personList.get(0).getAddress(), personList.get(0).getGender(), personList.get(0).getEmail()),
                        tuple(personList.get(1).getFirstName(), personList.get(1).getLastName(), personList.get(1).getAddress(), personList.get(1).getGender(), personList.get(1).getEmail()),
                        tuple(personList.get(2).getFirstName(), personList.get(2).getLastName(), personList.get(2).getAddress(), personList.get(2).getGender(), personList.get(2).getEmail())
                );
    }

    @Test
    void shouldDoNothing_whenDelete() {
        Person personToBeSaved = PersonSample.createPerson();
        Person savedPerson = personRepository.save(personToBeSaved);
        Long personId = savedPerson.getId();

        given()
                .spec(requestSpecification)
                .pathParam("id", personId)
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);

        boolean personExists = personRepository.existsById(personId);
        assertThat(personExists).isFalse();
    }

    @Test
    void shouldReturnNotFound_whenDeleteNonExistsId() throws JsonProcessingException {
        Long nonExistsID = 15L;

        String responseBody = given()
                .spec(requestSpecification)
                .accept(CONTENT_TYPE)
                .pathParam("id", nonExistsID)
                .when()
                .delete("{id}")
                .then()
                .statusCode(404)
                .extract()
                .body()
                .asString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(responseBody, ExceptionResponse.class);

        assertThat(exceptionResponse)
                .extracting(ExceptionResponse::getMessage)
                .isEqualTo("No records found for this ID!");
    }

    
    @Test
    void shouldReturnAPerson_whenFindByEmail() throws JsonProcessingException {
        Person personToBeSaved = PersonSample.createPerson();
        Person savedPerson = personRepository.save(personToBeSaved);
        String personEmail = savedPerson.getEmail();

        String responseBody = given()
                .spec(requestSpecification)
                .accept(CONTENT_TYPE)
                .param("value", personEmail)
                .when()
                .get("/email")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonResponse foundPerson = objectMapper.readValue(responseBody, PersonResponse.class);

        assertThat(foundPerson)
                .isNotNull()
                .satisfies(person -> {
                    assertThat(person.getId()).isGreaterThan(0);
                    assertThat(person.getFirstName()).isEqualTo(savedPerson.getFirstName());
                    assertThat(person.getLastName()).isEqualTo(savedPerson.getLastName());
                    assertThat(person.getAddress()).isEqualTo(savedPerson.getAddress());
                    assertThat(person.getGender()).isEqualTo(savedPerson.getGender());
                    assertThat(person.getEmail()).isEqualTo(savedPerson.getEmail());
                });
    }

    @Test
    void shouldReturnNotFound_whenFindByEmailWithNonExistsEmail() throws JsonProcessingException {
        String nonExistsEmail = "nonexistsemail@email.com";

        String responseBody = given()
                .spec(requestSpecification)
                .accept(CONTENT_TYPE)
                .param("value", nonExistsEmail)
                .when()
                .get("/email")
                .then()
                .statusCode(404)
                .extract()
                .body()
                .asString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(responseBody, ExceptionResponse.class);

        assertThat(exceptionResponse)
                .extracting(ExceptionResponse::getMessage)
                .isEqualTo("No records found for this e-Mail: " + nonExistsEmail);
    }

    @Test
    void shouldReturnAPersonsList_whenLikeName() throws JsonProcessingException {
        List<Person> personList = PersonSample.createPersonList();
        personRepository.saveAll(personList);
        String termSearch = "FirstName";

        String responseBody = given()
                .spec(requestSpecification)
                .accept(CONTENT_TYPE)
                .param("term", termSearch)
                .when()
                .get("/like-name")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        List<PersonResponse> people = Arrays.asList(objectMapper.readValue(responseBody, PersonResponse[].class));

        assertThat(people)
                .hasSize(personList.size())
                .extracting(PersonResponse::getFirstName, PersonResponse::getLastName, PersonResponse::getAddress, PersonResponse::getGender, PersonResponse::getEmail)
                .containsExactlyInAnyOrder(
                        tuple(personList.get(0).getFirstName(), personList.get(0).getLastName(), personList.get(0).getAddress(), personList.get(0).getGender(), personList.get(0).getEmail()),
                        tuple(personList.get(1).getFirstName(), personList.get(1).getLastName(), personList.get(1).getAddress(), personList.get(1).getGender(), personList.get(1).getEmail()),
                        tuple(personList.get(2).getFirstName(), personList.get(2).getLastName(), personList.get(2).getAddress(), personList.get(2).getGender(), personList.get(2).getEmail())
                );
    }

    @Test
    void shouldReturnListGender_whenListGender() throws JsonProcessingException {
        List<GenderResponse> genderResponseList = Arrays.stream(Gender.values()).map(gender ->
                new GenderResponse(gender.name(), gender.getDescription())
        ).toList();

        String responseBody = given()
                .spec(requestSpecification)
                .accept(CONTENT_TYPE)
                .when()
                .get("/gender")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        List<GenderResponse> genders = Arrays.asList(objectMapper.readValue(responseBody, GenderResponse[].class));

        assertThat(genders)
                .hasSize(genderResponseList.size())
                .extracting(GenderResponse::getName, GenderResponse::getDescription)
                .containsExactlyInAnyOrder(
                        tuple(genderResponseList.get(0).getName(), genderResponseList.get(0).getDescription()),
                        tuple(genderResponseList.get(1).getName(), genderResponseList.get(1).getDescription())
                );
    }
}
