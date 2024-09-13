package com.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.domain.LibraryEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class LibraryEventsProducer {
    private static final Log LOG = LogFactory.getLog(LibraryEventsProducer.class);

    @Value("${spring.kafka.topic}")
    public String topic;

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public LibraryEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendLibraryEventAsync(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.libraryId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        CompletableFuture<SendResult<Integer, String>> resultCompletableFuture = kafkaTemplate.send("library-events", key, value);

        resultCompletableFuture.whenComplete((sendResult, throwable) -> {
            if (throwable == null) {
                // success
                handleSuccess(key, value, sendResult);
            } else {
                handleException(key, value, throwable);
            }
        });
    }

    public SendResult<Integer, String> sendLibraryEventSynh(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        Integer key = libraryEvent.libraryId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        SendResult<Integer, String> sendResult = kafkaTemplate.send("library-events", key, value)
                .get(3, TimeUnit.SECONDS);


        handleSuccess(key, value, sendResult);
        return sendResult;
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventAsyncProducerRecord(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.libraryId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value);

        CompletableFuture<SendResult<Integer, String>> resultCompletableFuture = kafkaTemplate.send(producerRecord);

        return resultCompletableFuture.whenComplete((sendResult, throwable) -> {
            if (throwable == null) {
                // success
                handleSuccess(key, value, sendResult);
            } else {
                handleException(key, value, throwable);
            }
        });
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }

    public void handleException(Integer key, String value, Throwable throwable) {
        LOG.error("Error sending the message with key " + key + " and value "+ value + "   ,the exception is: " + throwable.getMessage());
    }

    public void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        LOG.info("Message sent successfully for the key: " + key + " and the value is: " + value + " partition is: " + sendResult.getRecordMetadata().partition());
    }
}
