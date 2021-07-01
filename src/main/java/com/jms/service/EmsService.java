package com.jms.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.jms.component.JmsConsumer;
import com.jms.component.JmsProducer;
import com.jms.component.MsgConverter;

@Service
public class EmsService {

	public JmsProducer producer;
	public JmsConsumer consumer;
	public JmsTemplate jmsTemplate;
	private MsgConverter converter;

	public EmsService(JmsProducer producer, JmsConsumer consumer, JmsTemplate jmsTemplate, MsgConverter converter) {
		this.producer = producer;
		this.consumer = consumer;
		this.jmsTemplate = jmsTemplate;
		this.converter = converter;

		init();
	}

	public void init() {
		jmsTemplate.setMessageConverter(converter);
	}

}