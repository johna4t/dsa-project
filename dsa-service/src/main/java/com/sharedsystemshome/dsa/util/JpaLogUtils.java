package com.sharedsystemshome.dsa.util;

import com.sharedsystemshome.dsa.model.SharedDataContent;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     */
    public static <T, R> List<R> getObjectIds(List<T> list, Function<T, R> idExtractor) {
        if (list == null) return null;
        return list.stream()
                .map(idExtractor)
                .toList();
    }

    /**
     * Converts a list of SharedDataContent entries to a readable string of DataContentDefinition IDs.
     *
     * Safely returns "uninitialized" if the list has not been initialized (lazy loading guard).
     *
     * @param sharedDataContent List of SharedDataContent, possibly uninitialized
     * @return String representation of DataContentDefinition IDs or "uninitialized"
     */
    public static String getSharedDataContentIds(List<SharedDataContent> sharedDataContent) {
        if (sharedDataContent == null) return "null";

        if (!Hibernate.isInitialized(sharedDataContent)) {
            return "uninitialized";
        }

        return sharedDataContent.stream()
                .map(sdc -> {
                    if (sdc.getDataContentDefinition() == null) return "null";
                    return String.valueOf(sdc.getDataContentDefinition().getId());
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
