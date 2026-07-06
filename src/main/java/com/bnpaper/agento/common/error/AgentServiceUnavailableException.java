package com.bnpaper.agento.common.error;

/**
 * Raised when the downstream {@code agento-agent} service cannot be reached
 * (connection refused, timeout, 5xx, etc.).
 */
public class AgentServiceUnavailableException extends RuntimeException {

    public AgentServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public AgentServiceUnavailableException(String message) {
        super(message);
    }
}
