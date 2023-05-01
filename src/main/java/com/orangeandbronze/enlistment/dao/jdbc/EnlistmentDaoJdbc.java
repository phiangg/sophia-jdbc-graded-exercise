package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.dao.DataAccessException;
import com.orangeandbronze.enlistment.dao.EnlistmentDAO;
import com.orangeandbronze.enlistment.dao.StaleDataException;
import com.orangeandbronze.enlistment.domain.Section;
import com.orangeandbronze.enlistment.domain.Student;

public class EnlistmentDaoJdbc extends AbstractDaoJdbc implements EnlistmentDAO {

    private final int MODIFY_STUDENT_NUMBER_FIELD = 1;
    private final int MODIFY_SECTION_ID_FIELD = 2;
    private final int MODIFY_VERSION_FIELD = 2;

    private final int QUERY_SECTION_ID_FIELD = 1;
    private DataSource dataSource;

    public EnlistmentDaoJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void create(Student student, Section section) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            // Version check
            try (PreparedStatement stmtUpdateVersion = conn.prepareStatement(
                    "UPDATE sections SET version = version + 1 WHERE section_id = ? AND version = ?")) {
                stmtUpdateVersion.setString(QUERY_SECTION_ID_FIELD, section.getSectionId());
                stmtUpdateVersion.setInt(MODIFY_VERSION_FIELD, section.getVersion());
                int recordsUpdated = stmtUpdateVersion.executeUpdate();
                if (recordsUpdated != 1) {
                    conn.rollback();
                    throw new StaleDataException("Enlistment data for "
                            + section + " was not updated to current version.");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

            // Insert into enlistments table
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO enlistments(student_number, section_id) VALUES(?, ?)")) {
                stmt.setInt(MODIFY_STUDENT_NUMBER_FIELD, student.getStudentNumber());
                stmt.setString(MODIFY_SECTION_ID_FIELD, section.getSectionId());
                if (stmt.executeUpdate() > 0) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DataAccessException(
                    "while inserting into enlistments table for " + student + ", " + section, e);
        }
    }

    @Override
    public void delete(int studentNumber, String sectionId) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            int version = -1;

            // Version check
            try (PreparedStatement versionStatement = conn
                    .prepareStatement("SELECT version FROM sections WHERE section_id = ?;")) {
                versionStatement.setString(QUERY_SECTION_ID_FIELD, sectionId);
                ResultSet rs = versionStatement.executeQuery();

                if (rs.next()) {
                    version = rs.getInt("version");
                }
            }

            try (PreparedStatement stmtUpdateVersion = conn.prepareStatement(
                    "UPDATE sections SET version = version + 1 WHERE section_id = ? AND version = ?")) {
                stmtUpdateVersion.setString(QUERY_SECTION_ID_FIELD, sectionId);
                stmtUpdateVersion.setInt(MODIFY_VERSION_FIELD, version);
                int recordsUpdated = stmtUpdateVersion.executeUpdate();
                if (recordsUpdated != 1) {
                    conn.rollback();
                    throw new StaleDataException("Enlistment data for "
                            + sectionId + " was not updated to current version.");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

            // Delete from enlistments table
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DataAccessException(
                    "while removing from enlistments table for " + studentNumber + ", " + sectionId, e);
        }
    }
}