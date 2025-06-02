package com.example.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.cloud.index}")
    private String indexName;

    public SearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

   public List<JsonNode> searchWithJson(String queryJson) throws IOException {
        SearchRequest request = SearchRequest.of(b -> b
                .index(indexName)
                .withJson(new StringReader(queryJson)));

        SearchResponse<JsonNode> response = elasticsearchClient.search(request, JsonNode.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<JsonNode> simpleSearch(String keyword) throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
                .index(indexName)
                .query(q -> q
                        .multiMatch(m -> m
                                .fields("title", "description") // Change fields as needed
                                .query(keyword))));

        SearchResponse<JsonNode> response = elasticsearchClient.search(request, JsonNode.class);

        List<JsonNode> resultList = new ArrayList<>();
        for (Hit<JsonNode> hit : response.hits().hits()) {
            resultList.add(hit.source());
        }
        return resultList;
    }

}
