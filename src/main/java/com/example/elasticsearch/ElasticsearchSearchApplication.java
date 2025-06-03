package com.example.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.elasticsearch.component.SearchProperties;

@SpringBootApplication
@EnableConfigurationProperties(SearchProperties.class)
public class ElasticsearchSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchSearchApplication.class, args);
    }
}
