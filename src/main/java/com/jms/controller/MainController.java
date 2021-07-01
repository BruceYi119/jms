package com.jms.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jms.service.EmsService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MainController {

	private EmsService EmsService;
	private JmsTemplate JmsTemplate;

	@GetMapping("/send/{desc}/{msg}")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test2(@PathVariable("desc") String desc, @PathVariable("msg") String msg) {
		Map<String, String> json = new HashMap();

		EmsService.producer.send(desc, msg);

		json.put("result", "success");

		return json;
	}

	@GetMapping("/recv/{dest}")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test3(@PathVariable("dest") String desc) {
		Map<String, String> json = new HashMap();

		String msg = (String) JmsTemplate.receiveAndConvert(desc);

		System.out.println(msg);

		json.put("result", "success");

		return json;
	}

}