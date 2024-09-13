package com.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.domain.LibraryEvent;
import com.kafka.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventsController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }


    @PostMapping("/v1/libraryevents")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("library event: {} " , libraryEvent);
        // invoke kafka producer
        libraryEventsProducer. sendLibraryEventAsyncProducerRecord(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }


}
