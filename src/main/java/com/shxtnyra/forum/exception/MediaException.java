package com.shxtnyra.forum.exception;

public class MediaException extends RuntimeException {
    public MediaException(String message) {
        super(message);
    }

    public MediaException(String message, Throwable cause) {
        super(message, cause);
    }
} 