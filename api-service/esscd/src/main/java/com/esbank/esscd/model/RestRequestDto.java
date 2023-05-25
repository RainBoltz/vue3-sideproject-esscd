package com.esbank.esscd.model;

import java.util.List;

public class RestRequestDto {

    private final List<String> selectedValues;
    private final String keyword;
    private final String filterType;
    private final Integer filterLevel;

    public RestRequestDto(List<String> selectedValues, String keyword, String filterType, Integer filterLevel) {
        this.selectedValues = selectedValues;
        this.keyword = keyword;
        this.filterType = filterType;
        this.filterLevel = filterLevel;
    }

    public List<String> getSelectedValues() {
        return selectedValues;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getFilterType() {
        return filterType;
    }

    public Integer getFilterLevel() {
        return filterLevel;
    }
}
