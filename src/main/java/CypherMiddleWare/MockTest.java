package CypherMiddleWare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.neo4j.graphdb.GraphDatabaseService;
import commons.Neo4jGraphUtility;
import commons.Query_Graph;
import commons.Util;

public class MockTest {

  String homeDir, dbPath;
  String query;
  GraphDatabaseService service;

  public void ini() {
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

  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub
    MockTest mockTest = new MockTest();
    mockTest.ini();
    mockTest.reformQueriesTest();
    Util.close(mockTest.service);
  }

  public void reformQueriesTest() throws Exception {
    Query_Graph query_Graph =
        CypherDecoder.getQueryGraph(query, "spatialnode", "(0, 0, 0,0)", service);
    HashMap<Integer, Collection<Long>> candidates = new HashMap<>();
    Collection<Long> candidateIds = new ArrayList<>();
    candidateIds.add((long) 0);
    candidateIds.add((long) 1);
    candidates.put(0, candidateIds);
    Util.println(RisoTreeConnector.reformQueries(query, query_Graph, candidates));
  }

}
