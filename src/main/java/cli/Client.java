package cli;

import picocli.CommandLine;

@CommandLine.Command(
    name = "client",
    subcommands = {
      ClientAdd.class,
      ClientToUpper.class,
    })
public class Client implements Runnable {
  @CommandLine.Option(
      names = "--endpoint",
      scope = CommandLine.ScopeType.INHERIT,
      defaultValue = "http://127.0.0.1:8080/twirp")
  public String endpoint;

  @CommandLine.Option(
      names = "--encoding",
      scope = CommandLine.ScopeType.INHERIT,
      defaultValue = "protobuf")
  public String encoding;

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }
}
