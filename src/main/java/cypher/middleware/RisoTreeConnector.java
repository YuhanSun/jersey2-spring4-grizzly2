package cypher.middleware;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import commons.Query_Graph;
import commons.Util;
import graph.RisoTreeQueryPN;

public class RisoTreeConnector {
  GraphDatabaseService service;
  RisoTreeQueryPN risoTreeQueryPN;
  final static int bulkSize = 500; // the number of candidates to form a cypher query.

  public RisoTreeConnector(GraphDatabaseService service, String dataset, int MAX_HOPNUM) {
    // TODO Auto-generated constructor stub
    this.service = service;
    risoTreeQueryPN = new RisoTreeQueryPN(service, dataset, MAX_HOPNUM);
  }


  /**
   * Given an input query to get the refined queries by using RisoTree.
   *
   * @param query
   * @param spatialNode
   * @param rectangleStr
   * @return
   * @throws Exception
   */
  public List<String> getRefinedQueries(String query, String spatialNode, String rectangleStr)
      throws Exception {
    Query_Graph query_Graph =
        CypherDecoder.getQueryGraph(query, spatialNode, rectangleStr, service);
    HashMap<Integer, Collection<Long>> candidates = risoTreeQueryPN.getCandidateSet(query_Graph);

    if (candidates.size() == 0) {
      return new LinkedList<>();
    } else if (candidates.size() > 1) {
      throw new Exception("More than one candidates is returned!");
    }
    return reformQueries(query, query_Graph, candidates);
  }

  /**
   * Given a collection of candidates to form the new queries with id in operator.
   *
   * @param query
   * @param query_Graph
   * @param candidates
   * @return
   * @throws Exception
   */
  public static List<String> reformQueries(String query, Query_Graph query_Graph,
      HashMap<Integer, Collection<Long>> candidates) throws Exception {
    List<String> res = new LinkedList<>();
    int startQueryNodeId = candidates.keySet().iterator().next();
    String startQueryNodeVariable = query_Graph.nodeVariables[startQueryNodeId];
    int index = 0;
    Util.println(query);

    String[] queryParts = generateQueryCombineParts(query);

    if (queryParts.length > 2) {
      throw new Exception(String.format("Query: %s. \nIt has more than one where!", query));
    }
    String idString = "[";
    for (long candidateId : candidates.get(startQueryNodeId)) {
      if (index == 0) {
        idString = String.format("[%d", candidateId);
        index++;
        continue;
      }
      idString += String.format(",%d", candidateId);
      index++;
      if (index == bulkSize) {
        idString += "]";
        query = combineQuery(queryParts, startQueryNodeVariable, idString);
        res.add(query);
        index = 0;
      }
    }
    if (index != 0) {
      idString += "]";
      query = combineQuery(queryParts, startQueryNodeVariable, idString);
      res.add(query);
    }
    return res;
  }

  public static String[] generateQueryCombineParts(String query) {
    String queryLowercase = query.toLowerCase();
    int splitPos = queryLowercase.indexOf("where") + 5;
    return new String[] {query.substring(0, splitPos), query.substring(splitPos, query.length())};
  }

  private static String combineQuery(String[] queryParts, String startQueryNodeVariable,
      String idString) {
    if (queryParts.length == 1) {
      return queryParts[0] + idString;
    }
    return queryParts[0] + String.format(" id(%s) in %s and", startQueryNodeVariable, idString)
        + queryParts[1];
  }

}
