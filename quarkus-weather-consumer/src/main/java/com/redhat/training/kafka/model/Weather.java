package com.redhat.training.kafka.model;

import java.io.Serializable;

public class Weather implements Serializable {
    private WeatherType weather;

    public WeatherType getWeather() {
        return weather;
    }

    public void setWeather(WeatherType weather) {
        this.weather = weather;
    }

    public String toString() {
        return "Weather will be " + this.getWeather().toString();
    }
}
