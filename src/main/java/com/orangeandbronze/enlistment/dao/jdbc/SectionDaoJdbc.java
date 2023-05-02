package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.dao.DataAccessException;
import com.orangeandbronze.enlistment.dao.SectionDAO;
import com.orangeandbronze.enlistment.domain.Room;
import com.orangeandbronze.enlistment.domain.Schedule;
import com.orangeandbronze.enlistment.domain.Section;
import com.orangeandbronze.enlistment.domain.Subject;

public abstract class SectionDaoJdbc extends AbstractDaoJdbc implements SectionDAO {

    public SectionDaoJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Section findBy(String sectionId) {
        String sql = "SELECT * FROM sections WHERE section_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sectionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String subjectId = rs.getString("subject_id");
                    Subject subject = new SubjectDaoJdbc(dataSource) {
                        @Override
                        public Subject findBy(String subjectId) {
                            return null;
                        }
                    }.findBy(subjectId);

                    List<Schedule> schedules = new ArrayList<>();
                    schedules.add(new Schedule (rs.getString("day"), rs.getString("start_time"), rs.getString("end_time")));

                    String roomName = rs.getString("room_name");
                    int capacity = rs.getInt("capacity");

                    return new Section(sectionId, subject, schedules, new Room(roomName, capacity));
                }
            }

            return Section.NONE;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Section> findAll() {
        String sql = "SELECT * FROM sections";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            List<Section> sections = new ArrayList<>();
            while (rs.next()) {
                String sectionId = rs.getString("section_id");
                Subject subject = new SubjectDaoJdbc(dataSource).findBy(rs.getString("subject_id"));

                List<Schedule> schedules = new ArrayList<>();
                schedules.add(new Schedule(rs.getString("day"), rs.getString("start_time"), rs.getString("end_time")));

                String roomName = rs.getString("room_name");
                int capacity = rs.getInt("capacity");

                sections.add(new Section(sectionId, subject, schedules, new Room(roomName, capacity)));
            }

            return sections;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
            private static final String CREATE_SECTION = "INSERT INTO sections (section_id, subject_id, room_name, day, start_time, end_time, capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void create(Section section) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_SECTION)) {
            stmt.setString(1, section.getSectionId());
            stmt.setString(2, section.getSubject().getSubjectId());
            stmt.setString(3, section.getRoom().getRoomName());
            stmt.setString(4, section.getSchedules().get(0).getDay());
            stmt.setString(5, section.getSchedules().get(0).getStartTime());
            stmt.setString(6, section.getSchedules().get(0).getEndTime());
            stmt.setInt(7, section.getCapacity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create section", e);
        }
    }
}
