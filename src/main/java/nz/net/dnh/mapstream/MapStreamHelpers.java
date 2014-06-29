package nz.net.dnh.mapstream;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/** Internal helpers for {@link MapStream} */
class MapStreamHelpers {
	/** Object used in place of {@code null} in a ConcurrentHashMap */
	private static final Object NULL = new Object();

	public static <K, V> BiFunction<K, V, K> keyBiFunction() {
		return (k, v) -> k;
	}

	public static <K, V> BiFunction<K, V, V> valueBiFunction() {
		return (k, v) -> v;
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

	/**
	 * @return a stateful null-safe predicate which returns true if the given value has not been seen before by this predicate, and false if
	 *         it has
	 */
	public static <T> Predicate<T> distinctPredicate() {
		final Set<Object> seenKeys = Collections.newSetFromMap(new ConcurrentHashMap<>());
		return o -> seenKeys.add(o == null ? NULL : o);
	}
}