/* Copyright 2013-2014 (c) Sepior Aps, all rights reserved. */

package edu.asu.mywebapp.domain;

import java.util.List;

import edu.asu.mywebapp.domain.interfaces.UserManager;
import edu.asu.mywebapp.storage.interfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserManagerImpl
 * @author Janus Dam Nielsen
 */
@Component
@Transactional
public class UserManagerImpl implements UserManager {

	@Autowired
	private UserDao userDAO;

	@Override
	public void insertUser(User user) {
		this.userDAO.insertUser(user);
	}

	@Override
	public void insertUserAndFailTransation(User user) {
		this.userDAO.insertUser(user);
		throw new UnsupportedOperationException();
	}


	@Override
	public List<User> getUser(String username) {
		return this.userDAO.getUser(username);
	}

	@Override
	public List<User> getUsers() {
		return this.userDAO.getUsers();
	}

}
