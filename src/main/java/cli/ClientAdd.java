package cli;

import picocli.CommandLine;
import rpc.AddRequest;
import rpc.AddResponse;
import rpc.RpcTwirp;

@CommandLine.Command(name = "add")
public class ClientAdd implements Runnable {
  @CommandLine.ParentCommand private Client client;

  @CommandLine.Parameters(arity = "2", description = "values")
  private float[] values;

  private RpcTwirp.TestService newTestService() {
    switch (client.encoding) {
      case "protobuf":
        return RpcTwirp.Helidon.Client.TestService.newProtobufClient(client.endpoint);
      case "json":
        return RpcTwirp.Helidon.Client.TestService.newJSONClient(client.endpoint);
      default:
        throw new IllegalArgumentException("unknown encoding");
    }
  }

  @Override
  public void run() {
    final RpcTwirp.TestService testService = newTestService();
    final AddRequest request = AddRequest.newBuilder().setA(values[0]).setB(values[1]).build();
    final AddResponse response = testService.add(request);
    System.out.printf("%f + %f = %f\n", request.getA(), request.getB(), response.getValue());
  }
}
