package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/playground")
public class PlaygroundController {
		
	@GetMapping("/sayHelloWorld")
	public String sayHelloWorld() {
		return "Hello World!";
	}
	
	@GetMapping("/saySomething")
	public String saySomething(@RequestParam(defaultValue = "Hello World! By default!") String sentence) {
		return sentence;
	}
	
	@PostMapping("/postWithRequestBody")
	public String postWithRequestBody(@RequestBody String dataBlob) {
		return "You've sent this body: " + dataBlob;
	}
	
}