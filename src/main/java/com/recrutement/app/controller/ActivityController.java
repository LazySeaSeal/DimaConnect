package com.recrutement.app.controller;

import com.recrutement.app.dto.ActivityDTO;
import com.recrutement.app.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@Tag(name = "Activities", description = "Candidate activities APIs")
public class ActivityController {

    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{candidateId}")
    @Operation(summary = "Get all activities (posts and comments) of a candidate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activities retrieved successfully")
    })
    public ResponseEntity<List<ActivityDTO>> getCandidateActivities(@PathVariable Long candidateId) {
        return ResponseEntity.ok(activityService.getCandidateActivities(candidateId));
    }
}