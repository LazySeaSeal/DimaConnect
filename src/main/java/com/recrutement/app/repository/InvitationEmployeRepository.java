package com.recrutement.app.repository;

import com.recrutement.app.model.InvitationEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationEmployeRepository extends JpaRepository<InvitationEmploye, Long> {

    Optional<InvitationEmploye> findByToken(String token);

    Optional<InvitationEmploye> findByEmail(String email);

    boolean existsByEmailAndEstUtilisee(String email, boolean estUtilisee);

}