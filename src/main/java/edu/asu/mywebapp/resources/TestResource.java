package edu.asu.mywebapp.resources;

import java.security.SecureRandom;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import edu.asu.mywebapp.domain.User;
import edu.asu.mywebapp.domain.interfaces.UserManager;

/**
 * TestResource
 * 
 * @author Janus Dam Nielsen
 */
@Component
@Path("/")
public class TestResource {

  // public static GraphDatabaseService service = null;

  // public static void main() {
  //
  // }

  @Autowired
  private UserManager userManager;

  @GET
  @Path("/cypherquery/{username}")
  @Produces("text/html")
  public String cypherQuery(@PathParam("username") String query) {
    System.out.println(query);
    Driver driver =
        GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "syh19910205"));
    String result = "";
    Session session = driver.session();
    StatementResult rs = session.run(query);
    while (rs.hasNext()) {
      result += rs.next();
    }
    System.out.println(result);
    driver.close();
    return result;
  }

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
