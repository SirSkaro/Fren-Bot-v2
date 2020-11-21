package skaro.frenbot.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;

import skaro.pokeaimpi.sdk.messaging.BadgeEventMessage;

@Profile(MessagingConfig.MESSAGING_PROFILE)
@RabbitListener(queues = MessagingConfig.BADGE_QUEUE_NAME)
public class BadgeEventHandler {

	@RabbitHandler
	public void handleBadgeEvent(BadgeEventMessage event) {
		System.out.println("Got an event " + event.getEventType());
	}
	
}
