package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import static com.orangeandbronze.enlistment.dao.jdbc.DataSourceManager.prepareStatement;
import com.orangeandbronze.enlistment.dao.DataAccessException;
import com.orangeandbronze.enlistment.dao.EnlistmentsDAO;
import com.orangeandbronze.enlistment.domain.Section;
import com.orangeandbronze.enlistment.domain.Student;

public class EnlistmentDaoJdbc implements EnlistmentsDAO {

    private final DataSource ds;

    public EnlistmentDaoJdbc(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void create(Student student, Section section) {
        try (PreparedStatement stmt = prepareStatement(ds, "EnlistmentCreateQuery.sql")) {
            stmt.setInt(1, student.getStudentNumber());
            stmt.setString(2, section.getSectionId());
            stmt.execute();
        } catch(SQLException ex) {
            throw new DataAccessException(
                    String.format("Problem adding enlistment for student %d and section %s", student, section),
                    ex
            );
        }
    }

    @Override
    public void delete(int studentNumber, String sectionId) {
        try (PreparedStatement stmt = prepareStatement(ds, "EnlistmentDeleteQuery.sql")) {
            stmt.setInt(1, studentNumber);
            stmt.setString(2, sectionId);
            stmt.execute();
        } catch (SQLException ex) {
            throw new DataAccessException(
                    String.format("Problem removing enlistment for student %d and section %s", studentNumber, sectionId),
                    ex
            );
        }
    }
}