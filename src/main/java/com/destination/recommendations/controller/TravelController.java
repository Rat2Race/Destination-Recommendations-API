package com.destination.recommendations.controller;

import com.destination.recommendations.service.TravelPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/travel")
public class TravelController {

	private final TravelPromptService travelPromptService;

	@Autowired
	public TravelController(TravelPromptService travelPromptService) {
		this.travelPromptService = travelPromptService;
	}

	@PostMapping("/answer")
	public Map<String, String> checkTravelRelated(@RequestBody Map<String, String> request) {
		String input = request.get("content");

		return travelPromptService.getGptAnswer(input);
	}
}
