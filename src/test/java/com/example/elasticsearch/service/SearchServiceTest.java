package com.example.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SearchServiceTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private SearchService searchService;

    public SearchServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchReturnsResults() throws Exception {
        // Given
        JsonNode inputQuery = new ObjectMapper().readTree("{\"query\": {\"match_all\": {}}}");

        SearchResponse<JsonNode> response = mock(SearchResponse.class);

        Hit<JsonNode> hit = mock(Hit.class);
        when(hit.source()).thenReturn(new ObjectMapper().readTree("{\"id\":\"1\",\"title\":\"test\"}"));

        // Mock search hit
        Hit<JsonNode> mockHit = mock(Hit.class);
        JsonNode fakeSource = new ObjectMapper().readTree("{\"id\":\"1\", \"title\":\"test title\"}");
        when(mockHit.source()).thenReturn(fakeSource);

        // Mock HitsMetadata
        HitsMetadata<JsonNode> mockHitsMetadata = mock(HitsMetadata.class);
        when(mockHitsMetadata.hits()).thenReturn(List.of(mockHit));

        // Mock SearchResponse
        SearchResponse<JsonNode> mockResponse = mock(SearchResponse.class);
        when(mockResponse.hits()).thenReturn(mockHitsMetadata); // ✅ critical fix

        // Mock the client
        when(elasticsearchClient.search(any(SearchRequest.class), eq(JsonNode.class)))
                .thenReturn(mockResponse);

        // Act
        List<JsonNode> result = searchService.searchWithJson(inputQuery.toString());

        // Assert
        assertEquals(1, result.size());
        assertEquals("test title", result.get(0).get("title").asText());

    }
}
