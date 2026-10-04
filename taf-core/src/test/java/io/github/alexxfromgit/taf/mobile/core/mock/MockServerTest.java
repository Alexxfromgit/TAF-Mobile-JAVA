package io.github.alexxfromgit.taf.mobile.core.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.alexxfromgit.taf.mobile.core.failure.PotentialDefectException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The "app" here is a plain HTTP client: exactly what a mobile app does when its backend URL (reverse proxy)
 * or HTTP proxy (forward proxy) points to the mock.
 */
public class MockServerTest {

    private WireMockServer backend;

    @BeforeClass
    public void startRealBackend() {
        backend = new WireMockServer(options().dynamicPort());
        backend.start();
        backend.stubFor(get(urlPathEqualTo("/products")).willReturn(okJson("[{\"id\": 1, \"name\": \"Backpack\"}]")));
        backend.stubFor(get(urlPathEqualTo("/profile")).willReturn(okJson("{\"name\": \"Bob\"}")));
    }

    @AfterClass(alwaysRun = true)
    public void stopRealBackend() {
        backend.stop();
    }

    @Test
    public void reverseProxyPassesThroughUnlessStubbed() throws Exception {
        try (MockServer mock = MockServer.reverseProxy(backend.baseUrl())) {
            HttpClient app = HttpClient.newHttpClient();

            assertThat(send(app, mock.baseUrl() + "/products").body()).contains("Backpack");

            mock.stub(get(urlPathEqualTo("/products")).willReturn(okJson("[]")));
            assertThat(send(app, mock.baseUrl() + "/products").body()).isEqualTo("[]");
            assertThat(send(app, mock.baseUrl() + "/profile").body()).as("not stubbed: real backend").contains("Bob");

            mock.stub(get(urlPathEqualTo("/profile")).willReturn(serverError()));
            assertThat(send(app, mock.baseUrl() + "/profile").statusCode()).isEqualTo(500);

            mock.verify(2, getRequestedFor(urlPathEqualTo("/profile")));
            assertThatThrownBy(() -> mock.verify(1, getRequestedFor(urlPathEqualTo("/orders"))))
                    .isInstanceOf(PotentialDefectException.class)
                    .hasMessageContaining("did not make the expected request");

            mock.reset();
            assertThat(send(app, mock.baseUrl() + "/products").body()).as("stubs removed").contains("Backpack");
        }
    }

    @Test
    public void requestBodiesCanBeAsserted() throws Exception {
        try (MockServer mock = MockServer.reverseProxy(backend.baseUrl())) {
            mock.stub(post(urlPathEqualTo("/orders")).willReturn(okJson("{\"id\": 7}")));
            HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(mock.baseUrl() + "/orders"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"productId\": 1, \"quantity\": 2}")).build(),
                    HttpResponse.BodyHandlers.ofString());

            @SuppressWarnings("rawtypes")
            List<Map> bodies = mock.requestBodies(postRequestedFor(urlPathEqualTo("/orders")), Map.class);

            assertThat(bodies).hasSize(1);
            assertThat(bodies.get(0)).containsEntry("quantity", 2);
        }
    }

    @Test
    public void forwardProxyStubsSelectedCallsOfAnyHost() throws Exception {
        try (MockServer mock = MockServer.forwardProxy()) {
            HttpClient app = HttpClient.newBuilder()
                    .proxy(ProxySelector.of(new InetSocketAddress("localhost", mock.port())))
                    .build();

            assertThat(send(app, backend.baseUrl() + "/products").body()).contains("Backpack");

            mock.stub(get(urlPathEqualTo("/products")).willReturn(okJson("[]")));
            assertThat(send(app, backend.baseUrl() + "/products").body()).isEqualTo("[]");
            assertThat(send(app, backend.baseUrl() + "/profile").body()).contains("Bob");
        }
    }

    private static HttpResponse<String> send(HttpClient client, String url) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder(URI.create(url)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
}
