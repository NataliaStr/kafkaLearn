package com.kafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.domain.LibraryEvent;
import com.kafka.producer.LibraryEventsProducer;
import com.kafka.util.TestUtil;
import com.kafka.util.TestUtilUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LibraryEventsControllerUnitTest.class)
public class LibraryEventsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent() throws Exception {
     //given
        String json = objectMapper.writeValueAsString(TestUtilUnit.libraryEventRecord());

        //when
        when(libraryEventsProducer.sendLibraryEventAsyncProducerRecord(isA(LibraryEvent.class)))
                .thenReturn(null);

        mockMvc.perform(post("/v1/libraryevents")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //then

    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(TestUtil.libraryEventRecordWithInvalidBook());

        //when
        when(libraryEventsProducer.sendLibraryEventAsyncProducerRecord(isA(LibraryEvent.class)))
                .thenReturn(null);

        // expectedErrorMsg = "book.bookId - must not be null, book.bookAuthor - must not be empty, book.bookName - must not be empty"

        mockMvc.perform(post("/v1/libraryevents")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());  //This we expect to be failing the test
//        .andExpect(status().is4xxClientError);  //This we expect to be passing the test
//        .andExpect(content().string(expectedErrorMsg));  //This we expect to be passing the test
        //then

    }
}
