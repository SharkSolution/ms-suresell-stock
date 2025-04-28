package org.blackequity.printer.domain;

/**
 * Clase que representa la respuesta a una solicitud de impresión
 */
public class PrintResponse {

    private boolean success;
    private String message;

    /**
     * Constructor
     *
     * @param success si la operación fue exitosa
     * @param message mensaje descriptivo del resultado
     */
    public PrintResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters y Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}