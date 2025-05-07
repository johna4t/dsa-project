package com.sharedsystemshome.dsa.util;

import java.util.List;
import java.util.function.Function;

public class JpaLogUtils {

    /**
     * Extracts IDs or other fields from a list using the provided extractor function.
     *
     * Note: If the list is lazily loaded and uninitialized (e.g. outside a transaction),
     * this method may throw LazyInitializationException. Use with care in toString().
     *
     * @param list The list of objects (may be null or lazily initialized)
     * @param idExtractor A function to extract a property (typically an ID) from each element
     * @return A list of extracted values or null if input list is null
     * TODO for permanent fix, refactor entity classes to avoid use of toString in logging,
     */
    public static <T, R> List<R> getObjectIds(List<T> list, Function<T, R> idExtractor) {
        if (list == null) return null;
        return list.stream()
                .map(idExtractor)
                .toList();
    }
}

