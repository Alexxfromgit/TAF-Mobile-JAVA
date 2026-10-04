package io.github.alexxfromgit.taf.mobile.core.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import io.github.alexxfromgit.taf.mobile.core.failure.PotentialDefectException;
import io.github.alexxfromgit.taf.mobile.core.report.Json;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;

/**
 * Network mocking for app tests: the app talks to this server instead of (or through it to) its backend.
 * Everything is passed through to the real backend unless a test stubs it, so tests only fake what they need -
 * an error, an empty list, a slow response - and can verify what the app sent.
 * <pre>{@code
 * try (MockServer mock = MockServer.reverseProxy("https://api.example.com")) {
 *     // start the app with its API base URL pointing to mock.baseUrl() (debug build setting / launch argument)
 *     mock.stub(get(urlPathEqualTo("/products")).willReturn(okJson("[]")));
 *     ...  // UI shows the empty state
 *     mock.verify(1, getRequestedFor(urlPathEqualTo("/products")));
 * }
 * }</pre>
 * Two modes:
 * <ul>
 *     <li>{@link #reverseProxy(String)} - the app's base URL points to the mock; unmatched calls go to {@code upstream};</li>
 *     <li>{@link #forwardProxy()} - the device's HTTP proxy points to the mock; unmatched calls go to their original
 *     host. HTTPS needs the device to trust WireMock's CA (Android: network_security_config in a debug build).</li>
 * </ul>
 * See docs/network-mocking.md.
 */
public final class MockServer implements AutoCloseable {

    private static final int PASSTHROUGH_PRIORITY = 10;
    private static final int STUB_PRIORITY = 1;

    private final WireMockServer server;
    private final String upstream;

    private MockServer(WireMockConfiguration options, String upstream) {
        this.server = new WireMockServer(options);
        this.upstream = upstream;
        server.start();
        addPassthrough();
    }

    /** Reverse proxy on a random port in front of {@code upstreamBaseUrl}. */
    public static MockServer reverseProxy(String upstreamBaseUrl) {
        return reverseProxy(0, upstreamBaseUrl);
    }

    public static MockServer reverseProxy(int port, String upstreamBaseUrl) {
        return new MockServer(WireMockConfiguration.options().port(port), upstreamBaseUrl);
    }

    /** Forward (HTTP) proxy on a random port. */
    public static MockServer forwardProxy() {
        return forwardProxy(0);
    }

    public static MockServer forwardProxy(int port) {
        return new MockServer(WireMockConfiguration.options().port(port).enableBrowserProxying(true), null);
    }

    /** {@code http://localhost:<port>}. From an Android emulator the host is {@code 10.0.2.2}. */
    public String baseUrl() {
        return "http://localhost:" + server.port();
    }

    public int port() {
        return server.port();
    }

    /** Adds a stub that wins over the passthrough. */
    public MockServer stub(MappingBuilder mapping) {
        server.stubFor(mapping.atPriority(STUB_PRIORITY));
        return this;
    }

    /** Requests received (stubbed or passed through) that match {@code pattern}, oldest first. */
    public List<LoggedRequest> requests(RequestPatternBuilder pattern) {
        return server.findAll(pattern);
    }

    /** Bodies of matching requests deserialized from JSON, e.g. to assert what the app sent. */
    public <T> List<T> requestBodies(RequestPatternBuilder pattern, Class<T> type) {
        return requests(pattern).stream().map(r -> {
            try {
                return Json.MAPPER.readValue(r.getBodyAsString(), type);
            } catch (JsonProcessingException e) {
                throw new FrameworkException("Request body of " + r.getUrl() + " is not a " + type.getSimpleName(), e);
            }
        }).toList();
    }

    /** Fails with {@link PotentialDefectException} unless exactly {@code count} matching requests were made. */
    public void verify(int count, RequestPatternBuilder pattern) {
        try {
            server.verify(count, pattern);
        } catch (VerificationException e) {
            throw new PotentialDefectException("The app did not make the expected request(s): " + e.getMessage(), e);
        }
    }

    /** Removes test stubs and the request log; keeps the passthrough. Call between tests. */
    public void reset() {
        server.resetAll();
        addPassthrough();
    }

    @Override
    public void close() {
        server.stop();
    }

    /** The underlying WireMock server for anything not covered here (delays, faults, scenarios). */
    public WireMockServer wireMock() {
        return server;
    }

    private void addPassthrough() {
        if (upstream != null) {
            server.stubFor(any(anyUrl()).atPriority(PASSTHROUGH_PRIORITY)
                    .willReturn(aResponse().proxiedFrom(upstream)));
        }
    }
}
