/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.ethelred.leveldb;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.Tag;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.ethelred.args4jboilerplate.Args4jBoilerplate;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.kohsuke.args4j.Option;

public class App extends Args4jBoilerplate {
  @Option(name = "--db", required = true)
  File db;

  public App(String[] args) {
    super.parseArgs(args);
  }

  public static void main(String[] args) throws IOException {
    new App(args).run();
  }

  private boolean run() throws IOException {
    Options options = new Options();
    options.createIfMissing(true);
    DB ldb = factory.open(db, options);
    try {
      Multiset<String> blockNames = HashMultiset.create();
      for (var e : ldb) {
        Key k = new Key(e.getKey());
        // if (k.isSpecial())
        // {
        //     System.out.println(k);
        //     Tag<?> root = _readTag(e.getValue());
        //     System.out.println(root);

        //     System.out.println();
        // }
        if (k.getRecordType() == RecordType.SubChunkPrefix) {
          SubChunkData value = SubChunkData.read(e.getValue());
          value.forEach(block -> blockNames.add(block.getName()));
        }
      }
      Multisets
        .copyHighestCountFirst(blockNames)
        .forEachEntry(
          (name, count) -> System.out.printf("%12d => %s%n", count, name)
        );
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Make sure you close the db to shutdown the
      // database and avoid resource leaks.
      ldb.close();
    }

    return true;
  }

  private Tag<?> _readTag(byte[] value) throws IOException {
    try (
      NBTInputStream in = NbtUtils.createReaderLE(
        new ByteArrayInputStream(value)
      )
    ) {
      return in.readTag();
    }
  }
}
