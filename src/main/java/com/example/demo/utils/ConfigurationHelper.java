package com.example.demo.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("default.configuration")
public class ConfigurationHelper {

    private String first;
	private String second;
	private String third;
	private String fourth;

}