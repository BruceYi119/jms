package com.jms.component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class MsgConverter implements MessageConverter {

	private SimpleMessageConverter simpleMessageConverter;

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		return simpleMessageConverter.toMessage(object, session);
	}

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		return simpleMessageConverter.fromMessage(message);
	}

}