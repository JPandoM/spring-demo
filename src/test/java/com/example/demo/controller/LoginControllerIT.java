package com.example.demo.controller;

import com.example.demo.security.CustomAuthenticationFailureHandler;
import com.example.demo.service.ApplicationShutdownService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

    @MockBean
    private ApplicationShutdownService shutdownService;

    @BeforeEach
    void setUp() {
        failureHandler.resetAttempts();
    }

    @Test
    void successfulLoginWithPlaygroundUser() throws Exception {
        mockMvc.perform(formLogin("/login").user("playground-user").password("password"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("playground-user").withRoles("PLAYGROUND"));
    }

    @Test
    void successfulLoginWithBookUser() throws Exception {
        mockMvc.perform(formLogin("/login").user("book-user").password("password"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("book-user").withRoles("BOOK"));
    }

    @Test
    @WithMockUser(username = "playground-user", roles = "PLAYGROUND")
    void playgroundUserCanAccessPlaygroundAndNotBooks() throws Exception {
        mockMvc.perform(get("/playground/sayHelloWorld"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "book-user", roles = "BOOK")
    void bookUserCanAccessBooksAndNotPlayground() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/playground/sayHelloWorld"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserIsRedirected() throws Exception {
        mockMvc.perform(get("/playground/sayHelloWorld"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mockMvc.perform(get("/api/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void failedLogin_unauthorized() throws Exception {
        mockMvc.perform(formLogin("/login").user("user").password("wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated());
    }


    @Test
    void failedLoginThreeTimes_triggersShutdown() throws Exception {
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "test-user")
                            .param("password", "wrong"))
                    .andExpect(status().isUnauthorized());
        }

        // 3rd attempt
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "test-user")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized());

        verify(shutdownService, times(1)).shutdown();
    }

    @Test
    void sessionIsMaintainedAfterLogin() throws Exception {
        MvcResult result = mockMvc.perform(formLogin("/login").user("book-user").password("password"))
                .andExpect(status().isOk())
                .andReturn();
        
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();

        Assertions.assertNotNull(session);
        mockMvc.perform(get("/api/books").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void successfulLogout() throws Exception {
        MvcResult result = mockMvc.perform(formLogin("/login").user("book-user").password("password"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();

        Assertions.assertNotNull(session);
        mockMvc.perform(post("/logout").with(csrf()).session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/books").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
