package org.simu.client.api;

/**
 * Any failure coming from the backend, already translated into a message a human can read.
 */
public class BackendException extends RuntimeException {

    private final int status;
    private final String code;

    public BackendException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public boolean isUnauthorized() {
        return status == 401;
    }
}
