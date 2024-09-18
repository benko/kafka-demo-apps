package com.redhat.training.kafka.serdes;

import com.redhat.training.kafka.model.Weather;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class WeatherObjectDeserializer extends ObjectMapperDeserializer<Weather> {

    public WeatherObjectDeserializer() {
        super(Weather.class);
    }
}
