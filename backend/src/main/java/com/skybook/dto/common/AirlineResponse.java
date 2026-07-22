package com.skybook.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirlineResponse {
    private String id;
    private String code;
    private String name;
    private String country;
}
