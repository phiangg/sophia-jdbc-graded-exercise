package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public abstract class AbstractDaoJdbc<T> {

    protected final DataSource dataSource;

    public AbstractDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected abstract T mapToResultSet(ResultSet resultSet) throws SQLException;

    protected List<T> getList(String sql, Object... parameters) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            for (Object parameter : parameters) {
                statement.setObject(parameterIndex++, parameter);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    T result = mapToResultSet(resultSet);
                    results.add(result);
                }
                return results;
            }
        }
    }

    protected T getSingle(String sql, Object... parameters) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            for (Object parameter : parameters) {
                statement.setObject(parameterIndex++, parameter);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    T result = mapToResultSet(resultSet);
                    if (resultSet.next()) {
                        throw new SQLException("Multiple results returned.");
                    }
                    return result;
                } else {
                    return null;
                }
            }
        }
    }

    protected int update(String sql, Object... parameters) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            for (Object parameter : parameters) {
                statement.setObject(parameterIndex++, parameter);
            }
            return statement.executeUpdate();
        }
    }

}
