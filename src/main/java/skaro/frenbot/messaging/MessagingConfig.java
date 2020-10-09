package skaro.frenbot.messaging;

import javax.validation.Valid;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("pub-sub")
public class MessagingConfig {
	
	@Bean("messagingProperties")
	@ConfigurationProperties("skaro.pokeaimpi.messaging.queue")
	@Valid
	public MessagingProperties getMessagingProperties() {
		return new MessagingProperties();
	}
	
	@Bean
	@Autowired
	public Queue getBadgesQueue(MessagingProperties properties) {
		return new Queue(properties.getBadges());
	}
	
	@Bean
	public BadgeEventHandler getBadgeEventHandler() {
		return new BadgeEventHandler();
	}
	
}
