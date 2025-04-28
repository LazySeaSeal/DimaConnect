package com.recrutement.app.service;

import com.recrutement.app.model.Test;
import com.recrutement.app.model.Question;
import com.recrutement.app.repository.TestRepository;
import com.recrutement.app.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    // start a test il candidat by id w test specific by id zeda
    public Test lancerTest(Long candidatId, Long testId) {
        return testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test introuvable"));
    }

    // add question lil test
    public Question ajouterQuestion(Long testId, Question question) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test introuvable"));

        question.setTest(test);
        return questionRepository.save(question);
    }

    // delete question
    public void supprimerQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalArgumentException("Question introuvable");
        }
        questionRepository.deleteById(questionId);
    }

    // les test ali sna3hum employee (id employee)
    public List<Test> getTestsByEmployeId(Long employeId) {
        return testRepository.findByCreateurId(employeId);
    }
}