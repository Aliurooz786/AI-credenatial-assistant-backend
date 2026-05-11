package com.urooz.ai_credential_assistant.controller;

import com.urooz.ai_credential_assistant.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;

    @PostMapping("/embedding")
    public double[] getEmbedding(@RequestBody String input) {
        return embeddingService.generateEmbedding(input);
    }
}
