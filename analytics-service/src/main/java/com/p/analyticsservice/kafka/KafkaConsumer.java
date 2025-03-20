package com.p.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent pEvent = PatientEvent.parseFrom(event);

            log.info("Received patient event in analytics Kafka consumer: {}", pEvent.toString());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error at deserializing kafka Patient Event: {}", event);
        }
    }
}
