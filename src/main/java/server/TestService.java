package server;

import rpc.*;

public class TestService implements RpcTwirp.TestService {

  @Override
  public AddResponse add(AddRequest input) {
    return AddResponse.newBuilder().setValue(input.getA() + input.getB()).build();
  }

  @Override
  public DoSomethingResponse doSomething(DoSomethingRequest input) {
    if (input.getThrowException()) {
      throw new RuntimeException(input.getExceptionMessage());
    }
    return DoSomethingResponse.newBuilder().build();
  }
}
