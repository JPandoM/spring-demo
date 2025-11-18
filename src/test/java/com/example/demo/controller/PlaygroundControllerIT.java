package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "PLAYGROUND")
class PlaygroundControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sayHelloWorld() throws Exception {
        mockMvc.perform(get("/playground/sayHelloWorld"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }

    @Test
    void saySomething_withParam() throws Exception {
        mockMvc.perform(get("/playground/saySomething").param("something", "CustomMessage"))
                .andExpect(status().isOk())
                .andExpect(content().string("Saying: CustomMessage"));
    }

    @Test
    void saySomething_withoutParam() throws Exception {
        mockMvc.perform(get("/playground/saySomething"))
                .andExpect(status().isOk())
                .andExpect(content().string("Saying: something"));
    }

    @Test
    void getConfigurationSentences() throws Exception {
        String expectedResponse = """
                This is a default configuration value retrieved from application.yml instead of .properties using a configuration helper.
                ConfigurationHelper.java uses @Component and @ConfigurationProperties to do so.
                Component tag lets Spring know is needed in app context, enables dependency injection and removes boilerplate config.
                ConfigurationProperties tag binds the properties with the prefix "default.configuration" to the fields in ConfigurationHelper.java.""";
        
        mockMvc.perform(get("/playground/getConfigurationSentences"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void postWithRequestBody() throws Exception {
        String requestBody = "test body";
        mockMvc.perform(post("/playground/postWithRequestBody")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("You've sent this body: " + requestBody));
    }
}
