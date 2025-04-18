package com.recrutement.app.dto;

public class MatchingRequest {
    private Long candidatId;
    private Integer seuilMatching;

    // Getters and setters
    public Long getCandidatId() {
        return candidatId;
    }

    public void setCandidatId(Long candidatId) {
        this.candidatId = candidatId;
    }

    public Integer getSeuilMatching() {
        return seuilMatching;
    }

    public void setSeuilMatching(Integer seuilMatching) {
        this.seuilMatching = seuilMatching;
    }
}