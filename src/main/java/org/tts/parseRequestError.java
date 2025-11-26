package org.tts;

public class parseRequestError extends RuntimeException {
    public parseRequestError(String message) {
        super(message);
    }
}
