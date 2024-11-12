package com.destination.recommendations.controller;

import com.destination.recommendations.dto.ClientInfoRequest;
import com.destination.recommendations.dto.ClientRequest;
import com.destination.recommendations.service.TravelPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {

	private final TravelPlanService travelPlanService;

	@Operation(summary = "여행 계획 생성", description = "여행 계획을 생성합니다.")
	@Parameter(name = "request", required = true, description = "클라이언트 정보")
    @PostMapping("/generate")
	public ResponseEntity<String> generateTravelPlan(@RequestBody ClientInfoRequest request) {
		String itinerary = travelPlanService.generateTravelPlan(request);
		return ResponseEntity.ok(itinerary);
	}

	@Operation(summary = "여행 계획 수정", description = "여행 계획을 수정합니다.")
	@Parameter(name = "modificationRequest", required = true, description = "클라이언트의 수정 요청사항")
	@PostMapping("/modify")
	public ResponseEntity<String> modifyItinerary(@RequestBody ClientRequest modificationRequest) {
		String modifiedItinerary = travelPlanService.modifyItinerary(modificationRequest);
		boolean isRelated = travelPlanService.isTravelRelated(modifiedItinerary);

		if(isRelated) {
			return ResponseEntity.ok(modifiedItinerary);
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바르지 않은 요청입니다.");
	}

}
