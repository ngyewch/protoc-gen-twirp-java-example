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
  private RpcTwirp.TestService2 helidonProtobufService;
  private RpcTwirp.TestService2 helidonJsonService;
  private RpcTwirp.TestService2 apacheProtobufService;
  private RpcTwirp.TestService2 apacheJsonService;

  public Stream<RpcTwirp.TestService2> serviceProvider() {
    return Stream.of(
        helidonProtobufService, helidonJsonService, apacheProtobufService, apacheJsonService);
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
    System.out.println(System.getProperty("user.dir"));
    final String baseUri = String.format("http://127.0.0.1:%d/twirp", webServer.port());
    helidonProtobufService = RpcTwirp.Helidon.Client.TestService2.newProtobufClient(baseUri);
    helidonJsonService = RpcTwirp.Helidon.Client.TestService2.newJSONClient(baseUri);
    apacheProtobufService = RpcTwirp.Apache.Client.TestService2.newProtobufClient(baseUri);
    apacheJsonService = RpcTwirp.Apache.Client.TestService2.newJSONClient(baseUri);
  }

  @AfterAll
  public void afterAll() {
    webServer.shutdown().await(15, TimeUnit.SECONDS);
  }

  @ParameterizedTest(name = "[{index}] testAdd()")
  @MethodSource("serviceProvider")
  public void testAdd(RpcTwirp.TestService2 service) {
    Assertions.assertEquals(
        "HELLO, WORLD!",
        service.toUpper(ToUpperRequest.newBuilder().setText("Hello, world!").build()).getText());
  }
}
