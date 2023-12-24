package server;

import rpc.RpcTwirp;
import rpc2.ToUpperRequest;
import rpc2.ToUpperResponse;

public class TestService2 implements RpcTwirp.TestService2 {

  @Override
  public ToUpperResponse toUpper(ToUpperRequest input) {
    return ToUpperResponse.newBuilder().setText(input.getText().toUpperCase()).build();
  }
}
