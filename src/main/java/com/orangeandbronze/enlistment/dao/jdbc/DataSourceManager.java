package com.orangeandbronze.enlistment.dao.jdbc;

import org.postgresql.ds.PGSimpleDataSource;

import com.orangeandbronze.enlistment.dao.DataAccessException;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class DataSourceManager {

    private static DataSource dataSource;

    private final static Map<String, String> sqlCache = new HashMap<>();

    private DataSourceManager() {};

    public static DataSource getDataSource() {
        if(dataSource == null) {
            Properties prop = new Properties();
            String propFileName = "pg.datasource.properties";

            try (Reader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(DataSourceManager.class.getClassLoader().getResourceAsStream(propFileName))
            ))) {
                prop.load(reader);
            } catch (IOException e) {
                throw new RuntimeException(
                        String.format("Problem reading file: %s", propFileName),
                        e
                );
            }

            PGSimpleDataSource simpleDataSource = new PGSimpleDataSource();
            simpleDataSource.setServerNames(new String[] {prop.getProperty("servername")});
            simpleDataSource.setDatabaseName(prop.getProperty("database"));
            simpleDataSource.setUser(prop.getProperty("user"));
            simpleDataSource.setPassword(prop.getProperty("password"));
            dataSource = simpleDataSource;
        }

        return dataSource;
    }

    public static String getSql(String sqlFile) {
        if(!sqlCache.containsKey(sqlFile)) {
            try (Reader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(DataSourceManager.class.getClassLoader().getResourceAsStream(sqlFile))
            ))) {

                StringBuilder builder = new StringBuilder();
                int i = 0;

                while ((i = reader.read()) > 0) {
                    builder.append((char) i);
                }

                sqlCache.put(sqlFile, builder.toString());
            } catch (IOException ex) {
                throw new DataAccessException(
                        String.format("Problem while trying to read file %s from classpath", sqlFile),
                        ex
                );
            }
        }

        return sqlCache.get(sqlFile);
    }
    public static PreparedStatement prepareStatement(DataSource ds, String sqlFile) throws DataAccessException {
        PreparedStatement preparedStatement = null;

        try {
            Connection conn = ds.getConnection();
            preparedStatement = conn.prepareStatement(getSql(sqlFile));
        } catch (SQLException ex) {
            // e.printStackTrace();
            throw new DataAccessException(
                    String.format("Problem with processing SQL query %s", sqlFile),
                    ex
            );
        }

        return preparedStatement;
    }

}