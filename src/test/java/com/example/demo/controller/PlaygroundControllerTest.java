package com.example.demo.controller;

import com.example.demo.utils.ConfigurationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaygroundControllerTest {

    @Mock
    private ConfigurationHelper configurationHelper;

    @InjectMocks
    private PlaygroundController playgroundController;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(playgroundController, "defaultWord", "default");
    }

    @Test
    void sayHelloWorld() {
        assertEquals("Hello World!", playgroundController.sayHelloWorld());
    }

    @Test
    void saySomething_withParam() {
        assertEquals("Saying: test", playgroundController.saySomething("test"));
    }

    @Test
    void saySomething_withoutParam() {
        assertEquals("Saying: default", playgroundController.saySomething(null));
    }
    
    @Test
    void saySomething_withEmptyParam() {
        assertEquals("Saying: default", playgroundController.saySomething(""));
    }

    @Test
    void getConfigurationSentences() {
        when(configurationHelper.getFirst()).thenReturn("first");
        when(configurationHelper.getSecond()).thenReturn("second");
        when(configurationHelper.getThird()).thenReturn("third");
        when(configurationHelper.getFourth()).thenReturn("fourth");

        String expected = "first\nsecond\nthird\nfourth";
        assertEquals(expected, playgroundController.getConfigurationSentences());
    }

    @Test
    void postWithRequestBody() {
        assertEquals("You've sent this body: test body", playgroundController.postWithRequestBody("test body"));
    }
}