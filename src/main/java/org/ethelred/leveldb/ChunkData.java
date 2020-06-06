package org.ethelred.leveldb;

import com.nukkitx.nbt.tag.Tag;

public class ChunkData {
  private final SubChunkData[] subChunkData = new SubChunkData[8];
  private int[] biomes;
  private int[] elevations;
  private Tag<?> blockEntity;

  public void setSubChunkData(int subChunkIndex, SubChunkData data) {
    subChunkData[subChunkIndex] = data;
  }

  public void setElevations(int[] elevation) {
    this.elevations = elevation;
  }

  public void setBiomes(int[] biomes) {
    this.biomes = biomes;
  }

  public void setBlockEntity(Tag<?> tag) {
    this.blockEntity = tag;
  }
}
