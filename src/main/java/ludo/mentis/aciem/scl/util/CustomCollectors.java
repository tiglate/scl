package ludo.mentis.aciem.scl.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Utility class providing custom collectors for use with Java Streams.
 * <p>
 * This class contains static methods that return collectors for specific use cases,
 * such as collecting elements into a LinkedHashMap while preserving the order of elements.
 * </p>
 * <p>
 * The class is designed to be non-instantiable and contains a private constructor
 * to prevent instantiation.
 * </p>
 */
public final class CustomCollectors {
    private static final String DUPLICATE_KEY_MESSAGE = "Duplicate key detected";

    /**
     * Private constructor to prevent instantiation.
     */
    private CustomCollectors() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Provides a Collector that collects elements into a LinkedHashMap preserving insertion order.
     *
     * @param keyMapper   a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a Collector that collects values into an insertion-order map
     * @throws IllegalStateException if duplicate keys are encountered
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedHashMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(
                keyMapper,
                valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(DUPLICATE_KEY_MESSAGE);
                },
                LinkedHashMap::new
        );
    }
}