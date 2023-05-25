package com.esbank.esscd.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import com.esbank.esscd.model.RestRequestDto;
import com.esbank.esscd.service.ScdService;

@RestController
@RequestMapping("/esscd")
public class ScdController {

    @Autowired
    ScdService scdService;

    @GetMapping("/diagram")
    public Map<String, List<Object>> getDiagram() {
        try {
            return scdService.fetchFullSystemDiagram();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new HashMap<>();
        }
        
    }

    @PostMapping("/diagram")
    public Map<String, List<Object>> getSubDiagram(@RequestBody RestRequestDto requestParams) {
        return scdService.fetchSystemDiagram(requestParams.getSelectedValues(), requestParams.getFilterType(), requestParams.getFilterLevel());
    }

    @PostMapping("/filter")
    public Map<String, List<Object>> getFilter(@RequestBody RestRequestDto requestParams) {
        return scdService.fetchFilteringSystem(requestParams.getKeyword(), requestParams.getFilterType());
    }

    // not using this method now
    @PostMapping("/path")
    public Map<String, List<Object>> getShortestPath(@RequestBody RestRequestDto requestParams) {
        // the value here is `sid`
        if (requestParams.getSelectedValues().size() == 2) {
            String sourceSid = requestParams.getSelectedValues().get(0);
            String targetSid = requestParams.getSelectedValues().get(1);
            Map<String, List<Object>> result = scdService.fetchShortestPath(sourceSid, targetSid);
            Map<String, List<Object>> resultReverse = scdService.fetchShortestPath(targetSid, sourceSid);

            System.out.println(result);
            System.out.println(resultReverse);

            List<Object> paths = new ArrayList<>();
            paths.addAll(result.get("paths"));
            paths.addAll(resultReverse.get("paths"));

            List<Object> systems = new ArrayList<>();
            systems.addAll(result.get("systems"));
            systems.addAll(resultReverse.get("systems"));

            Map<String, List<Object>> resultMerged = new HashMap<>();
            resultMerged.put("systems", systems);
            resultMerged.put("paths", paths);

            return resultMerged;
        } else {
            return Map.of("systems", new ArrayList<>(), "paths", new ArrayList<>());
        }
    }

}
