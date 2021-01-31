package skaro.frenbot.messaging;

import java.lang.invoke.MethodHandles;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import skaro.frenbot.receivers.services.DiscordService;
import skaro.pokeaimpi.sdk.messaging.BadgeEventMessage;
import skaro.pokeaimpi.sdk.messaging.BadgeEventType;
import skaro.pokeaimpi.sdk.resource.Badge;

public class BadgeEventHandler {
	private static final Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

	private DiscordService discordService;
	private Map<BadgeEventType, Consumer<Badge>> eventMap;
	
	public BadgeEventHandler(DiscordService discordService) {
		this.discordService = discordService;
		eventMap = new EnumMap<BadgeEventType, Consumer<Badge>>(BadgeEventType.class);
		eventMap.put(BadgeEventType.DELETE, (badge) -> handleDelete(badge));
	}
	
	@RabbitListener(queues = "#{@"+MessagingConfig.BADGE_QUEUE_BEAN+".getName()}")
	public void handleBadgeEvent(BadgeEventMessage event) {
		LOG.info("Received badge event {} for badge {}", event.getEventType(), event.getBadge().getTitle());
		getEventCallback(event.getEventType())
			.ifPresent(callback -> callback.accept(event.getBadge()));
	}
	
	private Optional<Consumer<Badge>> getEventCallback(BadgeEventType type) {
		return Optional.ofNullable(eventMap.get(type));
	}
	
	private void handleDelete(Badge badge) {
		discordService.deleteRole(badge.getDiscordRoleId())
			.subscribe();
	}
	
}
