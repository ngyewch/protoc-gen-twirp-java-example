package cli;

import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import java.util.concurrent.TimeUnit;
import picocli.CommandLine;
import rpc.RpcTwirp;
import server.TestService;
import server.TestService2;

@CommandLine.Command(name = "server")
public class Server implements Runnable {
  @Override
  public void run() {
    final WebServer webServer =
        WebServer.builder()
            .port(8080)
            .routing(
                Routing.builder()
                    .register(
                        "/twirp",
                        rules -> {
                          RpcTwirp.Helidon.Server.TestService.update(rules, new TestService());
                          RpcTwirp.Helidon.Server.TestService2.update(rules, new TestService2());
                        })
                    .build())
            .build();
    webServer.start().await(15, TimeUnit.SECONDS);
    System.out.printf("Server started at: http://localhost:%d\n", webServer.port());
    while (true) {
      try {
        Thread.sleep(15000);
      } catch (InterruptedException e) {
        break;
      }
    }
  }
}
