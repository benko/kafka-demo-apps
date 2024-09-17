package com.redhat.training.kafka;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherSerializer implements Serializer<Weather> {
    private ObjectMapper om = new ObjectMapper();

    @Override
    public byte[] serialize(String arg0, Weather arg1) {
        try {
            return om.writeValueAsBytes(arg1);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException(jpe);
        }
    }

}
