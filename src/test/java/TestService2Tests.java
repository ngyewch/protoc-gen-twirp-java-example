import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
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
  private RpcTwirp.TestService2 protobufService;
  private RpcTwirp.TestService2 jsonService;

  public Stream<RpcTwirp.TestService2> serviceProvider() {
    return Stream.of(protobufService, jsonService);
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
    final String baseUri = String.format("http://127.0.0.1:%d/twirp", webServer.port());
    protobufService = RpcTwirp.Helidon.Client.TestService2.newProtobufClient(baseUri);
    jsonService = RpcTwirp.Helidon.Client.TestService2.newJSONClient(baseUri);
  }

  @AfterAll
  public void afterAll() {
    webServer.shutdown().await(15, TimeUnit.SECONDS);
  }

  @ParameterizedTest
  @MethodSource("serviceProvider")
  public void testAdd(RpcTwirp.TestService2 service) {
    Assertions.assertEquals(
        "HELLO, WORLD!",
        service.toUpper(ToUpperRequest.newBuilder().setText("Hello, world!").build()).getText());
  }
}
