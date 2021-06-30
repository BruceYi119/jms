package com.jms.config;

import javax.jms.ConnectionFactory;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@Configuration
public class BeanConfig {

	@Bean
	public JmsListenerContainerFactory<?> jmsFactory(ConnectionFactory connetFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

		configurer.configure(factory, connetFactory);

		return factory;
	}

//	@Bean
//	public MessageConverter jjmcMessageConverter() {
//		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//		
//	}

}