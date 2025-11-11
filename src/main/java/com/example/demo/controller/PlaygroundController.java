package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.utils.ConfigurationHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/playground")
public class PlaygroundController {
	
	private ConfigurationHelper configurationHelper;
	
    public PlaygroundController(ConfigurationHelper configurationHelper) {
        this.configurationHelper = configurationHelper;
    }
	
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
	    return "Saying: " + something;
	}

	@GetMapping("/getConfigurationSentences")
	public String getConfigurationSentences() {
		return configurationHelper.getFirst() + "\n" +
			   configurationHelper.getSecond() + "\n" +
			   configurationHelper.getThird() + "\n" +
			   configurationHelper.getFourth();
	}
	
	@PostMapping("/postWithRequestBody")
	public String postWithRequestBody(@RequestBody String dataBlob) {
		return "You've sent this body: " + dataBlob;
	}
	
}