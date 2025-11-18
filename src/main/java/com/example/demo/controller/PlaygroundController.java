package com.example.demo.controller;

import com.example.demo.utils.ConfigurationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playground")
@RequiredArgsConstructor
public class PlaygroundController {

    private final ConfigurationHelper configurationHelper;

    @Value("${default.word}")
    private String defaultWord;

    @GetMapping("/sayHelloWorld")
    public String sayHelloWorld() {
        return "Hello World!";
    }

    @GetMapping("/saySomething")
    public String saySomething(@RequestParam(required = false) String something) {
        if (something == null || something.isEmpty()) {
            something = defaultWord; // fallback to injected property
        }
        return String.format("Saying: %s", something);
    }

    @GetMapping("/getConfigurationSentences")
    public String getConfigurationSentences() {
        return String.join("\n",
                configurationHelper.getFirst(),
                configurationHelper.getSecond(),
                configurationHelper.getThird(),
                configurationHelper.getFourth());
    }

    @PostMapping("/postWithRequestBody")
    public String postWithRequestBody(@RequestBody String dataBlob) {
        return String.format("You've sent this body: %s", dataBlob);
    }

}
