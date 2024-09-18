package com.redhat.training.kafka.serdes;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.training.kafka.model.Weather;

public class WeatherDeserializer implements Deserializer<Weather> {
    private ObjectMapper om = new ObjectMapper();

    @Override
    public Weather deserialize(String topic, byte[] data) {
        try {
            return om.readValue(data, Weather.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
