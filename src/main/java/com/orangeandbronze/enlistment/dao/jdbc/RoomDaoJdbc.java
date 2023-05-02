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
import com.orangeandbronze.enlistment.dao.RoomDAO;
import com.orangeandbronze.enlistment.domain.Faculty;
import com.orangeandbronze.enlistment.domain.Room;
import com.orangeandbronze.enlistment.domain.Schedule;
import com.orangeandbronze.enlistment.domain.Section;
import com.orangeandbronze.enlistment.domain.Subject;

public class RoomDaoJdbc extends AbstractDaoJdbc implements RoomDAO {
    private final int QUERY_ROOM_NAME_FIELD = 1;

    public RoomDaoJdbc(DataSource dataSource) {
        super(dataSource, "FindRoomById.sql");
    }

    @Override
    public Room findBy(String roomName) {
        Room room = Room.TBA;

        try (PreparedStatement stmt = prepareStatementFromFile("FindRoomById.sql")) {
            Collection<Section> sections = new ArrayList<>();

            boolean found = false;
            int capacity = -1;

            stmt.setString(QUERY_ROOM_NAME_FIELD, roomName);

            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    if(!found) found = true;

                    if(capacity < 0) {
                        capacity = rs.getInt("capacity");
                    }

                    if(StringUtils.isNotBlank(rs.getString("section_id"))) {
                        String sectionId = rs.getString("section_id");
                        Subject subject = new Subject(rs.getString("subject_id"));
                        Schedule schedule = Schedule.valueOf(rs.getString("schedule"));
                        Room _room = new Room(roomName);
                        Faculty faculty = new Faculty(rs.getInt("faculty_number"));

                        sections.add(new Section(sectionId, subject, schedule, _room, faculty));
                    }

                    room = new Room(roomName, capacity, sections, rs.getInt("version"));
                }
            }

        } catch(SQLException | IOException ex) {
            throw new DataAccessException(
                    String.format("SQL Query failed: Problem retrieving room data for id %s", roomName),
                    ex
            );
        }

        return room;
    }

    @Override
    public Collection<String> findAllIds() {
        Collection<String> roomNames = new ArrayList<>();

        try (PreparedStatement stmt = prepareStatementFromFile("FindAllIds.sql")) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                roomNames.add(rs.getString("room_name"));
            }

        } catch(SQLException | IOException ex) {
            throw new DataAccessException(
                    String.format("SQL Query failed: Problem retrieving room names"),
                    ex
            );
        }

        return roomNames;
    }

    @Override
    public Collection<Map<String, String>> findAllRoomInfos() {
        Collection<Map<String, String>> rooms = new ArrayList<>();

        try (PreparedStatement stmt = prepareStatementFromFile("FindAllRoomInfos.sql")) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Map<String, String> roomInfo = new HashMap<>();
                roomInfo.put("name", rs.getString("room_name"));
                roomInfo.put("capacity", rs.getString("capacity"));

                rooms.add(roomInfo);
            }

        } catch(SQLException | IOException ex) {
            throw new DataAccessException(
                    String.format("SQL Query failed: Problem retrieving room names"),
                    ex
            );
        }

        return rooms;
    }
}