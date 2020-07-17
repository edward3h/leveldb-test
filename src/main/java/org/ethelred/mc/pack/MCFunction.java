package org.ethelred.mc.pack;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
public interface MCFunction extends MCPackFile {
  String name();
  List<String> commands();

  @Override
  default String getRelativePath() {
    return "functions/" + name() + ".mcfunction";
  }

  @Override
  default void writeTo(OutputStream out) throws IOException {
    PrintStream p = new PrintStream(out, false, StandardCharsets.UTF_8);
    commands().forEach(p::println);
  }
}
