package io.github.alexxfromgit.taf.mobile.core.perf;

import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PerfReportTest {

    @Test
    public void statistics() {
        PerfStats stats = PerfStats.of(List.of(100L, 300L, 200L, 400L, 1000L));
        assertThat(stats).isEqualTo(new PerfStats(5, 100, 400, 1000, 1000));
        assertThat(PerfStats.of(List.of())).isEqualTo(new PerfStats(0, 0, 0, 0, 0));
    }

    @Test
    public void slowMeasurementsAreFlaggedPerKind() {
        Map<PerfCollector.Key, PerfStats> stats = new LinkedHashMap<>();
        stats.put(new PerfCollector.Key(PerfCollector.Kind.TRANSITION, "Catalog", "firstProduct", "Product"),
                new PerfStats(3, 900, 1500, 4000, 4000));
        stats.put(new PerfCollector.Key(PerfCollector.Kind.LOOKUP, "Catalog", "title", ""),
                new PerfStats(10, 50, 80, 200, 300));

        PerfReport report = new PerfReport(stats, 1000, 3000);

        assertThat(report.overThreshold()).containsExactly(
                "TRANSITION Catalog --firstProduct--> Product: p95 4000 ms > 3000 ms");
        assertThat(report.toHtml()).contains("Screen transitions").contains("class=\"bad num\">4000");
        assertThat(report.toJson()).contains("LOOKUP Catalog.title");
    }
}
