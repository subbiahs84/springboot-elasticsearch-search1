package com.example.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

@Configuration
public class ElasticClientConfig {

    @Value("${elasticsearch.mode}")
    private String mode;

    @Value("${elasticsearch.index}")
    private String indexName;

    // Elastic Cloud values
    @Value("${elasticsearch.cloud.endpoint:}")
    private String cloudEndpoint;

    @Value("${elasticsearch.cloud.apiKey}")
    private String apiKey;

    // Local Elasticsearch values
    @Value("${elasticsearch.local.host:}")
    private String localHost;

    @Value("${elasticsearch.local.port:9200}")
    private int localPort;

    @Value("${elasticsearch.local.scheme:http}")
    private String localScheme;

    @Value("${elasticsearch.local.username:}")
    private String username;

    @Value("${elasticsearch.local.password:}")
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        if ("cloud".equalsIgnoreCase(mode)) {
            return createCloudClient();
        } else {
            return createLocalClient();
        }
    }

    public String getIndexName() {
        return indexName;
    }

    private ElasticsearchClient createCloudClient() {
        RestClient restClient = RestClient.builder(HttpHost.create(cloudEndpoint))
            .setDefaultHeaders(new BasicHeader[]{
                new BasicHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + apiKey)
            }).build();

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    private ElasticsearchClient createLocalClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(username, password));

        RestClient restClient = RestClient.builder(
            new HttpHost(localHost, localPort, localScheme))
            .setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
            .build();

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
