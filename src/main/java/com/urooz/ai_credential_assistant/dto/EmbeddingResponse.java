package com.urooz.ai_credential_assistant.dto;

import java.util.List;

public class EmbeddingResponse {

    private List<List<Double>> embeddings;

    public List<List<Double>> getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(List<List<Double>> embeddings) {
        this.embeddings = embeddings;
    }
}