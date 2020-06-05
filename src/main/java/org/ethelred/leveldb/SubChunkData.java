package org.ethelred.leveldb;

public interface SubChunkData extends Iterable<Block> {
  static SubChunkData read(byte[] value) {
    byte version = value[0];
    switch (version) {
      case 1:
      case 8:
        return new SubChunkDataV1(value);
      default:
        return new SubChunkDataV0(value);
    }
  }
}
