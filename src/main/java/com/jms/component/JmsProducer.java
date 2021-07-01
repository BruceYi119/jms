package com.jms.component;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JmsProducer {

	public JmsTemplate jmsTemplate;

	public void send(String msg) {
		jmsTemplate.convertAndSend(msg);
	}

	public void send(String desc, String msg) {
		jmsTemplate.convertAndSend(desc, msg);
	}

}