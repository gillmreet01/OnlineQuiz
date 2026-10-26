package com.quiz.dao;

/**
 * Thrown when an XML file cannot be read or written.
 * It is a RuntimeException so servlets do not need try/catch everywhere;
 * web.xml sends any uncaught exception to the friendly error page.
 */
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class DataException extends RuntimeException {

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
