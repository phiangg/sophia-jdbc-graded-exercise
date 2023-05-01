package com.orangeandbronze.enlistment.dao.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

/**
 * An abstract class that provides common functionality for JDBC DAOs.
 */
public abstract class AbstractDaoJdbc {

    protected DataSource dataSource;

    // A cache for storing SQL queries that have been read from files
    private final Map<String, String> sqlCache = new HashMap<>();

    /**
     * Constructs a JdbcDao object with the given data source.
     * @param dataSource the data source to be used for the DAO
     */
    public AbstractDaoJdbc (DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Reads a SQL query from a file with the given filename.
     * @param filename the name of the file containing the SQL query
     * @return the SQL query as a String
     * @throws RuntimeException if the SQL query failed to load
     */
    protected String getSql(String filename) throws IOException {
        if (!sqlCache.containsKey(filename)) {
            try (Reader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filename))))) {
                StringBuilder queryBuilder = new StringBuilder();
                int i;
                while ((i = reader.read()) > 0) {
                    queryBuilder.append((char) i);
                }
                sqlCache.put(filename, queryBuilder.toString());
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load SQL query from file: " + filename, ex);
            }
        }
        return sqlCache.get(filename);
    }

    /**
     * Prepares a PreparedStatement object using a SQL query read from a file with the given filename.
     * @param sqlFile the name of the file containing the SQL query
     * @return a PreparedStatement object with the prepared SQL query
     * @throws RuntimeException if there was a problem processing the SQL query
     */
    protected PreparedStatement prepareStatementFromFile(String sqlFile) throws IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(getSql(sqlFile));
        } catch (Exception ex) {
            throw new RuntimeException(
                    String.format("Failed to prepare SQL statement from file %s", sqlFile),
                    ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ex) {
                // ignore
            }
        }

        return preparedStatement;
    }

    /**
     * Prepares a PreparedStatement object using the given SQL query.
     * @paramsql the SQL query to be prepared
     * @return a PreparedStatement object with the prepared SQL query
     * @throws RuntimeException if there was a problem processing the SQL query
     */
    public static PreparedStatement prepareStatement(DataSource dataSource, String query) throws IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);
        } catch (Exception ex) {
            throw new RuntimeException(
                    String.format("Failed to prepare SQL statement: %s", query),
                    ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ex) {
                // ignore
            }
        }

        return preparedStatement;
    }

}