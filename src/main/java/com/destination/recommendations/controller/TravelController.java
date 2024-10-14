package com.destination.recommendations.controller;

import com.destination.recommendations.service.TravelPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/travel")
public class TravelController {

	private final TravelPromptService travelPromptService;

	@Autowired
	public TravelController(TravelPromptService travelPromptService) {
		this.travelPromptService = travelPromptService;
	}

	// 클라이언트가 보낸 질문에 대한 GPT의 답변을 반환하는 메서드
	@PostMapping("/check")
	public Map<String, String> checkTravelRelated(@RequestBody Map<String, String> request) {
		// 클라이언트가 보낸 "content" 키의 값을 추출하여 GPT 서비스에 전달
		String input = request.get("content");

		// 여행 여부와 GPT 답변을 반환하는 Map을 받아서 클라이언트에게 반환
		return travelPromptService.getGptAnswer(input);
	}
}
