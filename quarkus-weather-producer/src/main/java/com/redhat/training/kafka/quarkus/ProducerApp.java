package com.redhat.training.kafka.quarkus;

import java.time.Duration;
import java.util.Random;

import jakarta.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import com.redhat.training.kafka.model.Weather;
import com.redhat.training.kafka.model.WeatherType;

@ApplicationScoped
public class ProducerApp {
    private static final Logger LOG = Logger.getLogger(ProducerApp.class);

    private final Random random = new Random();

    @Outgoing("weather-forecast")
    public Multi<Record<String, Weather>> generate() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onOverflow().drop()
                .map(tick -> {
                    String fcast = "fcast-run-" + random.nextInt(10);

                    Weather w = new Weather();
                    w.setWeather(WeatherType.values()[random.nextInt(WeatherType.values().length)]);

                    LOG.infov("Forecast run: {0}, forecast: {1}", fcast, w);
                    return Record.of(fcast, w);
                });
    }
}
