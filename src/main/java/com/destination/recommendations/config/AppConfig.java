package com.destination.recommendations.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class AppConfig {

	@Value("${OPEN_AI_KEY}")
	private String openaiApiKey;

	@Value("${OPEN_AI_URI}")
	private String apiUrl;

	@Bean
	public WebClient webClient(WebClient.Builder webClientBuilder) {
		return webClientBuilder
				.baseUrl(apiUrl)
				.defaultHeader("Authorization", "Bearer " + openaiApiKey)
				.defaultHeader("Content-Type", "application/json")
				.build();
	}

}
