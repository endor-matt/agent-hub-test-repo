package com.skybook.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirportResponse {
    private String id;
    private String iataCode;
    private String name;
    private String city;
    private String country;
    private String timezone;
}
