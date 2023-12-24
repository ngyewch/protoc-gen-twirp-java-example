import cli.Client;
import cli.Server;
import picocli.CommandLine;

@CommandLine.Command(
    subcommands = {
      Client.class,
      Server.class,
    })
public class Main implements Runnable {
  public static void main(String[] args) throws Exception {
    final int exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }
}
