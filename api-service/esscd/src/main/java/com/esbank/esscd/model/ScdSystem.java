package com.esbank.esscd.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("System")
public class ScdSystem {

	@Id
	private final String sid;

	private final String alias;

    private final String name;

	private final String type;

    public ScdSystem(String sid, String alias, String name, String type) {
        this.sid = sid;
        this.alias = alias;
        this.name = name;
        this.type = type;
    }

    public String getSid() {
        return sid;
    }

    public String getAlias() {
        return alias;
    }

    public String getName() {
        return name;
    }   

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "EsSystem{" +
                "sid='" + sid + '\'' +
                ", alias='" + alias + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

