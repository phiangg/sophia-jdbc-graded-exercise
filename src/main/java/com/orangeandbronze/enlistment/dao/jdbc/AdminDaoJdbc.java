package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.dao.AdminDAO;
import com.orangeandbronze.enlistment.dao.DataAccessException;

public class AdminDaoJdbc extends UserDaoJdbc implements AdminDAO {

    private final int QUERY_ADMIN_ID_FIELD = 1;

    public AdminDaoJdbc(DataSource ds) {
        super(ds, "queries/admins");
    }

    @Override
    public Map<String, String> findAdminInfoBy(int adminId) {
        Map<String, String> data = new HashMap<>();

        try (PreparedStatement stmt = prepareStatementFromFile("FindAdminInfoById.sql")) {

            stmt.setInt(QUERY_ADMIN_ID_FIELD, adminId);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                data.put("firstname", rs.getString("firstname"));
                data.put("lastname", rs.getString("lastname"));
            }

        } catch(SQLException ex) {
            throw new DataAccessException(
                    String.format("SQL Query failed: Problem retrieving admin info for id %d", adminId),
                    ex
            );
        }

        return data;
    }

    private PreparedStatement prepareStatementFromFile(String s) {
    }

    // findUserInfobById seems to be only used
    // in an admin context.
    @Override
    public Map<String, String> findUserInfobById(int id) {
        return findAdminInfoBy(id);
    }

}