package io.github.diegorscs.mapper;

import io.github.diegorscs.dto.PersonRequest;
import io.github.diegorscs.dto.PersonResponse;
import io.github.diegorscs.model.Person;

public class PersonMapper {
    public static PersonResponse toResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getGender(),
                person.getEmail()
        );
    }

    public static Person toEntity(PersonRequest request) {
        return new Person(
                request.getFirstName(),
                request.getLastName(),
                request.getAddress(),
                request.getGender(),
                request.getEmail()
        );
    }
}
