package com.destination.recommendations.controller;

import com.destination.recommendations.dto.ClientInfoRequest;
import com.destination.recommendations.service.TravelPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travel")
public class TravelController {

	private final TravelPlanService travelPlanService;

	@Autowired
	public TravelController(TravelPlanService travelPromptService) {
		this.travelPlanService = travelPromptService;
	}

	@PostMapping("/generate")
	public ResponseEntity<?> generateCalendar(@RequestBody ClientInfoRequest request) {
		try {
			// ClientInfoRequest 정보를 기반으로 일정을 생성
			String itinerary = travelPlanService.generateTravelPlan(request);

			// 생성된 일정을 반환
			return ResponseEntity.ok(itinerary);

		} catch (Exception e) {
			// 오류 발생 시 500 에러와 메시지 반환
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("일정 생성 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

//	@PatchMapping("/update")
//	public ResponseEntity<?> updateCalendar(String input) {
//
//	}
}
