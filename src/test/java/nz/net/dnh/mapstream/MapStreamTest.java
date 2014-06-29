package nz.net.dnh.mapstream;
import static java.util.stream.Collectors.toList;
import static nz.net.dnh.mapstream.EntryCollectors.toMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.theInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class MapStreamTest {
	private static final Map<String, Integer> MAP = ImmutableMap.of("key1", 1, "key2", 2, "key3", 3);

	@Mock
	private Map<String, Object> mockMap;
	@Mock
	private Set<Entry<String, Object>> mockEntrySet;
	@Mock
	private Stream<Entry<String, Object>> mockEntryStream;
	@Mock
	private Collector<Entry<String, Object>, ?, Integer> collector;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		when(this.mockMap.entrySet()).thenReturn(this.mockEntrySet);
		when(this.mockEntrySet.stream()).thenReturn(this.mockEntryStream);
	}

	@Test
	public void entryStreamReturnsStreamOfMapEntries() {
		assertThat(MapStream.of(this.mockMap), hasEntryStream(theInstance(this.mockEntryStream)));
	}
	@Test
	public void keyStreamReturnsStreamOfMapKeys() {
		assertThat(MapStream.of(MAP).keyStream().collect(toList()), is(listOf(MAP.keySet())));
	}

	@Test
	public void valueStreamReturnsStreamOfMapValues() {
		assertThat(MapStream.of(MAP).valueStream().collect(toList()), is(listOf(MAP.values())));
	}

	@Test
	public void collectPassesCollectorToEntryStream() {
		when(this.mockEntryStream.collect(this.collector)).thenReturn(7);
		assertEquals(7, (int) MapStream.of(this.mockMap).collect(this.collector));
	}

	@Test
	public void filterFiltersByKeyAndValue() {
		assertThat(MapStream.of(MAP).filter((k, v) -> k.equals("key1") && v.equals(1)).collect(toMap()),
				is(Collections.singletonMap("key1", 1)));
	}

	@Test
	public void filterKeysFiltersByKey() {
		assertThat(MapStream.of(MAP).filterKeys("key2"::equals).collect(toMap()), is(Collections.singletonMap("key2", 2)));
	}

	@Test
	public void filterValuesFiltersByValue() {
		assertThat(MapStream.of(MAP).filterValues(v -> v == 3).collect(toMap()), is(Collections.singletonMap("key3", 3)));
	}

	@Test
	public void mapMapsKeysAndValuesToSingleStream() {
		assertThat(MapStream.of(MAP).map((k, v) -> k + "=" + v).collect(toList()), contains("key1=1", "key2=2", "key3=3"));
	}

	@Test
	public void mapKeysMapsKeysToMapStreamWithNewKeys() {
		assertThat(MapStream.of(MAP).mapKeys(s -> s.charAt(3)).collect(toList()), contains(entries('1', 1, '2', 2, '3', 3)));
	}

	@Test
	public void mapKeysMapsKeysAndValuesToMapStreamWithNewKeys() {
		assertThat(MapStream.of(MAP).mapKeys((k, v) -> k + "=" + v).collect(toList()),
				contains(entries("key1=1", 1, "key2=2", 2, "key3=3", 3)));
	}

	@Test
	public void mapValuesMapsValuesToMapStreamWithNewValues() {
		assertThat(MapStream.of(MAP).mapValues(v -> v * 2).collect(toList()), contains(entries("key1", 2, "key2", 4, "key3", 6)));
	}

	@Test
	public void mapValuesMapsKeysAndValuesToMapStreamWithNewValues() {
		assertThat(MapStream.of(MAP).mapValues((k, v) -> v + k.length()).collect(toList()),
				contains(entries("key1", 5, "key2", 6, "key3", 7)));
	}

	@Test
	public void mapWithKeyAndValueFunctionsReturnsMapStreamWithNewKeysAndValues() {
		assertThat(MapStream.of(MAP).map(k -> k.toUpperCase(), v -> v * 2).collect(toList()),
				contains(entries("KEY1", 2, "KEY2", 4, "KEY3", 6)));
	}

	@Test
	public void mapWithKeyAndValueBiFunctionsReturnsMapStreamWithNewKeysAndValues() {
		assertThat(MapStream.of(MAP).map((k, v) -> v, (k, v) -> k).collect(toList()), contains(entries(1, "key1", 2, "key2", 3, "key3")));
	}

	@Test
	public void distinctReturnsMapStreamOfDistinctEntries() {
		final MapStream<String, Integer> stream = () -> Stream.concat(MAP.entrySet().stream(),
				Stream.of(new SimpleEntry<>("key2", 2), new SimpleEntry<>("key2", 4), new SimpleEntry<>("key4", 2)));

		assertThat(stream.distinct().collect(toList()), contains(entries("key1", 1, "key2", 2, "key3", 3, "key2", 4, "key4", 2)));
	}

	@Test
	public void distinctKeysReturnsMapStreamOfDistinctKeys() {
		final MapStream<String, Integer> stream = () -> Stream.concat(MAP.entrySet().stream(),
				Stream.of(new SimpleEntry<>("key2", 2), new SimpleEntry<>("key2", 4), new SimpleEntry<>("key4", 2)));

		assertThat(stream.distinctKeys().collect(toList()), contains(entries("key1", 1, "key2", 2, "key3", 3, "key4", 2)));
	}

	@Test
	public void distinctValuesReturnsMapStreamOfDistinctValues() {
		final MapStream<String, Integer> stream = () -> Stream.concat(MAP.entrySet().stream(),
				Stream.of(new SimpleEntry<>("key2", 2), new SimpleEntry<>("key2", 4), new SimpleEntry<>("key4", 2)));

		assertThat(stream.distinctValues().collect(toList()), contains(entries("key1", 1, "key2", 2, "key3", 3, "key2", 4)));
	}

	@Test
	public void sortedKeysReturnsMapStreamInNaturalOrder() {
		assertThat(MapStream.of(ImmutableMap.of(5, "foo", 4, "bar", 3, "baz")).sortedKeys().collect(toList()),
				contains(entries(3, "baz", 4, "bar", 5, "foo")));
	}

	@Test
	public void sortedKeysThrowsExceptionOnTerminalOpForNonComparableKeys() {
		final MapStream<NotComparable<Integer>, String> stream = MapStream.of(ImmutableMap.of(new NotComparable<>(5), "foo",
				new NotComparable<>(4), "bar", new NotComparable<>(3), "baz")).sortedKeys();

		this.expectedException.expect(Exception.class);

		stream.collect(toList());
	}

	@Test
	public void sortedKeysWithComparatorReturnsMapStreamInComparatorOrder() {
		final MapStream<NotComparable<Integer>, String> stream = MapStream.of(ImmutableMap.of(new NotComparable<>(5), "foo",
				new NotComparable<>(4), "bar", new NotComparable<>(3), "baz")).sortedKeys(NotComparable.comparator());

		assertThat(
				stream.collect(toList()),
				contains(entries(new NotComparable<Integer>(3), "baz",
						new NotComparable<Integer>(4), "bar",
						new NotComparable<Integer>(5), "foo"
				)));
	}

	@Test
	public void sortedValuesReturnsMapStreamInNaturalOrder() {
		assertThat(MapStream.of(ImmutableMap.of(5, "foo", 4, "bar", 3, "baz")).sortedValues().collect(toList()),
				contains(entries(4, "bar", 3, "baz", 5, "foo")));
	}

	@Test
	public void sortedValuesThrowsExceptionOnTerminalOpForNonComparableValues() {
		final MapStream<Integer, NotComparable<String>> stream = MapStream.of(
				ImmutableMap.of(5, new NotComparable<>("foo"), 4, new NotComparable<>("bar"), 3, new NotComparable<>("baz")))
				.sortedValues();

		this.expectedException.expect(Exception.class);

		stream.collect(toList());
	}

	@Test
	public void sortedValuesWithComparatorReturnsMapStreamInComparatorOrder() {
		final MapStream<Integer, NotComparable<String>> stream = MapStream.of(
				ImmutableMap.of(5, new NotComparable<>("foo"), 4, new NotComparable<>("bar"), 3, new NotComparable<>("baz")))
				.sortedValues(NotComparable.comparator());

		assertThat(stream.collect(toList()),
				contains(entries(4, new NotComparable<>("bar"), 3, new NotComparable<>("baz"), 5, new NotComparable<>("foo"))));
	}

	@Test
	public void peekPassesKeysAndValuesToBiConsumerOnTerminalOp() {
		final Map<String, Integer> seenEntries = new LinkedHashMap<>();
		final MapStream<String, Integer> stream = MapStream.of(MAP).peek(seenEntries::put);

		assertThat(seenEntries, is(emptyMap()));

		assertEquals(MAP.size(), stream.count());
		assertThat(seenEntries, is(MAP));
	}

	@Test
	public void peekKeysPassesKeysToConsumerOnTerminalOp() {
		final List<String> seenKeys = new ArrayList<>();
		final MapStream<String, Integer> stream = MapStream.of(MAP).peekKeys(seenKeys::add);

		assertThat(seenKeys, is(empty()));

		assertEquals(MAP.size(), stream.count());
		assertThat(seenKeys, contains("key1", "key2", "key3"));
	}

	@Test
	public void peekValuesPassesKeysToConsumerOnTerminalOp() {
		final List<Integer> seenValues = new ArrayList<>();
		final MapStream<String, Integer> stream = MapStream.of(MAP).peekValues(seenValues::add);

		assertThat(seenValues, is(empty()));

		assertEquals(MAP.size(), stream.count());
		assertThat(seenValues, contains(1, 2, 3));
	}

	@Test
	public void limitReturnsLimitedMapStream() {
		assertThat(MapStream.of(MAP).limit(2).collect(toList()), contains(entries("key1", 1, "key2", 2)));
	}

	@Test
	public void skipReturnsMapStreamSkippingNEntries() {
		assertThat(MapStream.of(MAP).skip(2).collect(toList()), contains(entries("key3", 3)));
	}

	@Test
	public void forEachPassesKeysAndValuesToBiConsumer() {
		final Map<String, Integer> seenEntries = new LinkedHashMap<>();
		MapStream.of(MAP).forEach(seenEntries::put);

		assertThat(seenEntries, is(MAP));
	}

	@Test
	public void forEachOrderedPassesKeysAndValuesToBiConsumer() {
		final Map<String, Integer> seenEntries = new LinkedHashMap<>();
		MapStream.of(MAP).forEachOrdered(seenEntries::put);

		assertThat(seenEntries, is(MAP));
	}

	@Test
	public void anyMatchReturnsTrueIfAnyItemsMatch() {
		assertTrue(MapStream.of(MAP).anyMatch((k, v) -> v.equals(2)));
	}

	@Test
	public void anyMatchReturnsFalseIfNoItemsMatch() {
		assertFalse(MapStream.of(MAP).anyMatch((k, v) -> v.equals(4)));
	}

	@Test
	public void allMatchReturnsTrueIfAllItemsMatch() {
		assertTrue(MapStream.of(MAP).allMatch((k, v) -> Integer.valueOf(k.substring(3)).equals(v)));
	}

	@Test
	public void allMatchReturnsFalseIfAnyItemsDontMatch() {
		assertFalse(MapStream.of(MAP).allMatch((k, v) -> v < 3));
	}

	@Test
	public void noneMatchReturnsTrueIfNoItemsMatch() {
		assertTrue(MapStream.of(MAP).noneMatch((k, v) -> v < 0));
	}

	@Test
	public void noneMatchReturnsFalseIfAnyItemsMatch() {
		assertFalse(MapStream.of(MAP).noneMatch((k, v) -> k.equals("key1")));
	}

	private static <K, V> Matcher<Map<K, V>> emptyMap() {
		return new FeatureMatcher<Map<K, V>, Set<Entry<K, V>>>(empty(), "empty map", "entry set") {
			@Override
			protected Set<Entry<K, V>> featureValueOf(final Map<K, V> map) {
				return map.entrySet();
			}
		};
	}

	private static <K, V> Matcher<MapStream<K, V>> hasEntryStream(final Matcher<? super Stream<Entry<K, V>>> entryStreamMatcher) {
		return new FeatureMatcher<MapStream<K, V>, Stream<Entry<K, V>>>(entryStreamMatcher, "has entry stream", "entry stream") {
			@Override
			protected Stream<Entry<K, V>> featureValueOf(final MapStream<K, V> mapStream) {
				return mapStream.entryStream();
			}
		};
	}

	private static <T> List<T> listOf(final Collection<T> collection) {
		return Lists.newArrayList(collection);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Entry<K, V>[] entries(final K key1, final V value1, final Object... others) {
		final Entry<K, V>[] entries = new Entry[others.length / 2 + 1];
		entries[0] = new SimpleEntry<>(key1, value1);
		for (int i = 0; i < others.length / 2; i++) {
			entries[i + 1] = new SimpleEntry<>((K) others[i * 2], (V) others[i * 2 + 1]);
		}
		return entries;
	}

	/** Object wrapper which does not implement Comparable, to test the sortedX methods on non-Comparable objects */
	private static class NotComparable<T> {
		/** Return a comparator for comparing the wrapped objects (assuming the wrapped objects implement Comparable) */
		public static <T extends Comparable<? super T>> Comparator<NotComparable<T>> comparator() {
			return (o1, o2) -> o1.obj.compareTo(o2.obj);
		}

		private final T obj;

		public NotComparable(final T obj) {
			this.obj = obj;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(this.obj);
		}

		@Override
		public boolean equals(final Object other) {
			return other instanceof NotComparable && Objects.equals(this.obj, ((NotComparable<?>) other).obj);
		}

		@Override
		public String toString() {
			return "NotComparable [obj=" + this.obj + "]";
		}
	}
}
