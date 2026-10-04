package io.github.alexxfromgit.taf.mobile.core.perf;

import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Client-side UX timings collected for free while functional tests run:
 * <ul>
 *     <li>{@code LOOKUP} - how long an element took to become visible;</li>
 *     <li>{@code TRANSITION} - tap on an element until the next screen was ready ({@code tapAndExpect});</li>
 *     <li>{@code SCREEN_READY} - {@code waitReady()} of a screen.</li>
 * </ul>
 * Numbers include Appium/driver overhead: compare trends between builds, not absolute values.
 */
public final class PerfCollector {

    public enum Kind { TRANSITION, SCREEN_READY, LOOKUP }

    /** What was measured, e.g. {@code TRANSITION CatalogScreen --firstProduct--> ProductScreen}. */
    public record Key(Kind kind, String from, String action, String to) {
        public String label() {
            return switch (kind) {
                case LOOKUP -> from + "." + action;
                case TRANSITION -> from + " --" + action + "--> " + to;
                case SCREEN_READY -> from;
            };
        }
    }

    private static final Map<Key, Queue<Long>> SAMPLES = new ConcurrentHashMap<>();

    private PerfCollector() {
    }

    public static void lookup(String screen, String element, long millis) {
        record(new Key(Kind.LOOKUP, screen, element, ""), millis);
    }

    public static void transition(String from, String action, String to, long millis) {
        record(new Key(Kind.TRANSITION, from, action, to), millis);
    }

    public static void screenReady(String screen, long millis) {
        record(new Key(Kind.SCREEN_READY, screen, "", ""), millis);
    }

    public static boolean isEmpty() {
        return SAMPLES.isEmpty();
    }

    public static void clear() {
        SAMPLES.clear();
    }

    /** Statistics per key, sorted by kind, then slowest p95 first. */
    public static Map<Key, PerfStats> snapshot() {
        Map<Key, PerfStats> stats = new LinkedHashMap<>();
        SAMPLES.forEach((key, samples) -> stats.put(key, PerfStats.of(new ArrayList<>(samples))));
        Map<Key, PerfStats> sorted = new LinkedHashMap<>();
        stats.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Key, PerfStats>, Kind>comparing(e -> e.getKey().kind())
                        .thenComparing(e -> -e.getValue().p95()))
                .forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }

    private static void record(Key key, long millis) {
        if (TafConfig.get().bool("perf.enabled", true)) {
            SAMPLES.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(millis);
        }
    }
}
