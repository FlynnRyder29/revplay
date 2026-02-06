package com.revplay;
import com.revplay.ui.MenuHandler;
import com.revplay.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("RevPlay Application Starting...");

        // Test DB connection
        if (DBConnection.getConnection() == null) {
            logger.error("Failed to connect to database. Exiting.");
            System.exit(1);
        }

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.closeConnection();
            logger.info("Application shutdown complete");
        }));

        // Start menu
        new MenuHandler().start();
    }
}