package com.jms.config;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

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

		return connectionFactory;
	}

	@Bean
	public JmsListenerContainerFactory<?> jmsFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

		configurer.configure(factory, connectionFactory);

		return factory;
	}

	@Bean
	public CachingConnectionFactory cachingConnectionFactory() {
		return new CachingConnectionFactory(connectionFactory());
	}

	@Bean
	public JmsTemplate jmsTemplate() throws Exception {
		JmsTemplate jmsTemplate = new JmsTemplate();

		jmsTemplate.setDefaultDestinationName(env.getProperty("ems.send.queue"));
		jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		jmsTemplate.setConnectionFactory(connectionFactory());

		return jmsTemplate;
	}

}