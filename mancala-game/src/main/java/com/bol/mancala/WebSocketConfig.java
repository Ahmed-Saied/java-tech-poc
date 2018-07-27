package com.bol.mancala;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author Ahmed
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// topic and queue are non sense with stomp,
		// it might come handy with rabbitmq, kafka and other message brokers
		config.enableSimpleBroker("/topic/", "/queue/");
		config.setApplicationDestinationPrefixes("/game");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// the SockJS connection establishment endpoint path
		registry.addEndpoint("/play").withSockJS();
	}

}