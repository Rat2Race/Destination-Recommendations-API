package com.destination.recommendations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TravelPromptService {

	private final WebClient webClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public Map<String, String> getGptAnswer(String input) {
		Map<String, String> result = new HashMap<>();

		try {
			String response = sendRequestToGpt(
					List.of(
							Map.of("role", "system", "content",
									"You are an assistant that only answers questions strictly related to travel. Travel-related questions are those about:"
											+ " 1) Specific travel destinations (e.g., countries, cities, or tourist spots),"
											+ " 2) Transportation options (e.g., flights, trains, buses, etc.),"
											+ " 3) Accommodation and lodging (e.g., hotels, hostels, etc.),"
											+ " 4) Cultural experiences (e.g., local foods, festivals, landmarks)."
											+ " If the question is travel-related, answer the user's question in detail and in Korean."
											+ " If the user's question does not belong to one of these categories, respond with '잘못된 입력입니다.' without providing further explanation or follow-up questions."),
							Map.of("role", "user", "content", input)
					)

			);

			JsonNode jsonResponse = objectMapper.readTree(response);
			String gptResponse = jsonResponse.get("choices").get(0).get("message").get("content").asText();

			result.put("answer", gptResponse);

		} catch (Exception e) {
			result.put("answer", "오류 발생: 답변 생성 중 문제가 발생했습니다.");
		}

		return result;
	}

	private String sendRequestToGpt(List<Map<String, String>> messages) {
		try {
			var requestBody = Map.of(
					"model", "ft:gpt-3.5-turbo-1106:rat2race::AHWr2HdI",
					"messages", messages
			);

			return webClient.post()
					.bodyValue(requestBody)
					.retrieve()
					.bodyToMono(String.class)
					.block();
		} catch (Exception e) {
			return "오류 발생: API 호출 중 문제가 발생했습니다.";
		}
	}
}
