package com.jms.ems;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmsService implements ApplicationListener<ApplicationStartedEvent> {

	public JmsProducer producer;
	public JmsConsumer consumer;
	public JmsTemplate jmsTemplate;
	private MsgConverter converter;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		jmsTemplate.setMessageConverter(converter);
	}

}