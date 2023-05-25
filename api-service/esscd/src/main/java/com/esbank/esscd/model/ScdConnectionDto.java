package com.esbank.esscd.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

public class ScdConnectionDto {

	private final String protocol;

    private final String description;

    private final String sourceSid;

    private final String targetSid;

    public ScdConnectionDto(String protocol, String description, String sourceSid, String targetSid) {
        this.protocol = protocol;
        this.description = description;
        this.sourceSid = sourceSid;
        this.targetSid = targetSid;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDescription() {
        return description;
    }

    public String getSourceSid() {
        return sourceSid;
    }

    public String getTargetSid() {
        return targetSid;
    }

    @Override
    public String toString() {
        return "ScdRelationship{" +
                ", protocol='" + protocol + '\'' +
                ", description='" + description + '\'' +
                ", sourceSid='" + sourceSid + '\'' +
                ", targetSid='" + targetSid + '\'' +
                '}';
    }
}

