/* Copyright 2013-2014 (c) Sepior Aps, all rights reserved. */

package edu.asu.mywebapp.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import edu.asu.mywebapp.storage.interfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import edu.asu.mywebapp.domain.User;

/**
 * UserDaoImpl
 * @author Janus Dam Nielsen
 */
@Repository
public class UserDaoImpl extends JdbcDaoSupport implements UserDao {

	@Autowired
	public UserDaoImpl(DataSource dataSource) {
		this.setDataSource(dataSource);
	}

	@Override
	public void insertUser(User user) {
		this.getJdbcTemplate().update(
				"INSERT INTO USERS (ID, USERNAME, NAME) VALUES (?, ?, ?)",
				new Object[] {
						user.getId(),
						user.getUsername(),
						user.getName()
				}
				);
	}

	@Override
	public List<User> getUser(String username) {
		List<User> user = this.getJdbcTemplate().
				query(
						"SELECT * FROM USERS WHERE USERNAME = ?",
						new Object[] { username },
						new UserMapper()
						);
		return user;
	}

	@Override
	public List<User> getUsers() {
		List<User> users = this.getJdbcTemplate().
				query("SELECT * FROM USERS",
						new UserMapper()
						);
		return users;
	}

	private class UserMapper implements RowMapper<User>{

		@Override
		public User mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			User user = new User();
			user.setId(rs.getInt("ID"));
			user.setUsername(rs.getString("USERNAME"));
			user.setName(rs.getString("NAME"));
			return user;
		}

	}

}
