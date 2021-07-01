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
	private JmsTemplate jmsTemplate;

	@GetMapping("/send/{dest}/{msg}")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test(@PathVariable("dest") String dest, @PathVariable("msg") String msg) {
		Map<String, String> json = new HashMap();

		EmsService.producer.send(dest, msg);

		json.put("result", "success");

		return json;
	}

	@GetMapping("/recv/{dest}")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test1(@PathVariable("dest") String dest) {
		Map<String, String> json = new HashMap();

		String msg = (String) jmsTemplate.receiveAndConvert(dest);

		System.out.println(msg);

		json.put("result", "success");

		return json;
	}

	@GetMapping("/sendb/{dest}/{msg}")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test2(@PathVariable("dest") String dest, @PathVariable("msg") String msg) {
		Map<String, String> json = new HashMap();

		jmsTemplate.convertAndSend(dest, msg.getBytes());

		json.put("result", "success");

		return json;
	}

	@GetMapping("/recvb/{dest}")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test3(@PathVariable("dest") String dest) {
		Map<String, String> json = new HashMap();

		byte[] msg = (byte[]) jmsTemplate.receiveAndConvert(dest);

		System.out.println(msg.length);
		System.out.println(new String(msg));

		json.put("result", "success");

		return json;
	}

	@GetMapping("/sendm/{dest}/{msg}")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test4(@PathVariable("dest") String dest, @PathVariable("msg") String msg) {
		Map<String, String> json = new HashMap();

		json.put("result", "success");
		json.put("msg", msg);

		jmsTemplate.convertAndSend(dest, json);

		return json;
	}

	@GetMapping("/recvm/{dest}")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object test5(@PathVariable("dest") String dest) {
		Map<String, String> json = new HashMap();

		Map<String, Object> msg = (Map<String, Object>) jmsTemplate.receiveAndConvert(dest);

		System.out.println(msg);

		json.put("result", "success");

		return json;
	}

}