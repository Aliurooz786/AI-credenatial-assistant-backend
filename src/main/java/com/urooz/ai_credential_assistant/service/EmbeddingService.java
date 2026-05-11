package com.urooz.ai_credential_assistant.service;

import com.urooz.ai_credential_assistant.dto.EmbeddingRequest;
import com.urooz.ai_credential_assistant.dto.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/embed";

    @Autowired
    private RestTemplate restTemplate;

    public double[] generateEmbedding(String text) {

        // Request build
        EmbeddingRequest request = new EmbeddingRequest("nomic-embed-text", text);

        // API call
        EmbeddingResponse response = restTemplate.postForObject(
                OLLAMA_URL,
                request,
                EmbeddingResponse.class);

        // Extract embedding
        if (response != null && response.getEmbeddings() != null && !response.getEmbeddings().isEmpty()) {
            return response.getEmbeddings().get(0)
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
        }

        throw new RuntimeException("Failed to generate embedding");
    }
}