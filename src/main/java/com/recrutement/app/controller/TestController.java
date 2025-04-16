package com.recrutement.app.controller;

import com.recrutement.app.model.Test;
import com.recrutement.app.model.Question;
import com.recrutement.app.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/lancer")
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    public ResponseEntity<Test> lancerTest(@RequestParam Long candidatId, @RequestParam Long testId) {
        Test test = testService.lancerTest(candidatId, testId);
        return ResponseEntity.ok(test);
    }
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    @PostMapping("/{testId}/questions")
    public ResponseEntity<Question> ajouterQuestion(@PathVariable Long testId, @RequestBody Question question) {
        Question createdQuestion = testService.ajouterQuestion(testId, question);
        return ResponseEntity.ok(createdQuestion);
    }
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> supprimerQuestion(@PathVariable Long questionId) {
        testService.supprimerQuestion(questionId);
        return ResponseEntity.ok().build();
    }
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH') or hasRole('ADMIN')")
    @GetMapping("/employe/{employeId}")
    public ResponseEntity<List<Test>> getTestsByEmploye(@PathVariable Long employeId) {
        List<Test> tests = testService.getTestsByEmployeId(employeId);
        return ResponseEntity.ok(tests);
    }
}