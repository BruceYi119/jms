package com.jms.component;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsConsumer {

	@JmsListener(destination = "Q.JMS1", containerFactory = "listenerContainerFactory")
	public void consumer1(Object msg) {
		System.out.println(msg);
	}

	@JmsListener(destination = "Q.JMS2", containerFactory = "listenerContainerFactory")
	public void consumer2(Object msg) {
		System.out.println(msg);
	}

	@JmsListener(destination = "Q.JMS3", containerFactory = "listenerContainerFactory")
	public void consumer3(Object msg) {
		System.out.println(msg);
	}

}