package edu.asu.mywebapp.resources;

import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import commons.Config;
import commons.MyRectangle;
import commons.Neo4jGraphUtility;
import cypher.middleware.RisoTreeConnector;
import edu.asu.mywebapp.domain.interfaces.UserManager;

/**
 * TestResource
 * 
 * @author Janus Dam Nielsen
 */
@Component
@Path("/")
public class TestResource {

  final static Logger LOGGER = Logger.getLogger(TestResource.class.getName());

  final static String dataset = Config.Datasets.wikidata.name();

  // public Driver driver =
  // GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "syh19910205"));

  // 407CD
  static String risoTreeDbPath = "D:/Project_Data/wikidata-20180308-truthy-BETA.nt/"
      + "neo4j-community-3.4.12_risotree/data/databases/graph.db";

  // server
  // static String risoTreeDbPath =
  // "/hdd/code/yuhansun/data/wikidata/neo4j-community-3.4.12_risotree/data/databases/graph.db";

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
    String[] strings = input.split(" delimiter ");
    String query = strings[0];
    LOGGER.info("query: " + query);
    String spatialNode = strings[1];
    LOGGER.info("spatialnode: " + spatialNode);
    String rectangleStr = strings[2];
    LOGGER.info("rectangleStr: " + rectangleStr);
    MyRectangle rectangle = convertFrontRectangleStringToMyRectangle(rectangleStr);
    LOGGER.info("rectangle: " + rectangle);


    // String result = "";
    // Session session = driver.session();
    // StatementResult rs = session.run(query);
    // while (rs.hasNext()) {
    // result += rs.next();
    // }
    // System.out.println(result);

    RisoTreeConnector risoTreeConnector = new RisoTreeConnector(dbserviceRisoTree, dataset, 1);
    List<String> refinedQueries =
        risoTreeConnector.getRefinedQueries(query, spatialNode, rectangle);
    return convertToFrontQuery(refinedQueries);
  }

  /**
   * Convert from list of queries to the ; separated string.
   *
   * @param queries
   * @return
   */
  public static String convertToFrontQuery(List<String> queries) {
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

  public static MyRectangle convertFrontRectangleStringToMyRectangle(String rectangleStr) {
    String[] strings = rectangleStr.split(", ");
    return new MyRectangle(Double.parseDouble(StringUtils.split(strings[2], ':')[1]),
        Double.parseDouble(StringUtils.split(strings[0], ':')[1]),
        Double.parseDouble(StringUtils.split(strings[3], ':')[1]),
        Double.parseDouble(StringUtils.split(strings[1], ':')[1]));
  }

  @GET
  @Path("/adduser/{username}")
  @Produces("text/html")
  public String addUser(@PathParam("username") String username) {
    LOGGER.info(username);
    return "Added one user " + username;
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
