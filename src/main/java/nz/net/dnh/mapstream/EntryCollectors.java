package nz.net.dnh.mapstream;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/** Helper {@link Collector Collectors} for use with map entries */
public class EntryCollectors {
	/**
	 * Return a {@link Collector} that accumulates elements from a {@link MapStream} into a {@link Map} whose keys and values are equal to
	 * the keys and values from the {@link MapStream}.
	 * <p>
	 * If the keys have duplicates, an {@link IllegalStateException} is thrown when the collection operation is performed.
	 * 
	 * @see MapStream#collect(Collector)
	 * @see Collectors#toMap(java.util.function.Function, java.util.function.Function)
	 */
	public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> toMap() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	/**
	 * Return a {@link Collector} that accumulates elements from a {@link MapStream} into a {@link Map} whose keys and values are equal to
	 * the keys and values from the {@link MapStream}.
	 * <p>
	 * If the keys have duplicates, the given {@link BinaryOperator} is called to merge the values into a single value.
	 * 
	 * @param mergeFunction
	 *            a merge function, used to resolve collisions between values associated with the same key, as supplied to
	 *            {@link Map#merge(Object, Object, java.util.function.BiFunction)}
	 * @see MapStream#collect(Collector)
	 * @see Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator)
	 */
	public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> toMap(final BinaryOperator<V> mergeFunction) {
		return Collectors.toMap(Entry::getKey, Entry::getValue, mergeFunction);
	}

	/**
	 * Return a {@link Collector} that accumulates elements from a {@link MapStream} into a {@link Map} whose keys and values are equal to
	 * the keys and values from the {@link MapStream}.
	 * <p>
	 * The map is constructed by the given {@link Supplier}, e.g. {@code TreeMap::new}
	 * <p>
	 * If the keys have duplicates, the given {@link BinaryOperator} is called to merge the values into a single value.
	 * 
	 * @param mergeFunction
	 *            a merge function, used to resolve collisions between values associated with the same key, as supplied to
	 *            {@link Map#merge(Object, Object, java.util.function.BiFunction)}
	 * @param mapSupplier
	 *            a function which returns a new, empty Map into which the results will be inserted
	 * @see MapStream#collect(Collector)
	 * @see Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, Supplier)
	 */
	public static <K, V, M extends Map<K, V>> Collector<Entry<K, V>, ?, M> toMap(final BinaryOperator<V> mergeFunction,
			final Supplier<M> mapSupplier) {
		return Collectors.toMap(Entry::getKey, Entry::getValue, mergeFunction, mapSupplier);
	}
}
