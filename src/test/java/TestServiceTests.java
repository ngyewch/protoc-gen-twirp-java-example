import io.helidon.webclient.WebClientException;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import rpc.AddRequest;
import rpc.DoSomethingRequest;
import rpc.RpcTwirp;
import server.TestService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestServiceTests {

  private WebServer webServer;
  private RpcTwirp.TestService protobufService;
  private RpcTwirp.TestService jsonService;

  public Stream<RpcTwirp.TestService> serviceProvider() {
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
                          RpcTwirp.Helidon.Server.TestService.update(rules, new TestService());
                        })
                    .build())
            .build();
    webServer.start().await(15, TimeUnit.SECONDS);
    final String baseUri = String.format("http://127.0.0.1:%d/twirp", webServer.port());
    protobufService = RpcTwirp.Helidon.Client.TestService.newProtobufClient(baseUri);
    jsonService = RpcTwirp.Helidon.Client.TestService.newJSONClient(baseUri);
  }

  @AfterAll
  public void afterAll() {
    webServer.shutdown().await(15, TimeUnit.SECONDS);
  }

  @ParameterizedTest
  @MethodSource("serviceProvider")
  public void testAdd(RpcTwirp.TestService service) {
    Assertions.assertEquals(
        14f, service.add(AddRequest.newBuilder().setA(9f).setB(5f).build()).getValue());
  }

  @ParameterizedTest
  @MethodSource("serviceProvider")
  public void testDoSomething(RpcTwirp.TestService service) {
    try {
      service.doSomething(
          DoSomethingRequest.newBuilder()
              .setThrowException(true)
              .setExceptionMessage("Hello, world!")
              .build());
      Assertions.fail("exception expected");
    } catch (RuntimeException e) {
      Assertions.assertNotNull(e.getCause());
      Throwable t = e.getCause();
      Assertions.assertEquals(WebClientException.class, t.getClass());
    } catch (Exception e) {
      Assertions.fail("RuntimeException expected");
    }
  }
}
