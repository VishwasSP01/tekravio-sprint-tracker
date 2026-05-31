package com.tekravio.tracker.exception;

public class EngineerNotAvailableException extends RuntimeException {

    public EngineerNotAvailableException(Long engineerId) {
        super("Engineer " + engineerId + " is not available for assignment");
    }
}
