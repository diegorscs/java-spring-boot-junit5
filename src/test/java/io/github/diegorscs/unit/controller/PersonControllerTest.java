package io.github.diegorscs.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.diegorscs.dto.GenderResponse;
import io.github.diegorscs.exceptions.ResourceAlreadyExistsException;
import io.github.diegorscs.exceptions.ResourceNotFoundException;
import io.github.diegorscs.model.Gender;
import io.github.diegorscs.model.Person;
import io.github.diegorscs.sample.PersonSample;
import io.github.diegorscs.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonService personService;

    @Test
    void shouldReturnSavedPerson_whenCreatePerson() throws Exception {
        Person person = PersonSample.createPerson();
        given(personService.create(any(Person.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(
                post("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person))
        );

        response
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(person.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(person.getLastName())))
                .andExpect(jsonPath("$.address", is(person.getAddress())))
                .andExpect(jsonPath("$.gender", is(person.getGender().toString())))
                .andExpect(jsonPath("$.email", is(person.getEmail())));
    }

    @Test
    void shouldReturnConflict_whenCreatePerson() throws Exception {
        Person person = PersonSample.createPerson();
        doThrow(ResourceAlreadyExistsException.class).when(personService).create(person);

        ResultActions response = mockMvc.perform(
                post("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person))
        );

        response
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    void shouldReturnPersonsList_whenFindAllPersons() throws Exception {
        List<Person> personList = PersonSample.createPersonList();
        given(personService.findAll()).willReturn(personList);

        ResultActions response = mockMvc.perform(get("/api/v1/persons"));

        response
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(personList.size())));
    }

    @Test
    void shouldReturnAPerson_whenFindById() throws Exception {
        Long personId = 1L;
        Person person = PersonSample.createPerson();
        given(personService.findById(personId)).willReturn(person);

        ResultActions response = mockMvc.perform(get("/api/v1/persons/{id}", personId));

        response
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(person.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(person.getLastName())))
                .andExpect(jsonPath("$.address", is(person.getAddress())))
                .andExpect(jsonPath("$.gender", is(person.getGender().toString())))
                .andExpect(jsonPath("$.email", is(person.getEmail())));
    }

    @Test
    void shouldReturnNotFound_whenFindById() throws Exception {
        Long personId = 1L;
        doThrow(ResourceNotFoundException.class).when(personService).findById(personId);

        ResultActions response = mockMvc.perform(get("/api/v1/persons/{id}", personId));

        response
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldReturnAUpdatedPerson_whenUpdatePerson() throws Exception {
        Long personId = 1L;
        given(personService.update(anyLong(), any(Person.class)))
                .willAnswer(invocation -> invocation.getArgument(1));

        Person updatedPerson = new Person("FirstName Updated",
                "LastName Updated",
                "City Updated - State Updated - Country Updated",
                Gender.MALE,
                "email.updated@email.com");

        ResultActions response = mockMvc.perform(put("/api/v1/persons/{id}", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPerson))
        );

        response
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(updatedPerson.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedPerson.getLastName())))
                .andExpect(jsonPath("$.address", is(updatedPerson.getAddress())))
                .andExpect(jsonPath("$.gender", is(updatedPerson.getGender().toString())))
                .andExpect(jsonPath("$.email", is(updatedPerson.getEmail())));
    }

    @Test
    void shouldReturnNotFound_whenUpdatePerson() throws Exception {
        Long personId = 1L;
        doThrow(ResourceNotFoundException.class).when(personService).update(anyLong(), any(Person.class));

        Person updatedPerson = new Person("FirstName Updated",
                "LastName Updated",
                "City Updated - State Updated - Country Updated",
                Gender.MALE,
                "email.updated@email.com");

        ResultActions response = mockMvc.perform(put("/api/v1/persons/{id}", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPerson))
        );

        response
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    
    @Test
    void shouldReturnNoContent_whenDelete() throws Exception {
        Long personId = 1L;
        willDoNothing().given(personService).delete(personId);

        ResultActions response = mockMvc.perform(delete("/api/v1/persons/{id}", personId));

        response
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldReturnNotFound_whenDelete() throws Exception {
        Long personId = 1L;
        doThrow(ResourceNotFoundException.class).when(personService).delete(personId);

        ResultActions response = mockMvc.perform(delete("/api/v1/persons/{id}", personId));

        response
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldReturnAPerson_whenFindByEmail() throws Exception {
        String email = "email@email.com";
        Person person = PersonSample.createPerson();
        given(personService.findByEmail(email)).willReturn(person);

        ResultActions response = mockMvc.perform(
                get("/api/v1/persons/email")
                        .param("value", email));

        response
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(person.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(person.getLastName())))
                .andExpect(jsonPath("$.address", is(person.getAddress())))
                .andExpect(jsonPath("$.gender", is(person.getGender().toString())))
                .andExpect(jsonPath("$.email", is(person.getEmail())));
    }

    @Test
    void shouldReturnNotFound_whenFindByEmail() throws Exception {
        String email = "email@email.com";
        doThrow(ResourceNotFoundException.class).when(personService).findByEmail(email);

        ResultActions response = mockMvc.perform(
                get("/api/v1/persons/email")
                        .param("value", email));

        response
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldReturnListPerson_whenFindByLikeName() throws Exception {
        String termSearch = "FirstName";
        List<Person> personList = PersonSample.createPersonList();
        given(personService.findByLikeName(termSearch)).willReturn(personList);

        ResultActions response = mockMvc.perform(get("/api/v1/persons/like-name")
                .param("term", termSearch));

        response
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(personList.size())));
    }

    @Test
    void shouldReturnListGender_whenListGender() throws Exception {
        List<GenderResponse> genderResponseList = Arrays.stream(Gender.values()).map(gender ->
                new GenderResponse(gender.name(), gender.getDescription())).toList();

        ResultActions response = mockMvc.perform(get("/api/v1/persons/gender"));

        response
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(genderResponseList.size())));
    }
}
