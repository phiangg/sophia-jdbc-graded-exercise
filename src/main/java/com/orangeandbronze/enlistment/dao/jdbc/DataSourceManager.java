package com.orangeandbronze.enlistment.dao.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

/**
 * This class manages the DataSource used to connect to the database.
 * It ensures that only a single instance of DataSource is created and used throughout the application.
 */
public class DataSourceManager {

    // Configuration filename
    private static final String PROP_FILENAME = "pg.datasource.properties";

    // The single DataSource object
    private static DataSource dataSource;

    /**
     * Returns the single dataSource object, instantiating the
     * object if it hasn't been created yet.
     *
     * @return the DataSource object
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            // Load the configuration file containing the database connection details
            Properties prop = new Properties();
            try (Reader reader = new BufferedReader(new InputStreamReader(
                    DataSourceManager.class.getClassLoader().getResourceAsStream(PROP_FILENAME)))) {
                prop.load(reader);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load configuration file", ex);
            }

            // Create a new PGSimpleDataSource object and set its properties based on the configuration file
            PGSimpleDataSource simpleDataSource = new PGSimpleDataSource();
            simpleDataSource.setServerName(prop.getProperty("servername"));
            simpleDataSource.setDatabaseName(prop.getProperty("database"));
            simpleDataSource.setUser(prop.getProperty("user"));
            simpleDataSource.setPassword(prop.getProperty("password"));

            // Set the dataSource field to the new DataSource object
            dataSource = simpleDataSource;
        }

        // Return the DataSource object
        return dataSource;
    }

    /**
     * Returns a Connection object from the DataSource.
     *
     * @return a Connection object
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Get a Connection object from the DataSource
            connection = getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return the Connection object
        return connection;
    }

}

