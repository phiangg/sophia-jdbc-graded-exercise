package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.domain.Course;

public class CourseDaoJdbc extends AbstractDaoJdbc<Course> {

    public CourseDaoJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Course mapToResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("course_id");
        String subject = resultSet.getString("subject");
        int section = resultSet.getInt("section");
        return new Course(id, subject, section);
    }

    public List<Course> getAll() throws SQLException {
        String sql = "SELECT * FROM courses";
        return getList(sql);
    }

    public Course getById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        return getSingle(sql, id);
    }

    public int add(Course course) throws SQLException {
        String sql = "INSERT INTO courses (subject, section) VALUES (?, ?)";
        return update(sql, course.getSubject(), course.getSection());
    }

}
