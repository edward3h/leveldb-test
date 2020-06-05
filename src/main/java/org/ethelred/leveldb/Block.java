package org.ethelred.leveldb;

import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.Tag;
import java.util.Map;

public class Block {
  private final CompoundTag tag;

  public Block(CompoundTag tag) {
    this.tag = tag;
  }

  public String getName() {
    String baseName = tag.getString("name");
    Map<String, Tag<?>> states = tag.getCompound("states").getValue();
    for (Map.Entry<String, Tag<?>> e : states.entrySet()) {
      if (e.getKey().endsWith("_type")) {
        return baseName + ":" + e.getValue().getValue();
      }
    }
    return baseName;
  }

  public String toString() {
    return String.valueOf(tag);
  }
}
