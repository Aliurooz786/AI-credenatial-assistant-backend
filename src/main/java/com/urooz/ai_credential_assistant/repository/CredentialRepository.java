package com.urooz.ai_credential_assistant.repository;

import com.urooz.ai_credential_assistant.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {

}