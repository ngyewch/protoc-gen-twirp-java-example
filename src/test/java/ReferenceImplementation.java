import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ReferenceImplementation {
  public static Process runGoServer() {
    try {
      doBuildGoServer();

      final File directory = new File("go");
      final ProcessBuilder processBuilder = new ProcessBuilder("build/twirp-playground");
      processBuilder.directory(directory);
      return processBuilder.start();
    } catch (RuntimeException e) {
      throw e;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  private static void doBuildGoServer() throws IOException, InterruptedException {
    final Map<String, String> miseEnvMap = getMiseEnv("go");
    final File directory = new File("go");
    final ProcessBuilder processBuilder = new ProcessBuilder("task", "build");
    processBuilder.directory(directory);
    processBuilder.environment().putAll(miseEnvMap);
    final Process process = processBuilder.start();
    process.waitFor();
  }

  private static Map<String, String> getMiseEnv(String directory)
      throws IOException, InterruptedException {
    final File tmpFile = File.createTempFile("mise-env-", ".json");
    try {
      final ProcessBuilder processBuilder =
          new ProcessBuilder("mise", "env", "--json", "--cd", directory);
      processBuilder.redirectOutput(tmpFile);
      final Process process = processBuilder.start();
      process.waitFor();

      final ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(tmpFile, new TypeReference<>() {});
    } finally {
      tmpFile.delete();
    }
  }
}
