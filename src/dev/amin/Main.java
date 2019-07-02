package dev.amin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Dispatcher dispatcher = new Dispatcher("192.168.1.100", 11111);
        
        try {
            dispatcher.Dispatch("...");
            LOGGER.log(Level.FINEST, "The file was sent successfully!");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "There was a problem dispatching file!" + " - " + e.getMessage());
        }
    }
}
