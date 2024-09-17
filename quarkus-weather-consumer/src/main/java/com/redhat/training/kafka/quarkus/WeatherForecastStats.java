package com.redhat.training.kafka.quarkus;

import java.util.HashMap;
import java.util.Map;

import com.redhat.training.kafka.model.Weather;
import com.redhat.training.kafka.model.WeatherType;

import jakarta.inject.Singleton;

@Singleton
public class WeatherForecastStats {
    HashMap<WeatherType,Integer> weatherStats = new HashMap<>();

    public void addForecast(Weather w) {
        int currentCount = 0;
        if (this.weatherStats.keySet().contains(w.getWeather())) {
            currentCount = this.weatherStats.get(w.getWeather());
        }
        currentCount++;
        this.weatherStats.put(w.getWeather(), currentCount);
    }

    public Map<WeatherType,Integer> getHistogram() {
        return this.weatherStats;
    }
}
