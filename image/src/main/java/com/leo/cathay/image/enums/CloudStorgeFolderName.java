package com.leo.cathay.image.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CloudStorgeFolderName {
    ORIGINAL("original"),
    THUMBNAIL("thumbnail");

    private final String value;

    CloudStorgeFolderName(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}