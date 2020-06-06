package org.ethelred.leveldb;

import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.Tag;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * static util class
 */
public class Common {

  private Common() {} // no instantiation

  public static Tag<?> readTag(byte[] value) throws IOException {
    try (
      NBTInputStream in = NbtUtils.createReaderLE(
        new ByteArrayInputStream(value)
      )
    ) {
      return in.readTag();
    }
  }
}
