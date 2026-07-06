package com.bnpaper.agento.common.error;

/**
 * Canonical set of error codes returned by the API. The HTTP status is derived
 * from the code so that clients can rely on a stable, machine-readable value.
 */
public enum ErrorCode {
    VALIDATION_ERROR,
    RESOURCE_NOT_FOUND,
    AGENT_SERVICE_UNAVAILABLE,
    AGENT_GENERATION_FAILED,
    INTERNAL_ERROR
}
