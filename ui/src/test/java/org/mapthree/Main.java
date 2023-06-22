package org.mapthree;

import java.io.IOException;

/**
 * The main class to start the task management application.
 */
public class Main {
    /**
     * The entry point of the application.
     *
     * @param args the command-line arguments
     * @throws IOException if an I/O error occurs during application startup
     */
    public static void main(String[] args) throws IOException {
        TaskUI cli = new TaskUI();
        try {
            cli.start();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
