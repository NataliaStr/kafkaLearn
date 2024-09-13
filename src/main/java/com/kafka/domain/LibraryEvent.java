package com.kafka.domain;

public record LibraryEvent(
        Integer libraryId,
        LibraryEventType type,

        Book book
) {
}
