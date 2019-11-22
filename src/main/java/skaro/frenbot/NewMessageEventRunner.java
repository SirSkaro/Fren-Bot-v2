package skaro.frenbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.PrivateChannel;
import reactor.core.publisher.Mono;
import skaro.frenbot.invokers.MessageCreateInvoker;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class NewMessageEventRunner implements CommandLineRunner {

	@Autowired
	private DiscordClient discordClient;
	@Autowired
	private MessageCreateInvoker invoker;
	
	@Override
	public void run(String... args) throws Exception {
		discordClient.getEventDispatcher().on(MessageCreateEvent.class)
			.map(event -> event.getMessage())
			.filter(message -> !authorIsBot(message))
			.filterWhen(message -> isFromPublicChannel(message))
			.flatMap(message -> invoker.respond(message))
			.onErrorResume(throwable -> Mono.empty())
			.subscribe(event -> System.out.println("event handled"));
	}
	
	private boolean authorIsBot(Message message) {
		return message.getAuthor()
				.map(user -> user.isBot())
				.orElse(true);
	}
	
	private Mono<Boolean> isFromPublicChannel(Message message) {
		return message.getChannel()
				.map(channel -> !(channel instanceof PrivateChannel));
	}

}
