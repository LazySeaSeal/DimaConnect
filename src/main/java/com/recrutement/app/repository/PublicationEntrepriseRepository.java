package com.recrutement.app.repository;

import com.recrutement.app.model.PublicationEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PublicationEntrepriseRepository extends JpaRepository<PublicationEntreprise, Long> {
    
    List<PublicationEntreprise> findByEntrepriseId(Long entrepriseId);
    
    List<PublicationEntreprise> findByDatePublicationAfter(LocalDate date);
    
    List<PublicationEntreprise> findByNombreLikesGreaterThan(Integer nombreLikes);

    Page<PublicationEntreprise> findByEntrepriseId(Long entrepriseId, Pageable pageable);
}

