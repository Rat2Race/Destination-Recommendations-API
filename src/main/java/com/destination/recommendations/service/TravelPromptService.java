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

	// GPT 모델에게 질문을 보내고, 여행 여부와 실제 답변을 받아오는 메서드
	public Map<String, String> getGptAnswerWithTravelCheck(String input) {
		Map<String, String> result = new HashMap<>();

		// 1단계: 여행 여부 체크 및 이유 포함 요청
		String travelCheck = checkIfTravelRelated(input);
		result.put("check", travelCheck);

		// 2단계: 여행과 관련 있다면 실제 답변 요청
		if (travelCheck.contains("Yes")) {
			String gptAnswer = getActualAnswer(input);
			result.put("answer", gptAnswer);
		} else {
			result.put("answer", "여행과 관련된 질문이 아닙니다.");
		}

		return result;
	}

	// 1단계: 여행 여부 확인 및 이유 요청 (이유 설명 추가)
	private String checkIfTravelRelated(String input) {
		try {
			// API 요청을 공통 메서드로 호출 (여행 여부와 이유를 포함한 답변 요청)
			String response = sendRequestToGpt(
					List.of(
							Map.of("role", "system", "content", "If the message is related to travel, say 'Yes, this is related to travel' and explain why. "
									+ "If not, say 'No, this is not related to travel' and explain why. Always answer in Korean."),
							Map.of("role", "user", "content", input)
					)
			);

			// 응답에서 여행 여부와 이유 추출
			JsonNode jsonResponse = objectMapper.readTree(response);
			String travelCheckWithReason = jsonResponse.get("choices").get(0).get("message").get("content").asText();

			return travelCheckWithReason;

		} catch (Exception e) {
			return "오류 발생: 여행 여부 확인 중 문제가 발생했습니다.";
		}
	}

	// 2단계: 실제 질문에 대한 답변 생성 (한국어로 출력)
	private String getActualAnswer(String input) {
		try {
			// API 요청을 공통 메서드로 호출 (한국어로 답변 요청)
			String response = sendRequestToGpt(
					List.of(
							Map.of("role", "system", "content", "Please answer the user's question in Korean."),
							Map.of("role", "user", "content", input)
					)
			);

			// 응답에서 실제 답변 추출
			JsonNode jsonResponse = objectMapper.readTree(response);
			return jsonResponse.get("choices").get(0).get("message").get("content").asText();

		} catch (Exception e) {
			return "오류 발생: 답변 생성 중 문제가 발생했습니다.";
		}
	}

	// 공통 API 요청 메서드
	private String sendRequestToGpt(List<Map<String, String>> messages) {
		try {
			// 요청 본문 작성
			var requestBody = Map.of(
					"model", "ft:gpt-3.5-turbo-1106:rat2race::AHWr2HdI",  // 모델 ID
					"messages", messages
			);

			// WebClient를 사용해 OpenAI API 호출
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
