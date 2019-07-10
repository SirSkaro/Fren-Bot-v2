package skaro.frenbot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import skaro.frenbot.receivers.PingReceiver;
import skaro.frenbot.receivers.PointAwardReceiver;
import skaro.frenbot.receivers.SilentPointAwardReceiver;

@Configuration
public class ReceiverConfig {

	@Bean
	public PingReceiver getPingReceiver() {
		return new PingReceiver();
	}
	
	@Bean
	public SilentPointAwardReceiver getSilentPointAwardReceiver() {
		return new SilentPointAwardReceiver();
	}
	
	@Bean
	public PointAwardReceiver getPointAwardReceiver() {
		return new PointAwardReceiver();
	}
	
}
