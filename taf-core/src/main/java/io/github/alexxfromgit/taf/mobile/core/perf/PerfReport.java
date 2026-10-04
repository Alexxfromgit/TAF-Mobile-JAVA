package io.github.alexxfromgit.taf.mobile.core.perf;

import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.report.Html;
import io.github.alexxfromgit.taf.mobile.core.report.Json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** HTML/JSON rendering of {@link PerfCollector} data with threshold highlighting. */
public final class PerfReport {

    private final Map<PerfCollector.Key, PerfStats> stats;
    private final long lookupThreshold;
    private final long transitionThreshold;

    public PerfReport(Map<PerfCollector.Key, PerfStats> stats, long lookupThresholdMs, long transitionThresholdMs) {
        this.stats = stats;
        this.lookupThreshold = lookupThresholdMs;
        this.transitionThreshold = transitionThresholdMs;
    }

    public static PerfReport fromCollector() {
        TafConfig config = TafConfig.get();
        return new PerfReport(PerfCollector.snapshot(),
                config.duration("perf.threshold.lookup").toMillis(),
                config.duration("perf.threshold.transition").toMillis());
    }

    public long threshold(PerfCollector.Kind kind) {
        return kind == PerfCollector.Kind.LOOKUP ? lookupThreshold : transitionThreshold;
    }

    public boolean isEmpty() {
        return stats.isEmpty();
    }

    /** Measurements whose p95 exceeds the threshold of their kind. */
    public List<String> overThreshold() {
        List<String> slow = new ArrayList<>();
        stats.forEach((key, s) -> {
            if (s.p95() > threshold(key.kind())) {
                slow.add(key.kind() + " " + key.label() + ": p95 " + s.p95() + " ms > " + threshold(key.kind()) + " ms");
            }
        });
        return slow;
    }

    public String toHtml() {
        Html html = new Html("Screen performance");
        if (stats.isEmpty()) {
            return html.paragraph("No timings were recorded.").render();
        }
        html.paragraph("Client-side timings in ms, including driver overhead - compare trends between builds. "
                + "Thresholds: element lookup " + lookupThreshold + " ms, transition / screen ready "
                + transitionThreshold + " ms.");
        for (PerfCollector.Kind kind : PerfCollector.Kind.values()) {
            List<List<String>> rows = new ArrayList<>();
            stats.forEach((key, s) -> {
                if (key.kind() == kind) {
                    String p95 = (s.p95() > threshold(kind) ? "!bad:" : "") + s.p95();
                    rows.add(List.of(key.label(), String.valueOf(s.count()), String.valueOf(s.min()),
                            String.valueOf(s.avg()), p95, String.valueOf(s.max())));
                }
            });
            if (!rows.isEmpty()) {
                html.heading(2, switch (kind) {
                    case TRANSITION -> "Screen transitions (tap until the next screen is ready)";
                    case SCREEN_READY -> "Screen ready";
                    case LOOKUP -> "Element lookups";
                });
                html.table(List.of("What", "Count", "Min", "Avg", "p95", "Max"), rows);
            }
        }
        return html.render();
    }

    public String toJson() {
        Map<String, Object> root = new LinkedHashMap<>();
        stats.forEach((key, s) -> root.put(key.kind() + " " + key.label(), s));
        return Json.pretty(root);
    }
}
