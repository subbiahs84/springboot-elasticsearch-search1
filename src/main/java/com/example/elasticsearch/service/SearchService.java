package com.example.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import com.example.elasticsearch.component.SearchProperties;
import com.example.elasticsearch.config.ElasticClientConfig;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final ElasticClientConfig config;
    private final SearchProperties searchProperties;

    @Value("${elasticsearch.index}")
    private String indexName;

   public SearchService(ElasticsearchClient elasticsearchClient,
                         ElasticClientConfig config,
                         SearchProperties searchProperties) {
        this.elasticsearchClient = elasticsearchClient;
        this.config = config;
        this.searchProperties = searchProperties;
    }

   public List<JsonNode> searchWithJson(String queryJson) throws IOException {
        SearchRequest request = SearchRequest.of(b -> b
                .index(config.getIndexName())
                .withJson(new StringReader(queryJson)));

        SearchResponse<JsonNode> response = elasticsearchClient.search(request, JsonNode.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<JsonNode> simpleSearch(String keyword) throws IOException {

            SearchRequest request = SearchRequest.of(s -> s
                            .index(config.getIndexName())
                            .query(q -> q
                                            .multiMatch(m -> m
                                                            .fields(searchProperties.getKeywordFields())
                                                            .query(keyword))));

            SearchResponse<JsonNode> response = elasticsearchClient.search(request, JsonNode.class);

            List<JsonNode> resultList = new ArrayList<>();
            for (Hit<JsonNode> hit : response.hits().hits()) {
                    resultList.add(hit.source());
            }
            return resultList;
    }

    public List<JsonNode> searchWithKeywordAndFilters(String keyword, Map<String, Object> filters) throws IOException {
        Query keywordQuery = Query.of(q -> q
                .multiMatch(m -> m
                        .fields(searchProperties.getKeywordFields()) // You can adjust these
                        .query(keyword)));

        // Build filter query (term queries)
        List<Query> filterQueries = filters.entrySet().stream()
                .map(entry -> Query.of(q -> q.term(t -> t
                        .field(entry.getKey())
                        .value(v -> v.stringValue(entry.getValue().toString())))))
                .toList();

        Query combinedQuery = Query.of(q -> q
                .bool(b -> b
                        .must(keywordQuery)
                        .filter(filterQueries)));

        SearchRequest request = SearchRequest.of(s -> s
                .index(config.getIndexName())
                .query(combinedQuery));

        SearchResponse<JsonNode> response = elasticsearchClient.search(request, JsonNode.class);

        List<JsonNode> resultList = new ArrayList<>();
        for (Hit<JsonNode> hit : response.hits().hits()) {
            resultList.add(hit.source());
        }
        return resultList;
    }

}
