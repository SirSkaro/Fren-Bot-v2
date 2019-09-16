package skaro.frenbot.receivers.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class RPSServiceImpl implements RPSService {

	private EnumeratedDistribution<RPSOption> optionDistribution;
	
	public RPSServiceImpl() {
		List<Pair<RPSOption, Double>> optionWeightPairs = Arrays.stream(RPSOption.values())
				.map(option -> new Pair<>(option, option.getWeight()))
				.collect(Collectors.toList());
		
		optionDistribution = new EnumeratedDistribution<>(optionWeightPairs);
	}
	
	@Override
	public RPSOption getWeightedOption() {
		return optionDistribution.sample();
	}

}
