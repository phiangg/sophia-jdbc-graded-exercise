package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.dao.StudentDAO;
import com.orangeandbronze.enlistment.domain.Student;

import static com.orangeandbronze.enlistment.dao.jdbc.DataSourceManager.getConnection;

public abstract class StudentDaoJdbc extends AbstractDaoJdbc implements StudentDAO {

    public StudentDaoJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Student findWithSectionsBy(int studentNumber) {
        String sql = "SELECT student_number, first_name, last_name, sections.section_id, section_name, subject_name, days, time_slot, room_name, capacity " +
                "FROM students " +
                "INNER JOIN enlisted_sections ON students.student_number = enlisted_sections.student_number " +
                "INNER JOIN sections ON enlisted_sections.section_id = sections.section_id " +
                "INNER JOIN subjects ON sections.subject_id = subjects.subject_id " +
                "INNER JOIN rooms ON sections.room_id = rooms.room_id " +
                "WHERE students.student_number = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // set the value of the parameter in the SQL statement
            statement.setInt(1, studentNumber);

            // execute the SQL statement and get the result set
            ResultSet resultSet = statement.executeQuery();

            // initialize the Student object to NONE
            Student student = Student.NONE;

            // loop through the result set
            while (resultSet.next()) {
                int studentNumberFromResultSet = resultSet.getInt("student_number");

                // check if the student number from the result set matches the input student number
                if (studentNumberFromResultSet == studentNumber) {
                    // if the Student object is still NONE, set the first name and last name from the result set
                    if (student == Student.NONE) {
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        student = new Student(studentNumber, firstName, lastName);
                    }

                    // get the section details from the result set
                    String sectionId = resultSet.getString("section_id");
                    String sectionName = resultSet.getString("section_name");
                    String subjectName = resultSet.getString("subject_name");
                    String days = resultSet.getString("days");
                    String timeSlot = resultSet.getString("time_slot");
                    String roomName = resultSet.getString("room_name");
                    int capacity = resultSet.getInt("capacity");

                    // enlist the student in the section
                    student.enlist(sectionId, sectionName, subjectName, days, timeSlot, roomName, capacity);
                }
            }

            // return the Student object
            return student;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Student findWithoutSectionsBy(int studentNumber) {
        String sql = "SELECT first_name, last_name FROM students WHERE student_number = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // set the value of the parameter in the SQL statement
            statement.setInt(1, studentNumber);

            // execute the SQL statement and get the result set
            ResultSet resultSet = statement.executeQuery();

            // if there is a result, create a new Student object with the first name, last name, and student number
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                return new Student(studentNumber, firstName, lastName);
            } else {
                // otherwise, return
                return Student.NONE;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}