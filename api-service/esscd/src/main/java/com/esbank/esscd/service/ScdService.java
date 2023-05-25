package com.esbank.esscd.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
//import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ScdService {

    // private final Neo4jClient neo4jClient;
    private final Driver driver;

    ScdService(Driver driver) {
        this.driver = driver;
    }

    public Map<String, List<Object>> fetchFullSystemDiagram() {

        ArrayList<Object> systems = new ArrayList<>();
        ArrayList<Object> relationships = new ArrayList<>();

        try (Session session = driver.session()) {
            List<Record> records = session.readTransaction(tx -> tx.run(""
                    + "MATCH (s:System)-[r:HAS_CONNECTION]->(t:System) "
                    + "RETURN s AS source, r as relationship, t as target;").list());

            records.forEach(record -> {
                Map<String, Object> source = record.get("source").asMap();
                Map<String, Object> relationship = record.get("relationship").asMap();
                Map<String, Object> target = record.get("target").asMap();

                Map<String, String> sourceDetail = Map.of(
                        "sid", source.get("sid").toString(),
                        "alias", source.get("alias").toString(),
                        "name", source.get("name").toString(),
                        "type", source.get("type").toString(),
                        "os", source.get("os").toString(),
                        "lang", source.get("lang").toString());

                Map<String, String> relationshipDetail = Map.of(
                        "protocol", relationship.get("protocol").toString(),
                        "description", relationship.get("description").toString(),
                        "sourceSid", source.get("sid").toString(),
                        "targetSid", target.get("sid").toString());

                Map<String, String> targetDetail = Map.of(
                        "sid", target.get("sid").toString(),
                        "alias", target.get("alias").toString(),
                        "name", target.get("name").toString(),
                        "type", target.get("type").toString(),
                        "os", source.get("os").toString(),
                        "lang", source.get("lang").toString());

                relationships.add(relationshipDetail);
                if (!systems.contains(sourceDetail)) {
                    systems.add(sourceDetail);
                }
                if (!systems.contains(targetDetail)) {
                    systems.add(targetDetail);
                }
            });
        }

        return Map.of("systems", systems, "relationships", relationships);
    }

    public Map<String, List<Object>> fetchSystemDiagram(List<String> selectedValues, String filterType,
            Integer filterLevel) {

        ArrayList<Object> systems = new ArrayList<>();
        ArrayList<Object> relationships = new ArrayList<>();
        String badCypher = ""; // vulnerable of sql injection

        for (int valueCount = 0; valueCount < selectedValues.size(); valueCount++) {

            if (valueCount >= 1) {
                badCypher += " UNION ALL";
            }
            badCypher += String.format(""
                    + " MATCH path = (s:System {%s: '%s'})-[:HAS_CONNECTION*1..%d]-(t:System)"
                    + " WHERE s.sid <> t.sid"
                    + " WITH relationships(path) AS edgesList"
                    + " UNWIND edgesList as edges"
                    + " UNWIND edges as edge"
                    + " RETURN DISTINCT startNode(edge) AS source, edge AS relationship, endNode(edge) AS target",
                    filterType, selectedValues.get(valueCount), filterLevel);
        }

        final String cypher = badCypher; // work-a-round for final static query string

        System.out.println(badCypher);

        try (Session session = driver.session()) {
            List<Record> records = session.readTransaction(tx -> tx.run(cypher).list());

            System.out.println(records);

            records.forEach(record -> {
                Map<String, Object> source = record.get("source").asMap();
                Map<String, Object> relationship = record.get("relationship").asMap();
                Map<String, Object> target = record.get("target").asMap();

                Map<String, String> sourceDetail = Map.of(
                        "sid", source.get("sid").toString(),
                        "alias", source.get("alias").toString(),
                        "name", source.get("name").toString(),
                        "type", source.get("type").toString(),
                        "os", source.get("os").toString(),
                        "lang", source.get("lang").toString());

                Map<String, String> relationshipDetail = Map.of(
                        "protocol", relationship.get("protocol").toString(),
                        "description", relationship.get("description").toString(),
                        "sourceSid", source.get("sid").toString(),
                        "targetSid", target.get("sid").toString());

                Map<String, String> targetDetail = Map.of(
                        "sid", target.get("sid").toString(),
                        "alias", target.get("alias").toString(),
                        "name", target.get("name").toString(),
                        "type", target.get("type").toString(),
                        "os", source.get("os").toString(),
                        "lang", source.get("lang").toString());

                relationships.add(relationshipDetail);
                if (!systems.contains(sourceDetail)) {
                    systems.add(sourceDetail);
                }
                if (!systems.contains(targetDetail)) {
                    systems.add(targetDetail);
                }
            });
        }

        return Map.of("systems", systems, "relationships", relationships);
    }

    public Map<String, List<Object>> fetchFilteringSystem(String keyword, String filterType) {
        ArrayList<Object> results = new ArrayList<>();

        System.out.println(keyword + " - " + filterType);

        try (Session session = driver.session()) {
            final String badCypher = String.format("" // volunerable to sql injections
                    + "MATCH (s:System) WHERE s.%s =~ '(?i).*%s.*' "
                    + "RETURN DISTINCT s.%s as result;", filterType, keyword, filterType);

            List<Record> records = session.readTransaction(tx -> tx.run(badCypher).list());

            records.forEach(record -> {

                System.out.println(record);

                String result = record.get("result").asString();
                results.add(result);
            });
        }

        System.out.println(results);

        return Map.of("results", results);
    }

    public Map<String, List<Object>> fetchShortestPath(String sourceSid, String targetSid) {

        ArrayList<Object> systems = new ArrayList<>();
        ArrayList<Object> paths = new ArrayList<>();

        try (Session session = driver.session()) {
            List<Record> records = session.readTransaction(tx -> tx.run(""
                    + "MATCH (s:System {sid: $sourceSid}) WITH s "
                    + "MATCH (t:System {sid: $targetSid}), p = shortestPath((s)-[*..3]->(t)) "
                    + "RETURN p AS paths", Map.of("sourceSid", sourceSid, "targetSid", targetSid))
                    .list());

            if (records.size() == 0) {
                System.out.println("No path found");
                return Map.of("systems", systems, "paths", paths);
            }

            records.forEach(record -> {
                ArrayList<Object> relationships = new ArrayList<>();

                record.get("paths").asPath().nodes().forEach(node -> {
                    Map<String, Object> system = node.asMap();
                    Map<String, String> systemDetail = Map.of(
                            "sid", system.get("sid").toString(),
                            "alias", system.get("alias").toString(),
                            "name", system.get("name").toString(),
                            "type", system.get("type").toString(),
                            "os", system.get("os").toString(),
                            "lang", system.get("lang").toString());
                    systems.add(systemDetail);
                });

                System.out.println("Node passed");

                record.get("paths").asPath().forEach(edge -> {
                    Map<String, Object> relationship = edge.relationship().asMap();
                    Map<String, String> relationshipDetail = Map.of(
                            "protocol", relationship.get("protocol").toString(),
                            "description", relationship.get("description").toString(),
                            "sourceSid", edge.start().asMap().get("sid").toString(),
                            "targetSid", edge.end().asMap().get("sid").toString());
                    relationships.add(relationshipDetail);
                });

                paths.add(relationships);
            });

        }

        return Map.of("systems", systems, "paths", paths);
    }

}
