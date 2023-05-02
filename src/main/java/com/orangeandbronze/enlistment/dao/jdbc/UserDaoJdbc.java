package com.orangeandbronze.enlistment.dao.jdbc;

import java.util.Map;

import javax.sql.DataSource;

import com.orangeandbronze.enlistment.dao.UserDAO;

public abstract class UserDaoJdbc extends AbstractDaoJdbc implements UserDAO {

    public UserDaoJdbc(DataSource dataSource, String queryPath) {
        super(dataSource, queryPath);
    }

    public UserDaoJdbc(DataSource ds) {
        super(ds);
    }

    @Override
    public abstract Map<String, String> findUserInfobById(int id);
}