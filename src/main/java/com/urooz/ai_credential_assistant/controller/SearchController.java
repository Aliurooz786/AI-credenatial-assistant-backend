package com.urooz.ai_credential_assistant.controller;

import com.urooz.ai_credential_assistant.dto.SearchRequest;
import com.urooz.ai_credential_assistant.entity.Credential;
import com.urooz.ai_credential_assistant.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    // -----------------------------------------------------------
    // POST: Search credentials using semantic similarity
    // -----------------------------------------------------------
    @PostMapping("/search")
    public ResponseEntity<?> searchCredentials(@RequestBody SearchRequest request) {

        log.info("===============================================");
        log.info("API CALL: POST /api/search");
        log.info("Incoming Query → {}", request.getQuery());

        try {
            // Step 4.4: Core similarity search
            List<Credential> results = searchService.searchCredentials(request.getQuery());

            log.info("API SUCCESS: Found {} matching credentials", results.size());
            log.info("===============================================");

            return ResponseEntity.ok(results);

        } catch (Exception e) {

            log.error("❌ API ERROR: Failed to search credentials → {}", e.getMessage());
            log.info("===============================================");

            return ResponseEntity.status(500).body("Search failed: " + e.getMessage());
        }
    }
}