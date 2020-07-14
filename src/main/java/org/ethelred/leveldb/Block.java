package org.ethelred.leveldb;

import com.nukkitx.nbt.NbtMap;
import java.util.Map;

public class Block {
  private final NbtMap tag;

  public Block(NbtMap tag) {
    this.tag = tag;
  }

  public String getName() {
    String baseName = tag.getString("name");
    Map<String, Object> states = tag.getCompound("states");
    for (Map.Entry<String, Object> e : states.entrySet()) {
      if (e.getKey().endsWith("_type")) {
        return baseName + ":" + e.getValue();
      }
    }
    return baseName;
  }

  public String toString() {
    return Nbt2Yaml.toYamlString(tag);
  }
}
