package com.orangeandbronze.enlistment.dao;

import com.orangeandbronze.enlistment.domain.Subject;

import java.util.*;

public interface SubjectDAO {

    Subject findBy(String subjectId);

    Collection<String> findAllIds();
}
