package com.pipeline.demo.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HelloWorldIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHelloEndpoint_Integration() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testHelloPersonalizedEndpoint_Integration() throws Exception {
        mockMvc.perform(get("/api/hello/personalized")
                        .param("name", "IntegrationTest"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Hello, IntegrationTest!"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testHealthEndpoint_Integration() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testActuatorHealthEndpoint_Integration() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String ct = result.getResponse().getContentType();
                    MediaType mediaType = MediaType.parseMediaType(ct);
                    boolean compatible = mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)
                            || mediaType.getSubtype().toLowerCase().endsWith("+json");
                    assertTrue(compatible, "Content-Type not compatible with application/json: " + ct);
                })
                .andExpect(jsonPath("$.status").exists());
    }
}
