package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.domain.Student;

public class StudentDaoJdbc extends AbstractDaoJdbc<Student> {

    public StudentDaoJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Student mapToResultSet(ResultSet resultSet) throws SQLException {
        int studentNumber = resultSet.getInt("student_number");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        return new Student(studentNumber, firstName, lastName);
    }

    public List<Student> getAll() throws SQLException {
        String sql = "SELECT * FROM students";
        return getList(sql);
    }

    public Student getById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_number = ?";
        return getSingle(sql, id);
    }

    public int add(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_number, first_name, last_name) VALUES (?, ?, ?)";
        return update(sql, student.getStudentNumber(), student.getFirstname(), student.getLastname());
    }

}
