package com.example.elasticsearch.component;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "search")
public class SearchProperties {
    private List<String> keywordFields;

    public List<String> getKeywordFields() {
        return keywordFields;
    }

    public void setKeywordFields(List<String> keywordFields) {
        this.keywordFields = keywordFields;
    }
}
