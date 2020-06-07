package org.ethelred.leveldb;

import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.Tag;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

  public static List<Tag<?>> readTagList(byte[] value) throws IOException {
    List<Tag<?>> result = new ArrayList<>();
    try (
      NBTInputStream in = NbtUtils.createReaderLE(
        new ByteArrayInputStream(value)
      )
    ) {
      while (true) {
        result.add(in.readTag());
      }
    } catch (IOException e) {
      // ignore - doesn't seem to be any other way to detect running out of bytes
    }
    return result;
  }

  public static int readInt(byte[] value) {
    return value[0] & (value[1] << 8) & (value[2] << 16) & (value[3] << 24);
  }
}
