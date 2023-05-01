package com.orangeandbronze.enlistment.dao.jdbc;

import com.orangeandbronze.enlistment.dao.*;
import com.orangeandbronze.enlistment.domain.*;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SubjectDaoJdbc extends AbstractDaoJdbc implements SubjectDAO {

    public SubjectDaoJdbc(DataSource ds) {
        super(ds);
    }

    @Override
    public Subject findBy(String subjectId) {
        try (PreparedStatement stmt = prepareStatement("SELECT * FROM subjects WHERE subject_id = ?")) {
            stmt.setString(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Subject(
                        rs.getString("subject_id"),
                        rs.getString("description"),
                        rs.getInt("units")
                );
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to find subject by ID: " + subjectId, ex);
        }

        return Subject.TBA;
    }

    @Override
    public Collection<Subject> findAll() {
        Collection<Subject> subjects = new ArrayList<>();

        try (PreparedStatement stmt = prepareStatement("SELECT * FROM subjects")) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subjects.add(new Subject(
                        rs.getString("subject_id"),
                        rs.getString("description"),
                        rs.getInt("units")
                ));
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to retrieve all subjects", ex);
        }

        return subjects;
    }

    @Override
    public Collection<String> findAllIds() {
        Collection<String> subjectIds = new ArrayList<>();

        try (PreparedStatement stmt = prepareStatement("SELECT subject_id FROM subjects")) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subjectIds.add(rs.getString("subject_id"));
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to retrieve all subject IDs", ex);
        }

        return subjectIds;
    }
}
