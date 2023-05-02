package com.orangeandbronze.enlistment.dao.jdbc;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.orangeandbronze.enlistment.dao.DataAccessException;
import com.orangeandbronze.enlistment.dao.FacultyDAO;
import com.orangeandbronze.enlistment.domain.Faculty;
import com.orangeandbronze.enlistment.domain.Room;
import com.orangeandbronze.enlistment.domain.Schedule;
import com.orangeandbronze.enlistment.domain.Section;
import com.orangeandbronze.enlistment.domain.Subject;

public class FacultyDaoJdbc extends AbstractDaoJdbc implements FacultyDAO {

    private final int QUERY_FACULTY_NUMBER_FIELD = 1;

    public FacultyDaoJdbc(DataSource dataSource) {
        super(dataSource, "FindFacultyByFacultyNo.sql");
    }

    @Override
    public Faculty findBy(int facultyNumber) {
        Faculty faculty = Faculty.TBA;

        try (PreparedStatement stmt = prepareStatementFromFile("FindFacultyByFacultyNo.sql")) {
            String firstName = "";
            String lastName = "";
            Collection<Section> sections = new ArrayList<>();

            boolean found = false;

            stmt.setInt(QUERY_FACULTY_NUMBER_FIELD, facultyNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if(!found) found = true;

                    if(StringUtils.isBlank(lastName) || StringUtils.isBlank(firstName)) {
                        firstName = rs.getString("firstname");
                        lastName = rs.getString("lastname");
                    }

                    if(StringUtils.isNotBlank(rs.getString("section_id"))) {
                        String sectionId = rs.getString("section_id");
                        Subject subject = new Subject(rs.getString("subject_id"));
                        Schedule schedule = Schedule.valueOf(rs.getString("schedule"));
                        Room room = new Room(rs.getString("room_name"), rs.getInt("capacity"));
                        Faculty _sFaculty = new Faculty(facultyNumber);

                        sections.add(new Section(sectionId, subject, schedule, room, _sFaculty));
                    }
                }

                faculty = new Faculty(facultyNumber, firstName, lastName, sections);
            }

        } catch (SQLException | IOException ex) {
            throw new DataAccessException(
                    String.format("SQL Query failed: Problem retrieving faculty data for id %d", facultyNumber),
                    ex
            );
        }

        return faculty;
    }

    @Override
    public Collection<Map<String, String>> findAllFacultyInfos() {
        Collection<Map<String, String>> faculty = new ArrayList<>();

        try (PreparedStatement stmt = prepareStatementFromFile("FindAllFaculty.sql")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> values = new HashMap<>();

                    values.put("facultyNumber", rs.getString("faculty_number"));
                    values.put("firstname", rs.getString("firstname"));
                    values.put("lastname", rs.getString("lastname"));

                    faculty.add(values);
                }
            }

        } catch (SQLException | IOException ex) {
            throw new DataAccessException("SQL Query failed: Failed to execute query for all faculty", ex);
        }

        return faculty;
    }
}