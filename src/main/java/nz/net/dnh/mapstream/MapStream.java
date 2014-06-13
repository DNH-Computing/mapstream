package nz.net.dnh.mapstream;

import static java.util.function.Function.identity;
import static nz.net.dnh.mapstream.MapStreamHelpers.entryConsumer;
import static nz.net.dnh.mapstream.MapStreamHelpers.entryFunction;
import static nz.net.dnh.mapstream.MapStreamHelpers.entryPredicate;
import static nz.net.dnh.mapstream.MapStreamHelpers.mappedPredicate;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public interface MapStream<K, V> {
	public static <K, V> MapStream<K, V> of(final Map<K, V> map) {
		return map.entrySet()::stream;
	}

	Stream<Entry<K, V>> entryStream();

	default Stream<K> keyStream() {
		return entryStream().map(Entry::getKey);
	}

	default Stream<V> valueStream() {
		return entryStream().map(Entry::getValue);
	}

	default MapStream<K, V> filter(final BiPredicate<? super K, ? super V> predicate) {
		return () -> entryStream().filter(entryPredicate(predicate));
	}

	default MapStream<K, V> filterKeys(final Predicate<? super K> predicate) {
		return () -> entryStream().filter(mappedPredicate(Entry::getKey, predicate));
	}

	default MapStream<K, V> filterValues(final Predicate<? super V> predicate) {
		return () -> entryStream().filter(mappedPredicate(Entry::getValue, predicate));
	}

	default <R> Stream<R> map(final BiFunction<? super K, ? super V, ? extends R> mapper) {
		return entryStream().map(entryFunction(mapper));
	}

	default <K2> MapStream<K2, V> mapKeys(final Function<? super K, ? extends K2> mapper) {
		return map(mapper, identity());
	}

	default <K2> MapStream<K2, V> mapKeys(final BiFunction<? super K, ? super V, ? extends K2> mapper) {
		return map(mapper, (k, v) -> v);
	}

	default <V2> MapStream<K, V2> mapValues(final Function<? super V, ? extends V2> mapper) {
		return map(identity(), mapper);
	}

	default <V2> MapStream<K, V2> mapValues(final BiFunction<? super K, ? super V, ? extends V2> mapper) {
		return map((k, v) -> k, mapper);
	}

	// TODO test below this line
	default <K2, V2> MapStream<K2, V2> map(final Function<? super K, ? extends K2> keyMapper,
			final Function<? super V, ? extends V2> valueMapper) {
		return map((k, v) -> keyMapper.apply(k), (k, v) -> valueMapper.apply(v));
	}

	default <K2, V2> MapStream<K2, V2> map(final BiFunction<? super K, ? super V, ? extends K2> keyMapper,
			final BiFunction<? super K, ? super V, ? extends V2> valueMapper) {
		return () -> entryStream().map(
				e -> new SimpleEntry<>(keyMapper.apply(e.getKey(), e.getValue()), valueMapper.apply(e.getKey(), e.getValue())));
	}

	default MapStream<K, V> distinct() {
		return () -> entryStream().distinct();
	}

	default MapStream<K, V> distinctKeys() {
		// TODO do I care?
		return null;
	}

	default MapStream<K, V> distinctValues() {
		// TODO do I care?
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	default MapStream<K, V> sortedKeys() {
		return sortedKeys((Comparator<? super K>) Comparator.naturalOrder());
	}

	default MapStream<K, V> sortedKeys(final Comparator<? super K> comparator) {
		return () -> entryStream().sorted(Comparator.comparing(Entry::getKey, comparator));
	}

	@SuppressWarnings("unchecked")
	default MapStream<K, V> sortedValues() {
		return sortedValues((Comparator<? super V>) Comparator.naturalOrder());
	}

	default MapStream<K, V> sortedValues(final Comparator<? super V> comparator) {
		return () -> entryStream().sorted(Comparator.comparing(Entry::getValue, comparator));
	}

	default MapStream<K, V> peek(final BiConsumer<? super K, ? super V> action) {
		return () -> entryStream().peek(entryConsumer(action));
	}

	default MapStream<K, V> peekKeys(final Consumer<? super K> action) {
		return () -> entryStream().peek(e -> action.accept(e.getKey()));
	}

	default MapStream<K, V> peekValues(final Consumer<? super V> action) {
		return () -> entryStream().peek(e -> action.accept(e.getValue()));
	}

	default MapStream<K, V> limit(final long maxSize) {
		return () -> entryStream().limit(maxSize);
	}

	default MapStream<K, V> skip(final long n) {
		return () -> entryStream().skip(n);
	}

	// Terminal operations
	default void forEach(final BiConsumer<? super K, ? super V> action) {
		entryStream().forEach(entryConsumer(action));
	}

	default void forEachOrdered(final BiConsumer<? super K, ? super V> action) {
		entryStream().forEachOrdered(entryConsumer(action));
	}

	default long count() {
		return entryStream().count();
	}

	default boolean anyMatch(final BiPredicate<? super K, ? super V> predicate) {
		return entryStream().anyMatch(entryPredicate(predicate));
	}

	default boolean allMatch(final BiPredicate<? super K, ? super V> predicate) {
		return entryStream().allMatch(entryPredicate(predicate));
	}

	default boolean noneMatch(final BiPredicate<? super K, ? super V> predicate) {
		return entryStream().noneMatch(entryPredicate(predicate));
	}

	// Convenience method for the most common collect operation - for others, call entryStream().collect or entryStream().reduce
	default <R, A> R collect(final Collector<? super Entry<K, V>, A, R> collector) {
		return entryStream().collect(collector);
	}
}
