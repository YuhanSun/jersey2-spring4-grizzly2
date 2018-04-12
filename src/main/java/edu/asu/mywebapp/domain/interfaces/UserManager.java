/* Copyright 2013-2014 (c) Sepior Aps, all rights reserved. */

package edu.asu.mywebapp.domain.interfaces;

import java.util.List;

import edu.asu.mywebapp.domain.User;

/**
 * UserManager
 * @author Janus Dam Nielsen
 */
public interface UserManager {

	void insertUser(User user);

	void insertUserAndFailTransation(User user);

	List<User> getUser(String username);

	List<User> getUsers();


}
