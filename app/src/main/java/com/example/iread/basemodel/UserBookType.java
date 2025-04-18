package com.example.iread.basemodel;

import java.io.Serializable;

public enum UserBookType implements Serializable {
    HISTORY(0),
    FAVORITE(1);

    private final int value;

    UserBookType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserBookType fromValue(int value) {
        for (UserBookType type : values()) {
            if (type.getValue() == value) return type;
        }
        throw new IllegalArgumentException("Unknown UserBookType value: " + value);
    }

}
