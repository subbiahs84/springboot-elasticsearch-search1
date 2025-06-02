package com.example.elasticsearch.controller;

import com.example.elasticsearch.service.SearchService;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @RequestMapping("/get")
    public ResponseEntity<?> searchGet(){
        return ResponseEntity.ok("Welcome to Elasticsearch");
    }

    @PostMapping
    public ResponseEntity<?> searchByJson(@RequestBody String queryJson) throws IOException {
        try {
            List<JsonNode> results = searchService.searchWithJson(queryJson);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
             throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }
}
