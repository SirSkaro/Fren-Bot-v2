package skaro.frenbot.receivers.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DTOBuilder<T> {
	private Supplier<T> instantiator;

    private List<Consumer<T>> instanceModifiers = new ArrayList<>();

    public DTOBuilder(Supplier<T> instantiator) {
        this.instantiator = instantiator;
    }

    public static <T> DTOBuilder<T> of(Supplier<T> instantiator) {
        return new DTOBuilder<T>(instantiator);
    }

    public <U> DTOBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> c = instance -> consumer.accept(instance, value);
        instanceModifiers.add(c);
        return this;
    }

    public T build() {
        T value = instantiator.get();
        instanceModifiers.forEach(modifier -> modifier.accept(value));
        instanceModifiers.clear();
        return value;
    }
}
