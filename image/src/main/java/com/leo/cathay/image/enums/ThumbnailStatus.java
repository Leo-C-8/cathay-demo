package com.leo.cathay.image.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ThumbnailStatus {
    PROCESSING("processing"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;

    ThumbnailStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}