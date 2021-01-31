package skaro.frenbot.messaging;

import static skaro.pokeaimpi.sdk.config.PokeAimPiSdkMessagingConfig.BADGE_FANOUT_BEAN;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import skaro.frenbot.receivers.services.DiscordService;
import skaro.pokeaimpi.sdk.config.PokeAimPiSdkMessagingConfig;

@Configuration
@Profile(MessagingConfig.MESSAGING_PROFILE)
@Import(PokeAimPiSdkMessagingConfig.class)
public class MessagingConfig {
	public static final String MESSAGING_PROFILE = "pub-sub";
	public static final String BADGE_QUEUE_BEAN = "badgeQueue";
	private static final String BADGE_BINDING_BEAN = "badgeBinding";
	
	@Bean(BADGE_QUEUE_BEAN)
	@Autowired
	public Queue getBadgesQueue() {
		return new AnonymousQueue();
	}
	
	@Bean(BADGE_BINDING_BEAN)
	@Autowired
	public Binding getBadgeBinding(@Qualifier(BADGE_FANOUT_BEAN) FanoutExchange exchange, 
			@Qualifier(BADGE_QUEUE_BEAN) Queue queue) {
		return BindingBuilder.bind(queue).to(exchange);
	}
	
	@Bean
	@Autowired
	public BadgeEventHandler getBadgeEventHandler(DiscordService discordService) {
		return new BadgeEventHandler(discordService);
	}
	
}
