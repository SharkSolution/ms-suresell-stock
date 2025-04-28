package org.blackequity.printer.util;


public class EscPosCommands {

    public static final byte[] ESC = {27};
    public static final byte[] GS = {29};
    public static final byte[] LF = {10};
    public static final byte[] CUT = {29, 86, 65};

    // Constantes para alineación
    public static final byte ALIGN_LEFT = 0;
    public static final byte ALIGN_CENTER = 1;
    public static final byte ALIGN_RIGHT = 2;

    /**
     * Inicializa la impresora
     * @return byte[] con el comando
     */
    public byte[] initPrinter() {
        byte[] command = new byte[]{
                27, 33, 0,   // ESC ! 0 - Reset formato de texto
                27, 97, 0    // ESC a 0 - Alineación izquierda
        };
        return command;
    }

    /**
     * Establece la alineación del texto
     * @param alignment tipo de alineación (0-izquierda, 1-centro, 2-derecha)
     * @return byte[] con el comando
     */
    public byte[] setAlignment(byte alignment) {
        byte[] command = new byte[3];
        command[0] = ESC[0];
        command[1] = 97; // a
        command[2] = alignment;
        return command;
    }

    public byte[] setBold(boolean enabled) {
        byte[] command = new byte[3];
        command[0] = ESC[0];
        command[1] = 69; // E
        command[2] = (byte)(enabled ? 1 : 0);
        return command;
    }

    public byte[] setFontSize(int width, int height) {
        byte[] command = new byte[3];
        command[0] = GS[0];
        command[1] = 33; // !
        // Valor combinado: 1-2 bits para ancho, 3-4 bits para altura
        command[2] = (byte)((width & 0x01) << 4 | (height & 0x01) << 5);
        return command;
    }

    public byte[] setUnderline(boolean enabled) {
        byte[] command = new byte[3];
        command[0] = ESC[0];
        command[1] = 45; // -
        command[2] = (byte)(enabled ? 1 : 0);
        return command;
    }

    public byte[] printQRCode(String content) {
        byte[] contentBytes = content.getBytes();
        int contentLength = contentBytes.length;

        // Ajustar tamaño del código QR
        byte[] modelCommand = new byte[]{GS[0], 40, 107, 4, 0, 49, 65, 49, 0};

        // Establecer tamaño de módulo (unidad): 1-16 (por defecto 3)
        byte[] sizeCommand = new byte[]{GS[0], 40, 107, 3, 0, 49, 67, 4};

        // Nivel de corrección de errores: 48-L(7%), 49-M(15%), 50-Q(25%), 51-H(30%)
        byte[] correctionCommand = new byte[]{GS[0], 40, 107, 3, 0, 49, 69, 48};

        // Almacenar datos
        byte[] storeCommand = new byte[contentLength + 8];
        storeCommand[0] = GS[0];
        storeCommand[1] = 40;  // (
        storeCommand[2] = 107; // k
        storeCommand[3] = (byte)(contentLength + 3);
        storeCommand[4] = 0;
        storeCommand[5] = 49;  // 1
        storeCommand[6] = 80;  // P
        storeCommand[7] = 48;  // 0

        System.arraycopy(contentBytes, 0, storeCommand, 8, contentLength);

        // Imprimir código QR
        byte[] printCommand = new byte[]{GS[0], 40, 107, 3, 0, 49, 81, 48};

        // Combine all commands
        byte[] result = new byte[modelCommand.length + sizeCommand.length +
                correctionCommand.length + storeCommand.length +
                printCommand.length];

        System.arraycopy(modelCommand, 0, result, 0, modelCommand.length);
        int pos = modelCommand.length;

        System.arraycopy(sizeCommand, 0, result, pos, sizeCommand.length);
        pos += sizeCommand.length;

        System.arraycopy(correctionCommand, 0, result, pos, correctionCommand.length);
        pos += correctionCommand.length;

        System.arraycopy(storeCommand, 0, result, pos, storeCommand.length);
        pos += storeCommand.length;

        System.arraycopy(printCommand, 0, result, pos, printCommand.length);

        return result;
    }

    public byte[] cutPaper() {
        return CUT;
    }

    /**
     * Avanzar línea
     * @param lines número de líneas a avanzar
     * @return byte[] con el comando
     */
    public byte[] feedLines(int lines) {
        byte[] command = new byte[3];
        command[0] = ESC[0];
        command[1] = 100; // d
        command[2] = (byte)lines;
        return command;
    }

    public byte[] openCashDrawer() {
        byte[] command = new byte[5];
        command[0] = ESC[0];
        command[1] = 112; // p
        command[2] = 0;   // Pin 2
        command[3] = 50;  // Tiempo de activación 50ms
        command[4] = 100; // Tiempo de espera 100ms
        return command;
    }

}