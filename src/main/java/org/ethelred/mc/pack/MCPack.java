package org.ethelred.mc.pack;

import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
public interface MCPack {
  Map<String, MCPackFile> files();
  String name();
  MCPackType type();
  String description();
}
