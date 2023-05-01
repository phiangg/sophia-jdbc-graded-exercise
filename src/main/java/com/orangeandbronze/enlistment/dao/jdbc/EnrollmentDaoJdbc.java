package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.orangeandbronze.enlistment.dao.EnrollmentDAO;
import com.orangeandbronze.enlistment.domain.Enrollment;
import com.orangeandbronze.enlistment.domain.Student;

public class EnrollmentDaoJdbc extends AbstractDaoJdbc implements EnrollmentDAO {

    @Override
    public void create(Enrollment enrollment) {
        String sql = "INSERT INTO enrollment (student_number, section_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudent().getStudentNumber());
            pstmt.setInt(2, enrollment.getSection().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Enrollment> findByStudent(Student student) {
        String sql = "SELECT e.student_number, e.section_id, s.schedule_id, s.course_id, c.subject, c.units, c.pre_requisite, c.schedule FROM enrollment e JOIN section s ON e.section_id = s.id JOIN course c ON s.course_id = c.id WHERE e.student_number = ?";
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getStudentNumber());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudent(student);
                    enrollment.setSection(new SectionDaoJdbc().findById(rs.getInt("section_id")));
                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return enrollments;
    }

}
