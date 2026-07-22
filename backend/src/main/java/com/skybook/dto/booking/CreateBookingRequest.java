package com.skybook.dto.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateBookingRequest {
    @NotBlank
    private String flightId;

    @NotEmpty
    @Valid
    private List<PassengerRequest> passengers;

    @NotEmpty
    private List<String> seats;

    @NotBlank
    @Email
    private String contactEmail;

    private String contactPhone;
}
