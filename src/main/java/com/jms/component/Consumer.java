package com.jms.component;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

	@JmsListener(destination = "Q.JMS", containerFactory = "jmsFactory")
	public void receiver(String msg) {
		System.out.println(msg);
	}

}