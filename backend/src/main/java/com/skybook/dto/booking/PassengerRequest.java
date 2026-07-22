package com.skybook.dto.booking;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PassengerRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String dob;
    private String passport;
}
