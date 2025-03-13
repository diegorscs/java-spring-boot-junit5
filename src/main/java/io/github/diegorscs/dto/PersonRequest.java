package io.github.diegorscs.dto;

import io.github.diegorscs.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequest {
    private String firstName;
    private String lastName;
    private String address;
    private Gender gender;
    private String email;
}
