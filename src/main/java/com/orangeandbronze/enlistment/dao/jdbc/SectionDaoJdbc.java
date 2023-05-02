package com.orangeandbronze.enlistment.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.dao.DataAccessException;
import com.orangeandbronze.enlistment.dao.SectionDAO;
import com.orangeandbronze.enlistment.domain.Room;
import com.orangeandbronze.enlistment.domain.Schedule;
import com.orangeandbronze.enlistment.domain.Section;
import com.orangeandbronze.enlistment.domain.Subject;
import com.orangeandbronze.enlistment.domain.Days;

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
                    SubjectDaoJdbc subjectDao = new SubjectDaoJdbc(dataSource) {
                        @Override
                        public Subject findBy(String subjectId) {
                            return null;
                        }
                    };
                    Subject subject = subjectDao.findBy(subjectId);

                    Days day = Days.valueOf(rs.getString("day"));
                    LocalTime startTime = LocalTime.parse(rs.getString("start_time"));
                    LocalTime endTime = LocalTime.parse(rs.getString("end_time"));
                    Schedule schedule = new Schedule(day, startTime, endTime);

                    List<Schedule> schedules = new ArrayList<>();
                    schedules.add(schedule);

                    String roomName = rs.getString("room_name");
                    int capacity = rs.getInt("capacity");

                    return new Section(sectionId, subject, (Schedule) schedules, new Room(roomName, capacity));
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
                SubjectDaoJdbc subjectDao = new SubjectDaoJdbc(dataSource) {
                    @Override
                    public Subject findBy(String subjectId) {
                        return null;
                    }
                };
                Subject subject = subjectDao.findBy(rs.getString("subject_id"));

                Days day = Days.valueOf(rs.getString("day"));
                LocalTime startTime = LocalTime.parse(rs.getString("start_time"));
                LocalTime endTime = LocalTime.parse(rs.getString("end_time"));
                Schedule schedule = new Schedule(day, startTime, endTime);

                List<Schedule> schedules = new ArrayList<>();
                schedules.add(schedule);

                String roomName = rs.getString("room_name");
                int capacity = rs.getInt("capacity");

                sections.add(new Section(sectionId, subject, (Schedule) schedules, new Room(roomName, capacity)));
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
             PreparedStatement
                     stmt = conn.prepareStatement(CREATE_SECTION)) {
            stmt.setString(1, section.getSectionId());
            stmt.setString(2, section.getSubject().getSubjectId());
            stmt.setString(3, section.getRoom().getRoomName());
            stmt.setString(4, section.getSchedules().get(0).getDay().toString());
            stmt.setString(5, section.getSchedules().get(0).getStartTime().toString());
            stmt.setString(6, section.getSchedules().get(0).getEndTime().toString());
            stmt.setInt(7, section.getRoom().getCapacity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create section", e);
        }
    }
    @Override
    public void create(Section section) {
        try (Connection conn = getConnection();
             PreparedStatement
                     stmt = conn.prepareStatement(CREATE_SECTION)) {
            stmt.setString(1, section.getSectionId());
            stmt.setString(2, section.getSubject().getSubjectId());
            stmt.setString(3, section.getRoom().getRoomName());
            stmt.setString(4, section.getSchedules().get(0).getDay().toString());
            stmt.setString(5, section.getSchedules().get(0).getStartTime().toString());
            stmt.setString(6, section.getSchedules().get(0).getEndTime().toString());
            stmt.setInt(7, section.getRoom().getCapacity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create section", e);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to connect to data source", e);
        }
    }
}



