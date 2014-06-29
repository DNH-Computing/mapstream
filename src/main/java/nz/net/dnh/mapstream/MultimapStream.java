package nz.net.dnh.mapstream;

import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

/**
 * Class for retrieving a {@link MapStream} from a {@link Multimap}. Separate from {@link MapStream} so we don't require a runtime
 * dependency on google commons.
 */
public class MultimapStream {
	/** @return a binary operator that may be used to combine 2 Multimaps into a single Multimap */
	private static <K, V, M extends Multimap<K, V>> BinaryOperator<M> multimapCombiner() {
		return (m1, m2) -> {
			m1.putAll(m2);
			return m1;
		};
	}

	/**
	 * Return a new {@link MapStream} based on the entries from the given {@link Multimap}. Since multimaps may contain duplicate keys and
	 * values, the returned MapStream may contain duplicate keys and values.
	 */
	public static <K, V> MapStream<K, V> of(final Multimap<K, V> map) {
		return map.entries()::stream;
	}

	/**
	 * Return a {@link Collector} that accumulates elements from a {@link MapStream} into a {@link Multimap} whose keys and values are equal
	 * to the keys and values from the {@link MapStream}.
	 * 
	 * @see MapStream#collect(Collector)
	 */
	public static <K, V> Collector<Entry<K, V>, ?, Multimap<K, V>> toMultimap() {
		return toMultimap((Supplier<Multimap<K, V>>) HashMultimap::create, Characteristics.UNORDERED);
	}

	/**
	 * Return a {@link Collector} that accumulates elements from a {@link MapStream} into a {@link ListMultimap} whose keys and values are
	 * equal to the keys and values from the {@link MapStream}.
	 * 
	 * @see MapStream#collect(Collector)
	 */
	public static <K, V> Collector<Entry<K, V>, ?, ListMultimap<K, V>> toListMultimap() {
		return toMultimap((Supplier<ListMultimap<K, V>>) ArrayListMultimap::create);
	}

	/**
	 * Return a {@link Collector} that accumulates elements from a {@link MapStream} into a {@link SetMultimap} whose keys and values are
	 * equal to the keys and values from the {@link MapStream}.
	 * 
	 * @see MapStream#collect(Collector)
	 */
	public static <K, V> Collector<Entry<K, V>, ?, SetMultimap<K, V>> toSetMultimap() {
		return toMultimap((Supplier<SetMultimap<K, V>>) HashMultimap::create, Characteristics.UNORDERED);
	}

	/**
	 * Return a {@link Collector} that accumulates elements from a {@link MapStream} into a {@link Multimap} whose keys and values are equal
	 * to the keys and values from the {@link MapStream}.
	 * <p>
	 * The multimap is constructed by the given {@link Supplier}, e.g. {@code LinkedHashMultiMap::new}
	 * 
	 * @param multimapSupplier
	 *            A function which returns a new, empty Multimap into which the results will be inserted
	 * @param characteristics
	 *            The characteristics of the returned Collector; see {@link Collector#characteristics()}. Note that the returned collector
	 *            will always have the characteristic {@link Characteristics#IDENTITY_FINISH}
	 * @see MapStream#collect(Collector)
	 */
	public static <K, V, M extends Multimap<K, V>> Collector<Entry<K, V>, ?, M> toMultimap(final Supplier<M> multimapSupplier,
			final Characteristics... characteristics) {
		return Collector.of(multimapSupplier, (m, e) -> m.put(e.getKey(), e.getValue()), multimapCombiner(), characteristics);
	}
}
