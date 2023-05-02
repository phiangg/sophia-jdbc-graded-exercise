package com.orangeandbronze.enlistment.controllers;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.sql.*;

import com.orangeandbronze.enlistment.dao.*;
import com.orangeandbronze.enlistment.dao.jdbc.*;
import com.orangeandbronze.enlistment.domain.Subject;
import com.orangeandbronze.enlistment.service.*;

import java.util.Collection;
import java.util.Map;

@WebListener
public class ContextLoaderListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {
		DataSource ds = DataSourceManager.getDataSource();
		ServletContext context = sce.getServletContext();
		SectionDAO sectionDao = new SectionDaoJdbc(ds) {
			@Override
			public Collection<Map<String, String>> findAllSectionInfos() {
				return null;
			}

			@Override
			public Collection<Map<String, String>> findSectionInfosByStudentNo(int studentNumber) {
				return null;
			}

			@Override
			public Collection<Map<String, String>> findSectionInfosNotByStudentNo(int studentNumber) {
				return null;
			}
		};
		StudentDAO studentDao = new StudentDaoJdbc(ds) {
			@Override
			public Map<String, String> findUserInfobById(int id) {
				return null;
			}
		};
		EnlistmentDAO enlistmentDao = new EnlistmentDaoJdbc(ds);
		SubjectDAO subjectDao = new SubjectDaoJdbc(ds) {
			@Override
			public Subject findBy(String subjectId) {
				return null;
			}
		};
		RoomDAO roomDao = new RoomDaoJdbc(ds);
		FacultyDAO facultyDao = new FacultyDaoJdbc(ds);
		AdminDAO adminDao = new AdminDaoJdbc(ds);
		EnlistService enlistService = new EnlistService(sectionDao,
				studentDao, enlistmentDao);
		context.setAttribute(AttributeAndParamNames.ENLIST_SERVICE, enlistService);
		CreateSectionService createSectionService = new CreateSectionService(sectionDao, subjectDao, roomDao, facultyDao);
		context.setAttribute(AttributeAndParamNames.CREATE_SECTION_SERVICE, createSectionService);
		UserService userService = new UserService(adminDao, studentDao);
		context.setAttribute(AttributeAndParamNames.USER_SERVICE, userService);
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// DO NOTHING
	}
}
