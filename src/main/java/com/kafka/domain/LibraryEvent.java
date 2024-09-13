package com.kafka.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LibraryEvent(
        Integer libraryId,
        LibraryEventType type,

        @NotNull
        @Valid
        Book book
) {
}
