package org.wyona.webapp.models;

/**
 * Helper class that holds response message in case when an error occurs, e.g.
 * when sending greeting emails fails due to bad destination address
 */
public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
