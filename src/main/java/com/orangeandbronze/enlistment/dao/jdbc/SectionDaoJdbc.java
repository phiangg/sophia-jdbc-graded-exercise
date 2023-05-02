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
import com.orangeandbronze.enlistment.domain.Schedule;
import com.orangeandbronze.enlistment.domain.Section;
import com.orangeandbronze.enlistment.domain.Subject;

import static com.orangeandbronze.enlistment.dao.jdbc.DataSourceManager.getConnection;

public class SectionDaoJdbc extends AbstractDaoJdbc implements SectionDAO {

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
                    Subject subject = new SubjectDaoJdbc(dataSource).findBy(subjectId);

                    List<Schedule> schedules = new ArrayList<>();
                    schedules.add(new Schedule(rs.getString("day"), rs.getString("start_time"), rs.getString("end_time")));

                    String roomName = rs.getString("room_name");
                    int capacity = rs.getInt("capacity");

                    return new Section(sectionId, subject, schedules, roomName, capacity);
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

                sections.add(new Section(sectionId, subject, schedules, roomName, capacity));
            }

            return sections;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Section section) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_SECTION)) {
            stmt.setString(1, section.getSectionId());
            stmt.setString(2, section.getSubject().getSubjectId());
            stmt.setString(3, section.getRoom().getRoomName());
            stmt.setString(4, section.getSchedule().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create section", e);
        }
    }

    @Override
    public List<Section> getAll() {
        List<Section> sections = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SECTIONS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Section section = createSectionFromResultSet(rs);
                sections.add(section);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve all sections", e);
        }
        return sections;
    }

    @Override
    public Section findById(String sectionId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_SECTION_BY_ID)) {
            stmt.setString(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createSectionFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve section with id " + sectionId, e);
        }
        return Section.NONE;
    }

    @Override
    public void update(Section section) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SECTION_BY_ID)) {
            stmt.setString(1, section.getSubject().getSubjectId());
            stmt.setString(2, section.getRoom().getRoomName());
            stmt.setString(3, section.getSchedule().toString());
            stmt.setString(4, section.getSectionId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update section with id " + section.getSectionId(), e);
        }
    }

    @Override
    public void deleteById(String sectionId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SECTION_BY_ID)) {
            stmt.setString(1, sectionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete section with id " + sectionId, e);
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
