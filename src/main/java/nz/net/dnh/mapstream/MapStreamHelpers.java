package nz.net.dnh.mapstream;

import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class MapStreamHelpers {

	public static <K extends Comparable<? super K>, V> Function<Entry<K, V>, K> entryGetComparableKey() {
		return Entry::getKey;
	}

	public static <K, V extends Comparable<? super V>> Function<Entry<K, V>, V> entryGetComparableValue() {
		return Entry::getValue;
	}
	
	public static <K, V> Function<Entry<K, V>, K> entryGetKey() {
		return Entry::getKey;
	}

	public static <K, V> Function<Entry<K, V>, V> entryGetValue() {
		return Entry::getValue;
	}

	public static <T, U> Predicate<T> mappedPredicate(final Function<T, U> mapper, final Predicate<U> predicate) {
		return el -> predicate.test(mapper.apply(el));
	}

	public static <K, V> Predicate<Entry<K, V>> entryPredicate(final BiPredicate<? super K, ? super V> predicate) {
		return e -> predicate.test(e.getKey(), e.getValue());
	}

	public static <K, V, R> Function<Entry<K, V>, ? extends R> entryFunction(final BiFunction<? super K, ? super V, ? extends R> function) {
		return e -> function.apply(e.getKey(), e.getValue());
	}

	public static <K, V> Consumer<? super Entry<K, V>> entryConsumer(final BiConsumer<? super K, ? super V> action) {
		return e -> action.accept(e.getKey(), e.getValue());
	}
}