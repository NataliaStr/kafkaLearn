package com.kafka.domain;

public record Book(
        Integer bookId,
        String bookName,
        String bookAuthor
) {
}
