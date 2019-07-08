package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.function.Consumer;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.BadgeDTO;

public interface DiscordService {

	public Mono<Member> getUserById(String id);
	public Mono<Message> replyToMessage(Message message, Consumer<MessageCreateSpec> reply);
	public Mono<Void> assignBadgeRoles(Member user, List<BadgeDTO> badges);
	public Mono<Role> getRoleForBadge(BadgeDTO badge);
	
}
