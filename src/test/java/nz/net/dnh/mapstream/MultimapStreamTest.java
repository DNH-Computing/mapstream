package nz.net.dnh.mapstream;

import static java.util.stream.Collectors.toList;
import static nz.net.dnh.mapstream.MapStreamTest.entries;
import static nz.net.dnh.mapstream.MultimapStream.toListMultimap;
import static nz.net.dnh.mapstream.MultimapStream.toMultimap;
import static nz.net.dnh.mapstream.MultimapStream.toSetMultimap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public class MultimapStreamTest {
	@Test
	public void mapStreamFromMultimap() {
		assertThat(MultimapStream.of(ImmutableMultimap.of("key1", 1, "key2", 2, "key1", 3))
				.filterKeys("key1"::equals).mapValues(i -> i + 100).entryStream().collect(toList()),
				contains(entries("key1", 101, "key1", 103)));
	}

	@Test
	public void collectMapStreamToMultimap() {
		final Multimap<String, Integer> multimap = MultimapStream.of(ImmutableMultimap.of("key1", 1, "key2", 2, "key1", 3))
				.filterKeys("key1"::equals).mapValues(i -> i + 100).collect(toMultimap());

		assertThat(multimap.entries(), contains(entries("key1", 101, "key1", 103)));
	}

	@Test
	public void collectMapStreamToListMultimap() {
		final ListMultimap<String, Integer> multimap = MultimapStream.of(ImmutableMultimap.of("key1", 1, "key2", 2, "key1", 3))
				.filterKeys("key1"::equals).mapValues(i -> i + 100).collect(toListMultimap());

		assertThat(multimap, is(ImmutableListMultimap.of("key1", 101, "key1", 103)));
	}

	@Test
	public void collectMapStreamToSetMultimap() {
		final SetMultimap<String, Integer> multimap = MultimapStream.of(ImmutableMultimap.of("key1", 1, "key2", 2, "key1", 3))
				.filterKeys("key1"::equals).mapValues(i -> i + 100).collect(toSetMultimap());

		assertThat(multimap, is(ImmutableSetMultimap.of("key1", 101, "key1", 103)));
	}
}
