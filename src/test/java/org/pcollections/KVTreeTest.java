package org.pcollections;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KVTreeTest {
	@ParameterizedTest
	@MethodSource("intKeys")
	void indexOf_matchesArraysBinarySearch(List<Integer> keys) {
		// The tree
		KVTree<Integer, Integer> tree = KVTree.empty();
		for (Integer key: keys) {
			tree = tree.plus(key, null, Integer::compareTo);
		}

		// The equivalent array
		Integer[] array = keys.stream()
			.distinct().sorted()
			.toArray(Integer[]::new);

		for (int i = 0; i < VALUE_BOUND; i++) {
			int expected = Arrays.binarySearch(array, i);
			int actual = tree.indexOf(i, Integer::compareTo);
			assertEquals(expected, actual,
				"KVTree.indexOf should match Arrays.binarySearch for key " + i);
		}
	}

	static Stream<Arguments> intKeys() {
		Stream<Arguments> cornerCases = Stream.of(
			emptyList(),
			singletonList(-1),
			singletonList(0),
			singletonList(1),
			singletonList(VALUE_BOUND-1),
			singletonList(VALUE_BOUND),
			singletonList(VALUE_BOUND+1)
		).map(Arguments::of);
		Stream<Arguments> pseudorandomCases = LongStream.rangeClosed(1,30)
			.mapToObj(KVTreeTest::pseudorandomList)
			.map(Arguments::of);
		return Stream.of(
			pseudorandomCases,
			cornerCases
		).flatMap(identity());
	}

	static List<Integer> pseudorandomList(long seed) {
		Random random = new Random(seed);
		int length = random.nextInt(2, 60);
		return random
			.ints(1, VALUE_BOUND)
			.limit(length)
			.boxed()
			.toList();
	}

	/**
	 * Randomly chosen values are all strictly less than this.
	 */
	static final int VALUE_BOUND = 100;
}
