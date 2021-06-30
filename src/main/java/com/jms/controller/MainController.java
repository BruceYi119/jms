package com.jms.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MainController {

	private JmsTemplate jsmtTemplate;

	@GetMapping("/test")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test() {
		Map<String, String> json = new HashMap();

		jsmtTemplate.convertAndSend("##############");

		json.put("result", "success");

		return json;
	}

}