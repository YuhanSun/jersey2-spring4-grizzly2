package cypher.middleware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import commons.Config;
import commons.MyRectangle;
import commons.Neo4jGraphUtility;
import commons.Util;

public class RisoTreeConnectorTest {

  String homeDir, dbPath;
  String query;
  String dataset = Config.Datasets.wikidata.name();
  GraphDatabaseService service;

  @Before
  public void setUp() {
    // ClassLoader classLoader = getClass().getClassLoader();
    // File file = new File(classLoader.getResource("").getFile());
    // homeDir = file.getAbsolutePath();

    // mac local test
    // homeDir = "/Users/zhouyang/Documents/tmp";
    // dbPath = homeDir + "/data/graph.db";

    // windows wikidata db
    homeDir = "D:/Project_Data/wikidata-20180308-truthy-BETA.nt";
    dbPath = homeDir + "/neo4j-community-3.4.12/data/databases/graph.db";

    query = "match (a:A)-->(b:B)<--(c:TEST) where a.type = 0 return a, b, c limit 10";
    query = "MATCH (c:C)--(a:A)-[b]-(spatialnode:Spatial) WHERE "
        + "22.187478257613602 <= spatialnode.lat <= 22.225842149771214 AND "
        + "113.50180238485339 <= spatialnode.lon <= 113.56607615947725 " + "RETURN * LIMIT 10";
    query = "MATCH (a:`heritage designation`)-[b]-(spatialnode:museum) "
        + "WHERE 22.187478257613602 <= spatialnode.lat <= 22.225842149771214 "
        + "AND 113.50180238485339 <= spatialnode.lon <= 113.56607615947725 "
        + "RETURN spatialnode LIMIT 5";
    Util.println(query);
    service = Neo4jGraphUtility.getDatabaseServiceNotExistCreate(dbPath);
  }

  @After
  public void tearDown() throws Exception {
    Util.close(service);
  }

  @Test
  public void reformQueriesTest() throws Exception {
    Collection<Long> candidateIds = new ArrayList<>();
    candidateIds.add((long) 0);
    candidateIds.add((long) 1);
    Util.println(RisoTreeConnector.reformQueries(query, "spatialNode", candidateIds));
  }

  @Test
  public void getRefinedQueriesTest() throws Exception {
    RisoTreeConnector risoTreeConnector = new RisoTreeConnector(service, dataset, 1);
    String rectangleStr =
        "(113.50180238485339, 22.187478257613602, 113.56607615947725,22.225842149771214)";
    MyRectangle rectangle = new MyRectangle(rectangleStr);
    List<String> queries = risoTreeConnector.getRefinedQueries(query, "spatialnode", rectangle);
    Util.println(queries);
  }

}
