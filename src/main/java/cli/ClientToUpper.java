package cli;

import picocli.CommandLine;
import rpc.RpcTwirp;
import rpc2.ToUpperRequest;
import rpc2.ToUpperResponse;

@CommandLine.Command(name = "toUpper")
public class ClientToUpper implements Runnable {
  @CommandLine.ParentCommand private Client client;

  @CommandLine.Parameters(arity = "1", description = "text")
  private String text;

  private RpcTwirp.TestService2 newTestService2() {
    switch (client.encoding) {
      case "protobuf":
        return RpcTwirp.Helidon.Client.TestService2.newProtobufClient(client.endpoint);
      case "json":
        return RpcTwirp.Helidon.Client.TestService2.newJSONClient(client.endpoint);
      default:
        throw new IllegalArgumentException("unknown encoding");
    }
  }

  @Override
  public void run() {
    final RpcTwirp.TestService2 testService2 = newTestService2();
    final ToUpperRequest request = ToUpperRequest.newBuilder().setText(text).build();
    final ToUpperResponse response = testService2.toUpper(request);
    System.out.printf("%s -> %s\n", request.getText(), response.getText());
  }
}
