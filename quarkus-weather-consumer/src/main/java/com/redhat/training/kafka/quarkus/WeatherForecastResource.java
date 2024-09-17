package com.redhat.training.kafka.quarkus;

import java.util.Map;

import com.redhat.training.kafka.model.WeatherType;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@ApplicationScoped
@Path("/weather")
public class WeatherForecastResource {

    @Inject
    WeatherForecastStats stats;

    @GET
    public Map<WeatherType,Integer> getWeatherStats() {
        Log.info("Returning current forecast histogram: " + stats.getHistogram());
        return stats.getHistogram();
    }
}
