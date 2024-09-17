package com.redhat.training.kafka.model;

public class RiskAssessment {
    String customerId;
    int assessmentScore;
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public int getAssessmentScore() {
        return assessmentScore;
    }
    public void setAssessmentScore(int assessmentScore) {
        this.assessmentScore = assessmentScore;
    }
}
