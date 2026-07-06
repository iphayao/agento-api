package com.bnpaper.agento.common.error;

/**
 * Raised when {@code agento-agent} is reachable but returns an error or an
 * unusable response for a generation request.
 */
public class AgentGenerationFailedException extends RuntimeException {

    public AgentGenerationFailedException(String message) {
        super(message);
    }

    public AgentGenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
