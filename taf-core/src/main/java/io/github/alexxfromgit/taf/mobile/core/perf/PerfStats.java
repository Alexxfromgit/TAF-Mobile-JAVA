package io.github.alexxfromgit.taf.mobile.core.perf;

import java.util.List;

/** Summary of a series of durations in milliseconds. */
public record PerfStats(int count, long min, long avg, long p95, long max) {

    public static PerfStats of(List<Long> samples) {
        if (samples.isEmpty()) {
            return new PerfStats(0, 0, 0, 0, 0);
        }
        List<Long> sorted = samples.stream().sorted().toList();
        long sum = sorted.stream().mapToLong(Long::longValue).sum();
        int p95Index = (int) Math.ceil(0.95 * sorted.size()) - 1;
        return new PerfStats(sorted.size(), sorted.get(0), Math.round((double) sum / sorted.size()),
                sorted.get(Math.max(0, p95Index)), sorted.get(sorted.size() - 1));
    }
}
