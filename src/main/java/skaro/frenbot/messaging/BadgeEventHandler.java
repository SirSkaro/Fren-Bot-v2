package skaro.frenbot.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;

@Profile("pub-sub")
@RabbitListener(queues = "#{@messagingProperties.badges}")
public class BadgeEventHandler {

	@RabbitHandler
	public void handleBadgeEvent(BadgeEventMessage event) {
		System.out.println("Got an event " + event.getEventType());
	}
	
}
