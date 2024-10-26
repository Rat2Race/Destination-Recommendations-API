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

	@PostMapping("/generate")
	public ResponseEntity<?> generateCalendar(@RequestBody ClientInfoRequest request) {
		try {
			String itinerary = travelPlanService.generateTravelPlan(request);
			return ResponseEntity.ok(itinerary);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("일정 생성 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@PostMapping("/judgment")
	public ResponseEntity<?> judgmentRequest(@RequestBody String request) {
		return ResponseEntity.status(HttpStatus.OK).body(travelPlanService.isTravelRelated(request));
	}

}
