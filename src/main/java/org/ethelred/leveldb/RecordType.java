package org.ethelred.leveldb;

import com.nukkitx.nbt.stream.LittleEndianDataInputStream;
import com.nukkitx.nbt.tag.Tag;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
      Tag<?> tag = Common.readTag(value);
      into.setBlockEntity(tag);
    }
  },
  Entity(50),
  PendingTicks(51),
  BlockExtraData(52),
  BiomeState(53),
  FinalizedState(54),
  HardCodedSpawnAreas(57),
  RandomTicks(58),
  Upcoming(59),
  Version(118);

  private final byte tagValue;

  private static Map<Byte, RecordType> FROM_BYTE_MAP = Stream
    .of(values())
    .collect(Collectors.toMap(r -> r.tagValue, r -> r));

  private RecordType(int value) {
    this.tagValue = (byte) value;
  }

  void readData(Key k, byte[] value, ChunkData into) throws IOException {
    System.out.println("Don't know how to read " + name() + " :-( " + k);
  }

  public static RecordType fromByte(byte value) {
    return FROM_BYTE_MAP.getOrDefault(value, Unknown);
  }
}
