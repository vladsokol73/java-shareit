package ru.practicum.shareit.util.exception;


public class UnsupportedStatusException extends RuntimeException {

    public String unknownStatus;

    public UnsupportedStatusException(String unknownStatus) {
        super();
        this.unknownStatus = unknownStatus;
    }
}
