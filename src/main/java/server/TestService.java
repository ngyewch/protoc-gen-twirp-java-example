package server;

import rpc.AddRequest;
import rpc.AddResponse;
import rpc.RpcTwirp;

public class TestService implements RpcTwirp.TestService {

  @Override
  public AddResponse add(AddRequest input) {
    return AddResponse.newBuilder().setValue(input.getA() + input.getB()).build();
  }
}
