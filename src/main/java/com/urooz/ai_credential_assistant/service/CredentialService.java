package com.urooz.ai_credential_assistant.service;

import com.urooz.ai_credential_assistant.dto.EmbeddingRequest;
import com.urooz.ai_credential_assistant.dto.EmbeddingResponse;
import com.urooz.ai_credential_assistant.entity.Credential;
import com.urooz.ai_credential_assistant.repository.CredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CredentialService {

    private static final Logger log = LoggerFactory.getLogger(CredentialService.class);

    private static final String OLLAMA_URL = "http://localhost:11434/api/embed";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CredentialRepository credentialRepository;

    // -----------------------------------------------------------
    // 1) Convert credential details → ONE semantic text
    // -----------------------------------------------------------
    private String buildTextForEmbedding(Credential credential) {

        String combined = credential.getTool() + " " +
                credential.getEnvironment() + " " +
                credential.getDescription() + " " +
                credential.getUrl() + " " +
                credential.getUsername();

        log.info("STEP 1: Combined text for embedding = {}", combined);
        return combined;
    }

    // -----------------------------------------------------------
    // 2) Generate embedding → Ollama API call
    // -----------------------------------------------------------
    private double[] generateEmbedding(String text) {

        log.info("STEP 2: Calling Ollama for embedding...");
        EmbeddingRequest request = new EmbeddingRequest("nomic-embed-text", text);

        EmbeddingResponse response = null;

        try {
            response = restTemplate.postForObject(
                    OLLAMA_URL,
                    request,
                    EmbeddingResponse.class);
        } catch (Exception e) {
            log.error("❌ ERROR: Failed to call Ollama API: {}", e.getMessage());
            throw new RuntimeException("Ollama embedding API call failed");
        }

        if (response == null || response.getEmbeddings() == null || response.getEmbeddings().isEmpty()) {
            log.error("❌ ERROR: Invalid embedding response from Ollama");
            throw new RuntimeException("Embedding generation failed");
        }

        double[] finalVector = response.getEmbeddings().get(0)
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        log.info("STEP 2 COMPLETED: Embedding vector size = {}", finalVector.length);
        return finalVector;
    }

    // -----------------------------------------------------------
    // 3) Save credential with vector in pgvector DB
    // -----------------------------------------------------------
    public Credential saveCredential(Credential credential) {

        log.info("======================================================");
        log.info("STEP 0: Starting credential save flow...");
        log.info("Incoming data: tool={}, env={}, desc={}",
                credential.getTool(),
                credential.getEnvironment(),
                credential.getDescription());

        // Step A: convert into semantic text
        String finalText = buildTextForEmbedding(credential);

        // Step B: call embedding generator
        double[] embeddingVector = generateEmbedding(finalText);

        // Step C: set embedding into entity
        credential.setEmbedding(embeddingVector);

        // Step D: save in DB
        Credential saved = credentialRepository.save(credential);

        log.info("STEP 3 COMPLETE: Credential saved with ID = {}", saved.getId());
        log.info("======================================================");

        return saved;
    }
}