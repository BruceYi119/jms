package com.jms.component;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsConsumer {

	@JmsListener(destination = "Q.JMS1")
	public void consumer1(String msg) {
		System.out.println(msg);
	}

	@JmsListener(destination = "Q.JMS2")
	public void consumer2(String msg) {
		System.out.println(msg);
	}

}