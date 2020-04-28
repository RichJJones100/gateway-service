package com.gembaadvantage.apollo.gatewayservice.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class FallbackResponse {
	private Integer msgCode;
	private String msg;
}
