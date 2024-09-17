package com.redhat.training.kafka.serde;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.training.kafka.model.RiskAssessment;

public class RiskAssessmentSerializer implements Serializer<RiskAssessment> {
    private ObjectMapper om = new ObjectMapper();
    @Override
    public byte[] serialize(String arg0, RiskAssessment arg1) {
        try {
            return om.writeValueAsBytes(arg1);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException(jpe);
        }
    }

}
