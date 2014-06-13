package nz.net.dnh.mapstream;
import static java.util.stream.Collectors.toList;
import static nz.net.dnh.mapstream.EntryCollectors.toMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.theInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
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

	private Collector<Entry<String, Object>, ?, Integer> collector;

	@Before
	public void setup() {
		when(this.mockMap.entrySet()).thenReturn(this.mockEntrySet);
		when(this.mockEntrySet.stream()).thenReturn(this.mockEntryStream);
	}

	@Test
	public void entryStreamReturnsStreamOfMapEntries() {
		assertThat(MapStream.of(this.mockMap).entryStream(), is(theInstance(this.mockEntryStream)));
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

	private static <T> List<T> listOf(final Collection<T> collection) {
		return Lists.newArrayList(collection);
	}

	@SuppressWarnings("unchecked")
	private static <K, V> Entry<K, V>[] entries(final K key1, final V value1, final Object... others) {
		final Entry<K, V>[] entries = new Entry[others.length / 2 + 1];
		entries[0] = new SimpleEntry<>(key1, value1);
		for (int i = 0; i < others.length / 2; i++) {
			entries[i + 1] = new SimpleEntry<>((K) others[i * 2], (V) others[i * 2 + 1]);
		}
		return entries;
	}
}
