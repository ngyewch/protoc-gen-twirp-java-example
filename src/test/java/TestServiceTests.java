import io.github.ngyewch.twirp.TwirpException;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
  private Process goServerProcess;
  private List<RpcTwirp.TestService> serviceList;

  public Stream<RpcTwirp.TestService> serviceProvider() {
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
                          RpcTwirp.Helidon.Server.TestService.update(rules, new TestService());
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
      serviceList.add(RpcTwirp.Helidon.Client.TestService.newProtobufClient(baseUri));
      serviceList.add(RpcTwirp.Helidon.Client.TestService.newJSONClient(baseUri));
      serviceList.add(RpcTwirp.Apache.Client.TestService.newProtobufClient(baseUri));
      serviceList.add(RpcTwirp.Apache.Client.TestService.newJSONClient(baseUri));
    }
  }

  @AfterAll
  public void afterAll() {
    webServer.shutdown().await(15, TimeUnit.SECONDS);
    goServerProcess.destroyForcibly();
  }

  @ParameterizedTest(name = "[{index}] testAdd()")
  @MethodSource("serviceProvider")
  public void testAdd(RpcTwirp.TestService service) {
    Assertions.assertEquals(
        14f, service.add(AddRequest.newBuilder().setA(9f).setB(5f).build()).getValue());
  }

  @ParameterizedTest(name = "[{index}] testDoSomething()")
  @MethodSource("serviceProvider")
  public void testDoSomething(RpcTwirp.TestService service) {
    Assertions.assertThrows(
        TwirpException.class,
        () ->
            service.doSomething(
                DoSomethingRequest.newBuilder()
                    .setThrowException(true)
                    .setExceptionMessage("Hello, world!")
                    .build()));
  }
}
