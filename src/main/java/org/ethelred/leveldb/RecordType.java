package org.ethelred.leveldb;

import com.nukkitx.nbt.util.stream.LittleEndianDataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ref: https://minecraft.gamepedia.com/Bedrock_Edition_level_format
 */
public enum RecordType {
  Unknown(-1),
  Data2D(45) {

    @Override
    void readData(Key k, byte[] value, ChunkData into) throws IOException {
      try (
        LittleEndianDataInputStream in = new LittleEndianDataInputStream(
          new ByteArrayInputStream(value)
        )
      ) {
        int[] elevation = new int[256];
        int[] biomes = new int[256];
        for (int i = 0; i < 256; i++) {
          elevation[i] = in.readUnsignedShort();
        }
        for (int i = 0; i < 256; i++) {
          biomes[i] = in.readUnsignedByte();
        }
        into.setElevations(elevation);
        into.setBiomes(biomes);
      }
    }
  },
  Data2DLegacy(46),
  SubChunkPrefix(47) {

    @Override
    void readData(Key k, byte[] value, ChunkData into) {
      SubChunkData subChunkData = SubChunkData.read(value);
      into.setSubChunkData(k.getSubchunkIndex(), subChunkData);
    }
  },
  LegacyTerrain(48),
  BlockEntity(49) {

    @Override
    void readData(Key k, byte[] value, ChunkData into) throws IOException {
      List<Object> tag = Common.readTagList(value);
      into.setBlockEntity(tag);
    }
  },
  Entity(50) {

    @Override
    void readData(Key k, byte[] value, ChunkData into) throws IOException {
      List<Object> tag = Common.readTagList(value);
      into.setEntities(tag);
    }
  },
  PendingTicks(51) {

    @Override
    void readData(Key k, byte[] value, ChunkData into) throws IOException {
      List<Object> tag = Common.readTagList(value);
      into.setPendingTicks(tag);
    }
  },
  BlockExtraData(52),
  BiomeState(53) {

    @Override
    void readData(Key k, byte[] value, ChunkData into) throws IOException {
      // this is a guess based on what the data looked like
      Map<Byte, Byte> biomeStates = new HashMap<>();
      int length = value[0];
      for (int i = 1; i < length * 2 + 1; i += 2) {
        biomeStates.put(value[i], value[i + 1]);
      }
      into.setBiomeStates(biomeStates);
    }
  },
  FinalizedState(54) {

    @Override
    void readData(Key k, byte[] value, ChunkData into) throws IOException {
      into.setFinalizedState(Common.readIntLE(value));
    }
  },
  HardCodedSpawnAreas(57),
  RandomTicks(58),
  Checksums(59),
  Version(118) {

    @Override
    void readData(Key k, byte[] value, ChunkData into) throws IOException {
      into.setVersion(value[0]);
    }
  };

  private final byte tagValue;

  private static Map<Byte, RecordType> FROM_BYTE_MAP = Stream
    .of(values())
    .collect(Collectors.toMap(r -> r.tagValue, r -> r));

  private RecordType(int value) {
    this.tagValue = (byte) value;
  }

  void readData(Key k, byte[] value, ChunkData into) throws IOException {
    System.err.println("Don't know how to read " + name() + " :-( " + k);
  }

  public static RecordType fromByte(byte value) {
    return FROM_BYTE_MAP.getOrDefault(value, Unknown);
  }
}
