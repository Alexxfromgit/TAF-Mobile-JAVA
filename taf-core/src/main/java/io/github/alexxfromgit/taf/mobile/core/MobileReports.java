package io.github.alexxfromgit.taf.mobile.core;

import io.github.alexxfromgit.taf.mobile.core.allure.AllureResults;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.PotentialDefectException;
import io.github.alexxfromgit.taf.mobile.core.log.Log;
import io.github.alexxfromgit.taf.mobile.core.perf.PerfReport;
import io.github.alexxfromgit.taf.mobile.core.report.ReportFiles;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Suite-level reports, run as ordinary tests so they appear in Allure. Add as the LAST {@code <test>} of a suite:
 * <pre>{@code
 * <test name="Framework reports">
 *     <classes><class name="io.github.alexxfromgit.taf.mobile.core.MobileReports"/></classes>
 * </test>
 * }</pre>
 */
@Epic("Framework reports")
@Owner("mobile-taf")
public class MobileReports {

    @Test(description = "Screen performance timings")
    @Feature("Screen performance")
    public void screenPerformance() {
        PerfReport report = PerfReport.fromCollector();
        ReportFiles.write("screen-performance.html", report.toHtml());
        ReportFiles.write("screen-performance.json", report.toJson());
        AllureResults.attachHtml("Screen performance", report.toHtml());
        AllureResults.attachJson("Screen performance (json)", report.toJson());

        List<String> slow = report.overThreshold();
        Log.info(slow.isEmpty() ? "All timings within thresholds" : slow.size() + " timing(s) above threshold");
        if (!slow.isEmpty() && TafConfig.get().bool("perf.fail", false)) {
            throw new PotentialDefectException("Screen timings above threshold (perf.fail=true):\n  "
                    + String.join("\n  ", slow));
        }
    }
}
