package com.jms.config;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import com.tibco.tibjms.TibjmsQueueConnectionFactory;

import lombok.AllArgsConstructor;

@Configuration
@EnableJms
@AllArgsConstructor
public class BeanConfig {

	private Environment env;

	@Bean
	public ConnectionFactory connectionFactory() {
		TibjmsQueueConnectionFactory connectionFactory = new TibjmsQueueConnectionFactory(
				env.getProperty("spring.activemq.broker-url"));

		connectionFactory.setUserName(env.getProperty("spring.activemq.user"));
		connectionFactory.setSSLPassword(env.getProperty("spring.activemq.password"));

		connectionFactory.setConnAttemptCount(3);
		connectionFactory.setConnAttemptDelay(3000);
		connectionFactory.setConnAttemptTimeout(2000);

		connectionFactory.setReconnAttemptCount(86400);
		connectionFactory.setReconnAttemptDelay(5000);
		connectionFactory.setReconnAttemptTimeout(2000);

		return connectionFactory;
	}

	@Bean
	public DefaultJmsListenerContainerFactory listenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

		factory.setConnectionFactory(connectionFactory());

		return factory;
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();

		jmsTemplate.setDefaultDestinationName(env.getProperty("ems.send.queue"));
		jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		jmsTemplate.setConnectionFactory(connectionFactory());

		return jmsTemplate;
	}

	@Bean
	public SimpleMessageConverter simpleMessageConverter() {
		return new SimpleMessageConverter();
	}

}