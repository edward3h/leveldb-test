package org.ethelred.leveldb;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ref: https://minecraft.gamepedia.com/Bedrock_Edition_level_format
 */
public enum RecordType {
  Unknown(-1),
  Data2D(45),
  Data2DLegacy(46),
  SubChunkPrefix(47),
  LegacyTerrain(48),
  BlockEntity(49),
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

  public static RecordType fromByte(byte value) {
    return FROM_BYTE_MAP.getOrDefault(value, Unknown);
  }
}
