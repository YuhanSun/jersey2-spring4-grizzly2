package CypherMiddleWare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import commons.Neo4jGraphUtility;
import commons.Query_Graph;
import commons.Util;

public class RisoTreeConnectorTest {

  String homeDir, dbPath;
  String query;
  GraphDatabaseService service;

  @Before
  public void setUp() {
    // ClassLoader classLoader = getClass().getClassLoader();
    // File file = new File(classLoader.getResource("").getFile());
    // homeDir = file.getAbsolutePath();
    homeDir = "/Users/zhouyang/Documents/tmp";
    dbPath = homeDir + "/data/graph.db";
    query = "match (a:A)-->(b:B)<--(c:TEST) where a.type = 0 return a, b, c limit 10";
    query = "MATCH (c:C)--(a:A)-[b]-(spatialnode:Spatial) WHERE "
        + "22.187478257613602 <= spatialnode.lat <= 22.225842149771214 AND "
        + "113.50180238485339 <= spatialnode.lon <= 113.56607615947725 " + "RETURN * LIMIT 10";
    query =
        "MATCH (a:`heritage designation`)-[b]-(spatialnode:museum) WHERE 22.187478257613602 <= spatialnode.lat <= 22.225842149771214 AND 113.50180238485339 <= spatialnode.lon <= 113.56607615947725 RETURN spatialnode LIMIT 5";
    Util.println(query);
    service = Neo4jGraphUtility.getDatabaseServiceNotExistCreate(dbPath);
  }

  @After
  public void tearDown() throws Exception {
    Util.close(service);
  }

  @Test
  public void reformQueriesTest() throws Exception {
    Query_Graph query_Graph = CypherDecoder.getQueryGraph(query, "spatialnode", "()", service);
    HashMap<Integer, Collection<Long>> candidates = new HashMap<>();
    Collection<Long> candidateIds = new ArrayList<>();
    candidateIds.add((long) 0);
    candidateIds.add((long) 1);
    Util.println(RisoTreeConnector.reformQueries(query, query_Graph, candidates));
  }

}
