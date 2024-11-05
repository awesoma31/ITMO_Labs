package org.awesoma.back.util;

public enum UserType {
    ADMIN("ADMIN"), TEACHER("TEACHER"), STUDENT("STUDENT"), GUEST("USER");

    private final String type;

    UserType(String string) {
        type = string;
    }

    @Override
    public String toString() {
        return type;
    }
}