package io.github.alexxfromgit.taf.mobile.core.testng;

import io.github.alexxfromgit.taf.mobile.core.allure.AllureResults;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.driver.DriverFactory;
import io.github.alexxfromgit.taf.mobile.core.driver.DriverManager;
import io.github.alexxfromgit.taf.mobile.core.perf.PerfCollector;
import io.github.alexxfromgit.taf.mobile.core.perf.PerfReport;
import io.github.alexxfromgit.taf.mobile.core.report.ReportFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IExecutionListener;

import java.util.Map;
import java.util.TreeMap;

/**
 * Run-level lifecycle: installs Allure categories before the first test; after the last one writes the
 * performance report and the Allure environment, closes all sessions and stops managed Appium servers.
 */
public class TafExecutionListener implements IExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(TafExecutionListener.class);

    @Override
    public void onExecutionStart() {
        AllureResults.installCategories();
    }

    @Override
    public void onExecutionFinish() {
        try {
            if (!PerfCollector.isEmpty()) {
                PerfReport report = PerfReport.fromCollector();
                ReportFiles.write("screen-performance.html", report.toHtml());
                ReportFiles.write("screen-performance.json", report.toJson());
            }
            AllureResults.writeEnvironment(environment());
        } catch (RuntimeException e) {
            LOG.error("Could not write end-of-run reports", e);
        } finally {
            DriverManager.quitAll();
            DriverFactory.stopManagedServers();
        }
    }

    private static Map<String, String> environment() {
        TafConfig config = TafConfig.get();
        Map<String, String> values = new TreeMap<>();
        values.put("platform", config.string("platform", "android"));
        values.put("target", config.string("target", "local"));
        config.optional("cloud.provider").ifPresent(v -> values.put("cloud.provider", v));
        config.optional("devices").ifPresent(v -> values.put("devices", v));
        config.optional("app.path").ifPresent(v -> values.put("app", v));
        return values;
    }
}
