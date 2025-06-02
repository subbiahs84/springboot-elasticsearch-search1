package com.example.elasticsearch.controller;

import com.example.elasticsearch.service.SearchService;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
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

    @PostMapping("/simple")
    public ResponseEntity<?> simpleSearch(@RequestParam String keyword) throws IOException {
        try {
            List<JsonNode> results = searchService.simpleSearch(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
             throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }

    @PostMapping("/combined")
    public ResponseEntity<List<JsonNode>> searchWithKeywordAndFilters(@RequestBody Map<String, Object> request) {
        String keyword = (String) request.get("keyword");
        Map<String, Object> filters = (Map<String, Object>) request.get("filters");

        try {
            List<JsonNode> results = searchService.searchWithKeywordAndFilters(keyword, filters);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

}
