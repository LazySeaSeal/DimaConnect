package com.recrutement.app.repository;

import com.recrutement.app.model.Question;
import com.recrutement.app.model.enums.TypeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByOffreId(Long offreId);
    
    List<Question> findByType(TypeQuestion type);
    
    List<Question> findByPointsGreaterThanEqual(Integer points);
}
