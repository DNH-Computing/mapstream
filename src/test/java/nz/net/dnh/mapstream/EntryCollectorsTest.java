package nz.net.dnh.mapstream;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class EntryCollectorsTest {
	private static final Map<Integer, String> MAP = ImmutableMap.of(1, "2", 3, "4", 5, "6", 7, "8");

	@Test
	public void toMapWithNoArgumentsReturnsSimpleMapCollector() {
		assertEquals(MAP, MAP.entrySet().stream().collect(EntryCollectors.toMap()));
	}

	@Test(expected = IllegalStateException.class)
	public void toMapWithNoArgumentsFailsWithDuplicateKeys() {
		Stream.concat(MAP.entrySet().stream(), Stream.of(new SimpleEntry<>(1, "new"))).collect(EntryCollectors.toMap());
	}

	@Test
	public void toMapWithMergeFunctionCollectsSimpleMap() {
		assertEquals(MAP, MAP.entrySet().stream().collect(EntryCollectors.toMap((s1, s2) -> s1 + s2)));
	}

	@Test
	public void toMapWithMergeFunctionCollectsDuplicateKeys() {
		final Map<Integer, String> collectedMap = Stream.concat(MAP.entrySet().stream(), Stream.of(new SimpleEntry<>(1, "new"))).collect(
				EntryCollectors.toMap((s1, s2) -> s1 + s2));
		assertEquals(ImmutableMap.of(1, "2new", 3, "4", 5, "6", 7, "8"), collectedMap);
	}

	@Test
	public void toMapWithMergeFunctionAndSupplierReturnsTypedMap() {
		final TreeMap<Integer, String> collectedMap = Stream.concat(MAP.entrySet().stream(), Stream.of(new SimpleEntry<>(1, "new")))
				.collect(
						EntryCollectors.toMap((s1, s2) -> s1 + s2, TreeMap::new));
		assertEquals(ImmutableMap.of(1, "2new", 3, "4", 5, "6", 7, "8"), collectedMap);
	}
}
