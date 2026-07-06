package com.bnpaper.agento.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Consistent error payload shape:
 * <pre>
 * { "error": { "code": "...", "message": "...", "details": { } } }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(ErrorBody error) {

    public static ApiError of(ErrorCode code, String message, Map<String, Object> details) {
        return new ApiError(new ErrorBody(code.name(), message, details));
    }

    public static ApiError of(ErrorCode code, String message) {
        return of(code, message, null);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorBody(String code, String message, Map<String, Object> details) {
    }
}
