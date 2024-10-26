package com.destination.recommendations.controller;

import com.destination.recommendations.dto.ClientInfoRequest;
import com.destination.recommendations.service.TravelPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {

	private final TravelPlanService travelPlanService;

	@PostMapping("/check")
	public ResponseEntity<Boolean> isTravelRelated(@RequestBody String input) {
		boolean isRelated = travelPlanService.isTravelRelated(input);
		return ResponseEntity.ok(isRelated);
	}

	@PostMapping("/generate")
	public ResponseEntity<String> generateTravelPlan(@RequestBody ClientInfoRequest request) {
		String itinerary = travelPlanService.generateTravelPlan(request);
		return ResponseEntity.ok(itinerary);
	}

	@PostMapping("/modify")
	public ResponseEntity<String> modifyItinerary(@RequestBody String modificationRequest) {
		String modifiedItinerary = travelPlanService.modifyItinerary(modificationRequest);
		return ResponseEntity.ok(modifiedItinerary);
	}

}
