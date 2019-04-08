package edu.asu.mywebapp.resources;

import java.security.SecureRandom;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import CypherMiddleWare.RisoTreeConnector;
import commons.Config;
import commons.Neo4jGraphUtility;
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

  final static String dataset = Config.Datasets.wikidata.name();

  // public Driver driver =
  // GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "syh19910205"));

  static String risoTreeDbPath = "D:/Project_Data/wikidata-20180308-truthy-BETA.nt/"
      + "neo4j-community-3.4.12/data/databases/graph.db";
  public static GraphDatabaseService dbserviceRisoTree =
      Neo4jGraphUtility.getDatabaseService(risoTreeDbPath);

  // static String RTreeDbPath = "D:/Project_Data/wikidata-20180308-truthy-BETA.nt/"
  // + "neo4j-community-3.4.12/data/databases/graph.db";
  // public static GraphDatabaseService dbserviceRTree =
  // Neo4jGraphUtility.getDatabaseService(RTreeDbPath);

  @Autowired
  private UserManager userManager;

  @GET
  @Path("/risotree/{input}")
  @Produces("text/html")
  public String risotree(@PathParam("input") String input) throws Exception {
    System.out.println(input);
    String[] strings = StringUtils.split(input, ';');
    String query = strings[0];
    String spatialNode = strings[1];
    String rectangleStr = strings[2];

    // String result = "";
    // Session session = driver.session();
    // StatementResult rs = session.run(query);
    // while (rs.hasNext()) {
    // result += rs.next();
    // }
    // System.out.println(result);

    RisoTreeConnector risoTreeConnector = new RisoTreeConnector(dbserviceRisoTree, dataset, 1);
    List<String> refinedQueries =
        risoTreeConnector.getRefinedQueries(query, spatialNode, rectangleStr);
    return convertToFrontQuery(refinedQueries);
  }

  public String convertToFrontQuery(List<String> queries) {
    String res = "";
    int index = 0;
    for (String query : queries) {
      if (index == 0) {
        res += query;
      } else {
        res += ";" + query;
      }
      index++;
    }
    return res;
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
