package io.github.diegorscs.model;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Masculino"), FEMALE("Feminino");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

}
