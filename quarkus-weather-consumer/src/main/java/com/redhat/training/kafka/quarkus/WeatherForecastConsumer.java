package com.redhat.training.kafka.quarkus;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.concurrent.CompletionStage;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.redhat.training.kafka.model.Weather;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;


@Singleton
public class WeatherForecastConsumer {

    @Inject
    WeatherForecastStats stats;

    // Injection approach:
    // @Inject
    // @Channel("weather-forecast")
    // Publisher<Weather> valuesPublisher;
    // Multi<Weather> values;

    // Do low-level client manipulation:
    @Inject
    KafkaClientService kcs;
    void onStartup(@Observes StartupEvent se) {
        Log.infov("Consumer starting up, consumer for channel \"weather-forecast\" has an ID of {0}",
                    kcs.getConsumer("weather-forecast").configuration().get(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG));
    }

    @Incoming("weather-forecast")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> processReading(Message<ConsumerRecord<String,Weather>> weather) {
        String r = weather.getPayload().key();
        Weather w = weather.getPayload().value();
        stats.addForecast(w);
        Log.infov("Received weather forecast run {0}: {1}", r, w.getWeather());

        // use KafkaClientService directly to commit offsets to broker
        // WARNING: this is VERY fishy as consumer is not thread-safe
        // weather.getPayload().partition() -> this record's partition
        // weather.getPayload().offset() -> this record's offset
        // LOG.info("Committing current offsets...");
        // kcs.getConsumer("weather-forecast").commit(Map<TopicPartition,OffsetAndMetadata>);
        // kcs.getConsumer("weather-forecast").commitAsync(Map<TopicPartition,OffsetAndMetadata>);
        return weather.ack();
    }

    // Above method can also be:
    // public void consume(Weather forecast) {
    // public Uni<Void> consume(Message<Weather> msg) {
    // public Uni<Void> consume(ConsumerRecord<String, Weather> rec) {
    // public CompletionStage<Void> processReading(Message<Weather> weather) {

    // If long-running, annotate with either:
    // @Blocking
    // @Transactional
    // public Multi<Integer> getEvents() {
    //     // stats.add(humidityValue);
    //     // System.out.println("Received humidity value: " + humidityValue);
    //     return values;
    // }

}
