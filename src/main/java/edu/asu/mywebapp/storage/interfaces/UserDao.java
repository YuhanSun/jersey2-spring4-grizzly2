/* Copyright 2013-2014 (c) Sepior Aps, all rights reserved. */

package edu.asu.mywebapp.storage.interfaces;

import java.util.List;

import edu.asu.mywebapp.domain.User;

/**
 * UserDao
 * @author Janus Dam Nielsen
 */
public interface UserDao {

	void insertUser(User user);

	List<User> getUser(String username);

	List<User> getUsers();
}
