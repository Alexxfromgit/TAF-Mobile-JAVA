# Network mocking

UI tests against a real backend are realistic but hard to steer: how do you test the "empty catalog" state, a
500 error, a slow response or an expired session? `MockServer` sits between the app and its backend. It passes
everything through, except what a test stubs, and records what the app sent.

```java
try (MockServer mock = MockServer.reverseProxy("https://api.example.com")) {
    // launch the app with its API base URL pointing to mock.baseUrl()
    mock.stub(get(urlPathEqualTo("/products")).willReturn(okJson("[]")));

    on(CatalogScreen.class).verifyEmptyState();

    mock.verify(1, getRequestedFor(urlPathEqualTo("/products")));
    List<Order> sent = mock.requestBodies(postRequestedFor(urlPathEqualTo("/orders")), Order.class);
}
```

Stubs always win over the passthrough. `mock.reset()` removes the stubs and the request log between tests.
`mock.wireMock()` exposes delays, faults and scenarios.

## Connecting the app

### Reverse proxy (recommended)

The app calls `mock.baseUrl()` instead of its real backend, and unmatched calls are forwarded to `upstream`.
You need a way to change the app's API base URL in test builds, for example:

- a launch argument or environment variable read by debug builds (`caps.android.optionalIntentArguments`,
  `caps.ios.processArguments`);
- a build flavour or scheme for UI tests;
- a hidden debug settings screen.

From the Android emulator, the host machine is `10.0.2.2` (`http://10.0.2.2:<port>`), or run
`adb reverse tcp:<port> tcp:<port>` and use `localhost`. iOS simulators share the host's network, so `localhost`
works. For cleartext HTTP on Android 9+, allow it in the debug build's `network_security_config`.

### Forward proxy

```java
MockServer mock = MockServer.forwardProxy();
// Android: adb shell settings put global http_proxy 10.0.2.2:<port>
```

The device sends all HTTP traffic through the mock, and unmatched requests go to their original host. HTTPS
interception requires the app to trust WireMock's CA certificate (on Android 7+, a debug-only
`network_security_config` that trusts user CAs). Prefer the reverse proxy when you control the build.

## About the demo app

The Sauce Labs demo app keeps its catalog locally and makes no backend calls worth mocking. That's why the
mocking module is demonstrated by its own device-free tests (`MockServerTest`: passthrough, stubs, request
assertions, both proxy modes) rather than by UI examples. Point it at your app's backend as described above.
