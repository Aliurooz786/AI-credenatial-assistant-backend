package com.urooz.ai_credential_assistant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "credentials")
@Getter
@Setter
@NoArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tool;

    @Column(nullable = false)
    private String environment;

    private String url;
    private String username;
    private String password;

    @Column(columnDefinition = "text")
    private String description;

    /*
     * Embedding vector (768 dimensions)
     * Stored in PostgreSQL pgvector column
     */
    @Column(columnDefinition = "vector(768)")
    private double[] embedding;

}