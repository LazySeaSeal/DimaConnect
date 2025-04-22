package com.recrutement.app.repository;

import com.recrutement.app.model.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
}
