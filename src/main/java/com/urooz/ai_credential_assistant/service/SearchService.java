package com.urooz.ai_credential_assistant.service;

import com.urooz.ai_credential_assistant.dto.SearchRequest;
import com.urooz.ai_credential_assistant.dto.EmbeddingRequest;
import com.urooz.ai_credential_assistant.dto.EmbeddingResponse;
import com.urooz.ai_credential_assistant.entity.Credential;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private static final String OLLAMA_URL = "http://localhost:11434/api/embed";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    // -----------------------------------------------------------
    // STEP 4.2: Generate embedding for user query
    // -----------------------------------------------------------
    public double[] generateQueryEmbedding(String queryText) {

        log.info("STEP 4.2: Generating embedding for query → {}", queryText);

        EmbeddingRequest request = new EmbeddingRequest("nomic-embed-text", queryText);

        EmbeddingResponse response;

        try {
            response = restTemplate.postForObject(
                    OLLAMA_URL,
                    request,
                    EmbeddingResponse.class);
        } catch (Exception e) {
            log.error("❌ ERROR: Failed to call Ollama for query embedding → {}", e.getMessage());
            throw new RuntimeException("Embedding generation failed for query");
        }

        if (response == null || response.getEmbeddings() == null || response.getEmbeddings().isEmpty()) {
            log.error("❌ ERROR: Empty embedding returned by Ollama!");
            throw new RuntimeException("No embedding returned for query");
        }

        double[] vector = response.getEmbeddings().get(0)
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        log.info("STEP 4.2 COMPLETE: Query embedding length = {}", vector.length);
        return vector;
    }

    // -----------------------------------------------------------
    // STEP 4.3 + 4.4: Vector similarity search (pgvector)
    // -----------------------------------------------------------
    public List<Credential> searchCredentials(String queryText) {

        log.info("===============================================================");
        log.info("STEP 4.4: Starting similarity search for query → {}", queryText);

        // Step A: generate vector
        double[] embeddingVector = generateQueryEmbedding(queryText);

        // Step B: convert array → pgvector string format
        StringBuilder vectorBuilder = new StringBuilder();
        vectorBuilder.append("[");
        for (int i = 0; i < embeddingVector.length; i++) {
            vectorBuilder.append(embeddingVector[i]);
            if (i != embeddingVector.length - 1)
                vectorBuilder.append(",");
        }
        vectorBuilder.append("]");
        String pgVector = vectorBuilder.toString();

        log.info("STEP 4.4: Converted Query Embedding → pgvector format");

        // Step C: Raw SQL → return as list of Object[]
        String sql = """
                    SELECT id, tool, environment, url, username, password, description
                    FROM credentials
                    ORDER BY embedding <-> CAST(:vector AS vector)
                    LIMIT 3
                """;

        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter("vector", pgVector);

        List<Object[]> rows = nativeQuery.getResultList();

        log.info("SQL returned {} rows", rows.size());

        // Step D: convert rows → Credential objects
        List<Credential> results = rows.stream().map(row -> {
            Credential c = new Credential();
            c.setId(((Number) row[0]).longValue());
            c.setTool((String) row[1]);
            c.setEnvironment((String) row[2]);
            c.setUrl((String) row[3]);
            c.setUsername((String) row[4]);
            c.setPassword((String) row[5]);
            c.setDescription((String) row[6]);
            return c;
        }).toList();

        log.info("STEP 4.4 COMPLETE: Final mapped results = {}", results.size());
        log.info("===============================================================");

        return results;
    }
}