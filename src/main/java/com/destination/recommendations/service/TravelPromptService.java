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
									"You are a friendly and polite assistant that only answers travel-related questions. Travel-related questions must be about: "
											+ "1) Specific travel destinations (e.g., countries, cities, or tourist spots), "
											+ "2) Transportation options (e.g., flights, trains, buses), "
											+ "3) Accommodation and lodging (e.g., hotels, hostels), "
											+ "4) Cultural experiences (e.g., local foods, festivals, landmarks). "
											+ "Do not answer questions related to general shopping or household concerns unless they involve local markets at travel destinations. "
											+ "If the question is related to travel, provide a detailed and polite answer in Korean. "
											+ "If the question is not related to travel, kindly respond with: '이 질문은 여행과 관련이 없는 것 같습니다. 여행과 관련된 질문을 해 주세요. 궁금한 것이 있으면 언제든 물어보세요!' "
											+ "Always respond in Korean, regardless of the user's language."
							), Map.of("role", "user", "content", input)
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
