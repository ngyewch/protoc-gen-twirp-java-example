import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import rpc.RpcTwirp;
import rpc2.ToUpperRequest;
import server.TestService2;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestService2Tests {
  private WebServer webServer;
  private Process goServerProcess;
  private List<RpcTwirp.TestService2> serviceList;

  public Stream<RpcTwirp.TestService2> serviceProvider() {
    return serviceList.stream();
  }

  @BeforeAll
  public void beforeAll() {
    webServer =
        WebServer.builder()
            .routing(
                Routing.builder()
                    .register(
                        "/twirp",
                        rules -> {
                          RpcTwirp.Helidon.Server.TestService2.update(rules, new TestService2());
                        })
                    .build())
            .build();
    webServer.start().await(15, TimeUnit.SECONDS);

    goServerProcess = ReferenceImplementation.runGoServer();

    final List<String> baseUris =
        Arrays.asList(
            String.format("http://127.0.0.1:%d/twirp", webServer.port()),
            "http://127.0.0.1:18080/twirp");

    serviceList = new ArrayList<>();
    for (final String baseUri : baseUris) {
      serviceList.add(RpcTwirp.Helidon.Client.TestService2.newProtobufClient(baseUri));
      serviceList.add(RpcTwirp.Helidon.Client.TestService2.newJSONClient(baseUri));
      serviceList.add(RpcTwirp.Apache.Client.TestService2.newProtobufClient(baseUri));
      serviceList.add(RpcTwirp.Apache.Client.TestService2.newJSONClient(baseUri));
    }
  }

  @AfterAll
  public void afterAll() {
    webServer.shutdown().await(15, TimeUnit.SECONDS);
    goServerProcess.destroyForcibly();
  }

  @ParameterizedTest(name = "[{index}] testToUpper()")
  @MethodSource("serviceProvider")
  public void testToUpper(RpcTwirp.TestService2 service) {
    Assertions.assertEquals(
        "HELLO, WORLD!",
        service.toUpper(ToUpperRequest.newBuilder().setText("Hello, world!").build()).getText());
  }
}
