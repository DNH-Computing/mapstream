package nz.net.dnh.mapstream;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/** Helper {@link Collector Collectors} for use with map entries */
public class EntryCollectors {
	public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> toMap() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> toMap(final BinaryOperator<V> mergeFunction) {
		return Collectors.toMap(Entry::getKey, Entry::getValue, mergeFunction);
	}

	public static <K, V, M extends Map<K, V>> Collector<Entry<K, V>, ?, M> toMap(final BinaryOperator<V> mergeFunction,
			final Supplier<M> mapSupplier) {
		return Collectors.toMap(Entry::getKey, Entry::getValue, mergeFunction, mapSupplier);
	}
}
