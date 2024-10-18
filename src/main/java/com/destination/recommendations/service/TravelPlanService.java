package com.destination.recommendations.service;

import com.destination.recommendations.dto.ClientInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TravelPlanService {

	private final WebClient webClient;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private String currentItinerary;

	public boolean isTravelRelated(String input) {
		try {
			String response = sendRequestToGpt(
					List.of(
							Map.of("role", "system", "content",
									"You are a friendly and polite assistant that only answers travel-related questions. "
											+ "Travel-related questions must be about: "
											+ "1) Specific travel destinations (e.g., countries, cities, or tourist spots), "
											+ "2) Transportation options (e.g., flights, trains, buses), "
											+ "3) Accommodation and lodging (e.g., hotels, hostels), "
											+ "4) Cultural experiences (e.g., local foods, festivals, landmarks). "
											+ "Do not answer questions related to general shopping or household concerns unless they involve local markets at travel destinations. "
											+ "If the question is not related to travel, respond with: 'false'. "
											+ "If the question is related to travel, respond with: 'true'. "
							), Map.of("role", "user", "content", input)
					)
			);

			JsonNode jsonResponse = objectMapper.readTree(response);
			String gptResponse = jsonResponse.get("choices").get(0).get("message").get("content").asText();

			// 'true' 또는 'false' 응답을 그대로 반환
			return Boolean.parseBoolean(gptResponse.trim());

		} catch (Exception e) {
			return false; // 오류 발생 시 false 반환
		}
	}


	// 여행 일정 생성
	public String generateTravelPlan(ClientInfoRequest request) {
		String recommendationPrompt = "Recommend a suitable travel destination for a "
				+ request.age() + "-year-old " + request.gender()
				+ " with a budget of " + request.budget() + " KRW. "
				+ "Recommend a destination that fits the given profile.";

		String recommendedDestination = sendRequestToGpt(
				List.of(
						Map.of("role", "system", "content", "You are a travel assistant that recommends destinations."),
						Map.of("role", "user", "content", recommendationPrompt)
				)
		);

		String travelPrompt = "Create a detailed travel itinerary for a trip to "
				+ recommendedDestination + " from " + request.startDate()
				+ " to " + request.endDate() + ". The total budget is " + request.budget() + " KRW. "
				+ "Include details about flights, accommodation, meals, transportation, and sightseeing for each day."
				+ "Plan your trip with the information you enter.Don't ask questions, just make a plan. If the user doesn't enter a currency unit, calculate it in Korean won, e.g., if they write 78, it's 780,000 won. Make a plan from day 1. Specify how much the flight will cost, how you've allocated your budget, where you're going to stay, what restaurants you're going to eat, what hotels you're going to stay at, and how much you're going to spend on transportation."
				+ "The response must be in Korean.";

		String response = sendRequestToGpt(
				List.of(
						Map.of("role", "system", "content", "You are a travel assistant that generates detailed travel itineraries."),
						Map.of("role", "user", "content", travelPrompt)
				)
		);

		currentItinerary = response;
		return currentItinerary;
	}

	// 일정 수정 요청 처리
	public String modifyItinerary(String modificationRequest) {
		String modifyPrompt = "Here is the current itinerary: " + currentItinerary
				+ ". Please modify it as follows: " + modificationRequest;

		String modifiedItinerary = sendRequestToGpt(
				List.of(
						Map.of("role", "system", "content", "You are a travel assistant that modifies travel itineraries."),
						Map.of("role", "user", "content", modifyPrompt)
				)
		);

		currentItinerary = modifiedItinerary; // 수정된 일정을 저장
		return currentItinerary; // 수정된 일정을 반환
	}

	private String sendRequestToGpt(List<Map<String, String>> messages) {
		try {

			Map<String, Object> requestBody = Map.of(
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
