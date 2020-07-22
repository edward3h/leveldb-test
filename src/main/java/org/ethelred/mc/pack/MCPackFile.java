package org.ethelred.mc.pack;

import java.io.IOException;
import java.io.OutputStream;

public interface MCPackFile {
  String getRelativePath();
  void writeTo(OutputStream out) throws IOException;

  default boolean isBinary() {
    return false;
  }
}
