package org.tts.ttsdeckgen;

public class parseRequestError extends RuntimeException {
    public parseRequestError(String message) {
        super(message);
    }
}
