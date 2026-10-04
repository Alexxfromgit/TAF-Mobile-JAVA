# Screen performance

Functional UI tests already wait for elements and screens. mobile-taf measures those waits and reports them, so
you get a performance trend without writing performance tests.

| Kind | Measured | Source |
|---|---|---|
| Transition | Tap until the next screen is ready | `UiElement.tapAndExpect(Next.class)` |
| Screen ready | `waitReady()` of a screen | every `on(...)` / `expect(...)` / transition |
| Element lookup | Until an element became visible | every `UiElement` action |

The *Screen performance* report (Allure → Framework reports, plus
`examples/target/taf-reports/screen-performance.html`) shows count, min, avg, p95 and max per measurement, sorted
by slowest p95. Values above the thresholds are highlighted:

```properties
perf.threshold.lookup=1s
perf.threshold.transition=3s
perf.fail=false        # true: the report test fails when a p95 exceeds its threshold
perf.enabled=true
```

Read the numbers as **trends between builds on the same device type**. They include Appium and driver overhead,
and on cloud devices also network latency. A transition that jumps from 1.2 s to 4 s after a commit is the signal,
not the absolute value.

Add `MobileReports` as the last `<test>` of a suite to get the report as a test result. The HTML/JSON files are
written at the end of the run either way.
