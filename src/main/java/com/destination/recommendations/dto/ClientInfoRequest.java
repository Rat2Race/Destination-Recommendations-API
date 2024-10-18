package com.destination.recommendations.dto;

public record ClientInfoRequest(
	String age,
	String gender,
	String budget,
	String startDate,
	String endDate
) {
}
