package com.adaptive.environments.pump_simulator.simulator;

import com.adaptive.environments.pump_simulator.kafka.KafkaDeviceDataProducer;
import com.adaptive.environments.pump_simulator.model.DeviceData;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Random;

@Component
@Profile("simulator")
public class FluidTempOutSimulator implements Simulator {

    private final KafkaDeviceDataProducer kafkaProducerService;
    private final Random random = new Random();

    public FluidTempOutSimulator(KafkaDeviceDataProducer kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public void simulate() {
        Flux.interval(Duration.ofSeconds(5))
                .flatMap(tick -> {
                    long timestamp = Instant.now().toEpochMilli();
                    double temp = 25.0 + random.nextDouble() * 5.0;

                    DeviceData data = DeviceData.builder()
                            .deviceId("pump-01")
                            .sensorId("temp-out-001")
                            .location("outlet")
                            .type("fluid_temp_out")
                            .timestamp(timestamp)
                            .authKey("secret-key")
                            .data(Map.of(
                                    "value", temp,
                                    "unit", "°C"
                            ))
                            .build();

                    return kafkaProducerService.send("iot.device-data", data);
                })
                .subscribe();
    }
}
