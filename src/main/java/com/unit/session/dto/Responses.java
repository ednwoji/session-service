package com.unit.session.dto;

public enum Responses {

    USER_CREATED("00", "User Created Successfully"),
    DUPLICATE_USER("01", "User already exists"),
    SPACE_CREATED("00", "Space added successfully"),
    UNEXPECTED_ERROR("90", "Unexpected error occurred"),
    WRONG_USERNAME("02", "Wrong User ID"),
    WRONG_PASSWORD("03", "Wrong Password"),
    WRONG_CREDENTIALS("04", "Username and password does not match");




    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    Responses(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
