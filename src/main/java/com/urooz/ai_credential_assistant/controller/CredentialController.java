package com.urooz.ai_credential_assistant.controller;

import com.urooz.ai_credential_assistant.entity.Credential;
import com.urooz.ai_credential_assistant.service.CredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    private static final Logger log = LoggerFactory.getLogger(CredentialController.class);

    @Autowired
    private CredentialService credentialService;

    // -----------------------------------------------------------
    // POST: Add new credential entry with embedding
    // -----------------------------------------------------------
    @PostMapping
    public ResponseEntity<?> saveCredential(@RequestBody Credential credential) {

        log.info("==============================================");
        log.info("API CALL: POST /api/credentials");
        log.info("Incoming Request → tool={}, env={}, user={}",
                credential.getTool(),
                credential.getEnvironment(),
                credential.getUsername());

        try {
            Credential saved = credentialService.saveCredential(credential);

            log.info("API SUCCESS: Credential saved with ID={} and embedding size={}",
                    saved.getId(),
                    saved.getEmbedding().length);

            log.info("==============================================");

            return ResponseEntity.ok(saved);

        } catch (Exception e) {

            log.error("❌ API ERROR: Failed to save credential → {}", e.getMessage());
            log.info("==============================================");

            return ResponseEntity.status(500).body("Failed to save credential: " + e.getMessage());
        }
    }
}