package com.urooz.ai_credential_assistant.dto;

public class SearchRequest {

    private String query;

    public SearchRequest() {
    }

    public SearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}