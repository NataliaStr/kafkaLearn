package com.kafka.intg.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void postLibraryEvent() {
        //given
//        new HttpEntity<>(TestUtil.libraryEventRecord());  TODO: to fix

        //when
        restTemplate.postForEntity("/v1/libraryevents", HttpMethod.POST, null);

        //then
        assertThat(restTemplate).isNotNull();
    }
}
