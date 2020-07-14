package org.ethelred.leveldb;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DumpTagFile {

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: DumpTagFile <filename>");
      System.exit(0);
    }

    for (var accessor : accessors) {
      try (FileInputStream fis = new FileInputStream(args[0])) {
        if (tryRead(accessor, fis)) {
          return;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  interface ThrowingFunction<IN, OUT> {
    OUT apply(IN in) throws IOException;
  }

  static List<ThrowingFunction<InputStream, NBTInputStream>> accessors = List.of(
    NbtUtils::createReaderLE,
    NbtUtils::createNetworkReader,
    NbtUtils::createGZIPReader,
    NbtUtils::createReader
  );

  static boolean tryRead(
    ThrowingFunction<InputStream, NBTInputStream> nbtAccessor,
    InputStream delegate
  )
    throws IOException {
    NBTInputStream in = nbtAccessor.apply(delegate);
    System.out.println(Nbt2Yaml.toYamlString(in.readTag()));
    return true;
  }
}
