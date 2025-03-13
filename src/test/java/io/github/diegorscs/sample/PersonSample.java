package io.github.diegorscs.sample;

import io.github.diegorscs.dto.PersonRequest;
import io.github.diegorscs.model.Gender;
import io.github.diegorscs.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonSample {

    public static Person createPerson() {
        return new Person("FirstName",
                "LastName",
                "City - State - Country",
                Gender.MALE,
                "email@email.com");
    }

    public static PersonRequest createPersonRequest() {
        return new PersonRequest("FirstNameRequest",
                "LastNameRequest",
                "CityRequest - StateRequest - CountryRequest",
                Gender.MALE,
                "email.request@email.com");
    }

    public static List<Person> createPersonList() {
        ArrayList<Person> personList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            personList.add(new Person("FirstName" + i,
                    "LastName" + i,
                    "City - State - Country",
                    Gender.MALE,
                    "email" + i + "@email.com"));
        }
        return personList;
    }
}