package edu.asu.mywebapp.resources;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.summary.ProfiledPlan;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import commons.Config;
import commons.MyRectangle;
import commons.Neo4jGraphUtility;
import commons.OwnMethods;
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
  final static DecimalFormat dFormat = new DecimalFormat("#.#");

  final static String dataset = Config.Datasets.wikidata.name();
  final static String delimiter = " delimiter ";

  public static Driver driver =
      GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "syh19910205"));

  // 407CD
  // static String risoTreeDbPath = "D:/Project_Data/wikidata-20180308-truthy-BETA.nt/"
  // + "neo4j-community-3.4.12_risotree/data/databases/graph.db";
  // static String RTreeDbPath = "D:/Project_Data/wikidata-20180308-truthy-BETA.nt/"
  // + "neo4j-community-3.4.12_rtree/data/databases/graph.db";

  // server
  static String risoTreeDbPath =
      "/hdd/code/yuhansun/data/wikidata/neo4j-community-3.4.12_backup_full/data/databases/graph.db";
  // static String RTreeDbPath =
  // "/hdd/code/yuhansun/data/wikidata/neo4j-community-3.4.12_rtree/data/databases/graph.db";

  public static GraphDatabaseService dbserviceRisoTree =
      Neo4jGraphUtility.getDatabaseService(risoTreeDbPath);
  // public static GraphDatabaseService dbserviceRTree =
  // Neo4jGraphUtility.getDatabaseService(RTreeDbPath);

  public static RisoTreeConnector risoTreeConnector =
      new RisoTreeConnector(dbserviceRisoTree, dataset, 1);


  @Autowired
  private UserManager userManager;

  @GET
  @Path("/compareperformance/{input}")
  @Produces("text/html")
  public String compareperformance(@PathParam("input") String input) throws Exception {
    System.out.println(input);
    String[] strings = input.split(delimiter);
    String query = strings[0];
    query = query.split("limit ")[0];
    LOGGER.info("query: " + query);
    String spatialNode = strings[1];
    LOGGER.info("spatialnode: " + spatialNode);
    String rectangleStr = strings[2];
    LOGGER.info("rectangleStr: " + rectangleStr);
    MyRectangle rectangle = convertFrontRectangleStringToMyRectangle(rectangleStr);
    LOGGER.info("rectangle: " + rectangle);

    int resultCount = 0;
    int pageAccess = 0;
    long time = 0;
    long start = System.currentTimeMillis();
    List<String> refinedQueries =
        risoTreeConnector.getRefinedQueries(query, spatialNode, rectangle);
    time += System.currentTimeMillis() - start;
    LOGGER.info(refinedQueries.toString());
    for (String refinedQuery : refinedQueries) {
      start = System.currentTimeMillis();
      Result result = dbserviceRisoTree.execute("profile " + refinedQuery);
      while (result.hasNext()) {
        resultCount++;
        result.next();
      }
      time += System.currentTimeMillis() - start;
      pageAccess += OwnMethods.GetTotalDBHits(result.getExecutionPlanDescription());
    }
    String compareResultRecord = formCompareResult(time, pageAccess, resultCount);
    String risoRecord = "RisoTree\t" + compareResultRecord;
    LOGGER.info("" + risoRecord);

    start = System.currentTimeMillis();
    Session session = driver.session();
    ResultSummary rs = session.run("profile " + query).consume();
    time = System.currentTimeMillis() - start;
    ProfiledPlan profiledPlan = rs.profile();
    LOGGER.info(profiledPlan.toString());
    session.close();
    resultCount = (int) profiledPlan.records();
    Map<String, Value> arguments = profiledPlan.arguments();
    LOGGER.info(arguments.toString());
    pageAccess = Integer.parseInt(arguments.get("PageCacheMisses").toString())
        + Integer.parseInt(arguments.get("PageCacheHits").toString());
    String neo4jRecord = "neo4j\t" + formCompareResult(time, pageAccess, resultCount);
    LOGGER.info(neo4jRecord);

    return risoRecord + "\n" + neo4jRecord;
  }

  public static String formCompareResult(long time, long pageAccess, long resultCount) {
    return String.format("query time: %dms, page access: %sK, result count: %d", time,
        dFormat.format(pageAccess / 1000.0), resultCount);
  }

  @GET
  @Path("/risotree/{input}")
  @Produces("text/html")
  public String risotree(@PathParam("input") String input) throws Exception {
    System.out.println(input);
    String[] strings = input.split(delimiter);
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
