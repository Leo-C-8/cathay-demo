package com.leo.cathay.image.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventarcPayloadDto {

    @JsonProperty("fileName")
    private String fileName;
}
