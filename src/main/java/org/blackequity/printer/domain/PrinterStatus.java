package org.blackequity.printer.domain;

/**
 * Clase que representa el estado actual de la impresora
 */
public class PrinterStatus {

    private boolean connected;
    private String printerName;
    private String printerPort;
    private boolean hasPaper;
    private Boolean drawerOpen; // Usando Boolean para permitir valor nulo (estado desconocido)

    /**
     * Constructor
     *
     * @param connected estado de conexión
     * @param printerName nombre del modelo de impresora
     * @param printerPort puerto donde está conectada
     * @param hasPaper si tiene papel disponible
     */
    public PrinterStatus(boolean connected, String printerName, String printerPort, boolean hasPaper) {
        this.connected = connected;
        this.printerName = printerName;
        this.printerPort = printerPort;
        this.hasPaper = hasPaper;
        this.drawerOpen = null; // Por defecto, estado desconocido
    }

    /**
     * Constructor con estado del cajón
     */
    public PrinterStatus(boolean connected, String printerName, String printerPort,
                         boolean hasPaper, Boolean drawerOpen) {
        this.connected = connected;
        this.printerName = printerName;
        this.printerPort = printerPort;
        this.hasPaper = hasPaper;
        this.drawerOpen = drawerOpen;
    }

    // Getters y Setters

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getPrinterPort() {
        return printerPort;
    }

    public void setPrinterPort(String printerPort) {
        this.printerPort = printerPort;
    }

    public boolean isHasPaper() {
        return hasPaper;
    }

    public void setHasPaper(boolean hasPaper) {
        this.hasPaper = hasPaper;
    }

    public Boolean isDrawerOpen() {
        return drawerOpen;
    }

    public void setDrawerOpen(Boolean drawerOpen) {
        this.drawerOpen = drawerOpen;
    }
}