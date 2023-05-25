package com.esbank.esscd.model;

import java.util.Map;
import java.util.Objects;

public class RestResponseDto {

    private final Map<String, Objects> content;

    public RestResponseDto(Map<String, Objects> content) {
        this.content = content;
    }

    public Map<String, Objects> getGraph() {
        return content;
    }
}
