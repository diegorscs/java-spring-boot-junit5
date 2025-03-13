package io.github.diegorscs.controller;

import io.github.diegorscs.dto.GenderResponse;
import io.github.diegorscs.dto.PersonRequest;
import io.github.diegorscs.dto.PersonResponse;
import io.github.diegorscs.mapper.PersonMapper;
import io.github.diegorscs.model.Gender;
import io.github.diegorscs.model.Person;
import io.github.diegorscs.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static io.github.diegorscs.mapper.PersonMapper.toEntity;
import static io.github.diegorscs.mapper.PersonMapper.toResponse;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonResponse>> findAll() {
        List<Person> people = personService.findAll();
        return ResponseEntity.ok().body(people.stream().map(PersonMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> findById(@PathVariable("id") Long id) {
        Person person = personService.findById(id);
        return ResponseEntity.ok().body(toResponse(person));
    }

    @PostMapping
    public ResponseEntity<PersonResponse> create(@RequestBody PersonRequest request) {
        Person person = personService.create(toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(person));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> update(@PathVariable("id") Long id, @RequestBody PersonRequest request) {
        Person person = personService.update(id, toEntity(request));
        return ResponseEntity.ok().body(toResponse(person));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email")
    public ResponseEntity<PersonResponse> findByEmail(@RequestParam String value) {
        Person person = personService.findByEmail(value);
        return ResponseEntity.ok().body(toResponse(person));
    }

    @GetMapping("/like-name")
    public ResponseEntity<List<PersonResponse>> findByLikeName(@RequestParam String term) {
        List<Person> people = personService.findByLikeName(term);
        return ResponseEntity.ok().body(people.stream().map(PersonMapper::toResponse).toList());
    }

    @GetMapping("/gender")
    public ResponseEntity<List<GenderResponse>> listGenders() {
        List<GenderResponse> genderResponseList = Arrays.stream(Gender.values()).map(gender ->
                new GenderResponse(gender.name(), gender.getDescription())
        ).toList();
        return ResponseEntity.ok(genderResponseList);
    }

}
