package org.ethelred.mc.pack;

import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
public interface MCPack {
  Set<MCPackFile> files();
  String name();
}
