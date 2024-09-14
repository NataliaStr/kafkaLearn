package com.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.domain.LibraryEvent;
import com.kafka.domain.LibraryEventType;
import com.kafka.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        libraryEventsProducer.sendLibraryEventAsyncProducerRecord(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevents")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("library event: {} " , libraryEvent);

        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;
        libraryEventsProducer.sendLibraryEventAsyncProducerRecord(libraryEvent);

        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

    private static ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        if (libraryEvent.libraryEventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
        }

        if (!libraryEvent.libraryEventType().equals(LibraryEventType.UPDATE)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only UPDATE event type is supported");
        }
        return null;
    }


}
