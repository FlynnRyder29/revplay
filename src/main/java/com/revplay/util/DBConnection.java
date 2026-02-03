package com.revplay.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class DBConnection {
    private static final Logger logger = LogManager.getLogger(DBConnection.class);
    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties props = new Properties();
                InputStream is = DBConnection.class.getClassLoader()
                        .getResourceAsStream("db.properties");
                props.load(is);

                Class.forName(props.getProperty("db.driver"));
                connection = DriverManager.getConnection(
                        props.getProperty("db.url"),
                        props.getProperty("db.username"),
                        props.getProperty("db.password")
                );
                logger.info("Database connected successfully");
            } catch (Exception e) {
                logger.error("Database connection failed", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing connection", e);
            }
        }
    }
}