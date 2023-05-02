package com.orangeandbronze.enlistment.dao.jdbc;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.dao.DataAccessException;
import com.orangeandbronze.enlistment.dao.SubjectDAO;
import com.orangeandbronze.enlistment.domain.Subject;


public class SubjectDaoJdbc extends AbstractDaoJdbc implements SubjectDAO {

    public SubjectDaoJdbc(DataSource dataSource) {
        super(dataSource, "FindAllIds.sql");
    }

    @Override
    public Subject findBy(String subjectId) {
        return null;
    }

    @Override
    public Collection<String> findAllIds() {
        Collection<String> subjects = new ArrayList<>();

        try (PreparedStatement stmt = prepareStatementFromFile("FindAllIds.sql")) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                subjects.add(rs.getString("subject_id"));
            }

        } catch(SQLException | IOException ex) {
            throw new DataAccessException(
                    String.format("SQL Query failed: Problem retrieving subjects"),
                    ex
            );
        }

        return subjects;
    }



}