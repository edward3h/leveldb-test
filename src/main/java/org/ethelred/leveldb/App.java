/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.ethelred.leveldb;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.stream.IntStream;
import org.ethelred.args4jboilerplate.Args4jBoilerplate;
import org.ethelred.mc.StructureConverter;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.javatuples.KeyValue;
import org.kohsuke.args4j.Option;

public class App extends Args4jBoilerplate {
  @Option(name = "--db", required = true)
  File db;

  @Option(name = "--special", usage = "output special string keys and values")
  boolean dumpSpecial = false;

  @Option(
    name = "--biomeIds",
    usage = "dump biome IDs (trying to figure out what they mean)"
  )
  boolean dumpBiomeIds = false;

  @Option(name = "--blockNames", usage = "summary of block types")
  boolean dumpBlockNames = false;

  @Option(
    name = "--blockEntities",
    usage = "dump block entities (trying to figure out what they mean)"
  )
  boolean dumpBlockEntities = false;

  @Option(
    name = "--entities",
    usage = "dump entities (trying to figure out what they mean)"
  )
  boolean dumpEntities = false;

  @Option(
    name = "--elevations",
    usage = "dump elevations (trying to figure out what they mean)"
  )
  boolean dumpElevations = false;

  @Option(name = "--grid", usage = "something")
  boolean dumpGrid = false;

  @Option(
    name = "--structure",
    aliases = { "--structures" },
    usage = "Dump structures"
  )
  boolean dumpStructures = false;

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
      Map<ChunkKey, ChunkData> chunks = new HashMap<>();
      Map<String, Object> general = new HashMap<>();
      Map<String, byte[]> generalRaw = new HashMap<>();
      for (var e : ldb) {
        Key k = new Key(e.getKey());
        ChunkKey chunkKey = k.getChunkKey();
        if (k.isSpecial()) {
          general.put(k.getSpecialKey(), Common.readTag(e.getValue()));
          generalRaw.put(k.getSpecialKey(), e.getValue());
        } else if (chunkKey != null) {
          ChunkData chunkData = chunks.computeIfAbsent(
            k.getChunkKey(),
            x -> new ChunkData()
          );
          k.getRecordType().readData(k, e.getValue(), chunkData);
        }
      }

      if (dumpSpecial) {
        general.forEach(
          (k, v) -> System.out.printf("%s => %s%n", k, Nbt2Yaml.toYamlString(v))
        );
      }
      if (dumpBiomeIds) {
        _dumpBiomeIds(chunks);
      }
      if (dumpBlockNames) {
        _dumpBlockNames(chunks);
      }
      if (dumpBlockEntities) {
        _dumpBlockEntities(chunks);
      }
      if (dumpEntities) {
        _dumpEntities(chunks);
      }
      if (dumpElevations) {
        _dumpElevations(chunks);
      }
      if (dumpGrid) {
        _dumpGrid(chunks);
      }
      if (dumpStructures) {
        _dumpStructures(generalRaw);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Make sure you close the db to shutdown the
      // database and avoid resource leaks.
      ldb.close();
    }

    return true;
  }

  private void _dumpStructures(Map<String, byte[]> raw) throws IOException {
    StructureConverter.convert(
      raw
        .entrySet()
        .stream()
        .filter(e -> e.getKey().startsWith("structure"))
        .map(e -> KeyValue.with(e.getKey(), e.getValue()))
    );
  }

  private void _dumpGrid(Map<ChunkKey, ChunkData> chunks) {
    IntSummaryStatistics xStats = chunks
      .keySet()
      .stream()
      .mapToInt(k -> k.getX())
      .summaryStatistics();
    IntSummaryStatistics zStats = chunks
      .keySet()
      .stream()
      .mapToInt(k -> k.getZ())
      .summaryStatistics();
    for (int z = zStats.getMin(); z <= zStats.getMax(); z++) {
      for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
        String rep = " ";
        ChunkData chunk = chunks.get(new ChunkKey(x, z));
        if (chunk != null) {
          rep = "O";
        }
        System.out.print(rep);
      }
      System.out.println();
    }
  }

  private void _dumpBlockNames(Map<ChunkKey, ChunkData> chunks) {
    Common.dumpThingByCount(
      chunks
        .values()
        .stream()
        .flatMap(chunk -> chunk.getBlocks().map(Block::toString))
    );
  }

  private void _dumpBlockEntities(Map<ChunkKey, ChunkData> chunks) {
    Common.dumpThingByCount(
      chunks
        .values()
        .stream()
        .flatMap(chunk -> chunk.getBlockEntity().stream()),
      Nbt2Yaml::toYamlString
    );
  }

  private void _dumpEntities(Map<ChunkKey, ChunkData> chunks) {
    Common.dumpThingByCount(
      chunks.values().stream().flatMap(chunk -> chunk.getEntities().stream()),
      Nbt2Yaml::toYamlString
    );
  }

  private void _dumpBiomeIds(Map<ChunkKey, ChunkData> chunks) {
    Common.dumpThingByCount(
      chunks
        .values()
        .stream()
        .flatMap(chunk -> IntStream.of(chunk.getBiomes()).mapToObj(x -> x))
    );
    System.out.println();
    Common.dumpThingByCount(
      chunks
        .values()
        .stream()
        .flatMap(chunk -> chunk.getBiomeStates().keySet().stream())
    );
  }

  private void _dumpElevations(Map<ChunkKey, ChunkData> chunks) {
    Common.dumpThingByCount(
      chunks
        .values()
        .stream()
        .flatMap(
          chunk ->
            IntStream
              .of(chunk.getElevations())
              .average()
              .stream()
              .mapToObj(x -> Math.round(x))
        )
    );
  }
}
