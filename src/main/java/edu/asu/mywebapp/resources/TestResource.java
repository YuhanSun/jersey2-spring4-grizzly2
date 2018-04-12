package edu.asu.mywebapp.resources;

import java.security.SecureRandom;

import javax.ws.rs.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.mywebapp.domain.User;
import edu.asu.mywebapp.domain.interfaces.UserManager;

/**
 * TestResource
 * @author Janus Dam Nielsen
 */
@Component
@Path("/")
public class TestResource {

	@Autowired
	private UserManager userManager;

	@GET
	@Path("/adduser/{username}")
	@Produces("text/html")
	public String addUser(@PathParam("username") String username) {
		User user = new User();
		user.setId(new SecureRandom().nextInt());
		user.setName("test");
		user.setUsername(username);
		this.userManager.insertUser(user);
		return "Added one user";
	}

    @GET
    @Path("/getuser/{username}")
    @Produces("text/html")
    public String getUser(@PathParam("username") String username) {
        return this.userManager.getUser(username).toString();
    }

    @GET
    @Path("/getuser")
    @Produces("text/html")
    public String getAllUser() {
        return this.userManager.getUsers().toString();
    }

}