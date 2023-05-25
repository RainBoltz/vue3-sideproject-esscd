package com.esbank.esscd.model;

import java.util.Objects;

public class ScdSystemDto {
    private final String sid ;
    private final String alias;
    private final String name;
    private final String type;
    private final String os;
    private final String lang;

    public ScdSystemDto(String sid, String alias, String name, String type, String os, String lang) {
        this.sid = sid;
        this.alias = alias;
        this.name = name;
        this.type = type;
        this.os = os;
        this.lang = lang;
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

    public String getOs() {
        return os;
    }

    public String getLang() {
        return lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScdSystemDto that = (ScdSystemDto) o;
        return Objects.equals(sid, that.sid) &&
                Objects.equals(alias, that.alias) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(os, that.os) &&
                Objects.equals(lang, that.lang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid, alias, name, type, os, lang);
    }
}
