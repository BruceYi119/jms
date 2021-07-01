package com.jms.component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class MsgConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		System.out.println("toMessage");

		TextMessage msg = session.createTextMessage((String) object);

		return msg;
	}

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		System.out.println("fromMessage");

		TextMessage msg = (TextMessage) message;

		return msg.getText();
	}

}