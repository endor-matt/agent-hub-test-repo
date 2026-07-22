package com.skybook.dto.booking;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelBookingRequest {
    @Size(max = 500)
    private String reason;
}
