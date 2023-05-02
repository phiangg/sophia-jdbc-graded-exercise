package com.orangeandbronze.enlistment.dao.jdbc;

import com.orangeandbronze.enlistment.domain.Room;
import com.orangeandbronze.enlistment.domain.Schedule;
import com.orangeandbronze.enlistment.domain.Subject;

import java.util.List;

public class SectionDaoJdbc {

    public static final SectionDaoJdbc NONE = new SectionDaoJdbc("", null, null, null);

    private final String sectionId;
    private final Subject subject;
    private final List<Schedule> schedules;
    private final Room room;

    public SectionDaoJdbc(String sectionId, Subject subject, List<Schedule> schedules, Room room) {
        this.sectionId = sectionId;
        this.subject = subject;
        this.schedules = schedules;
        this.room = room;
    }

    public String getSectionId() {
        return sectionId;
    }

    public Subject getSubject() {
        return subject;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return "Section [sectionId=" + sectionId + ", subject=" + subject + ", schedules=" + schedules + ", room="
                + room + "]";
    }

}
