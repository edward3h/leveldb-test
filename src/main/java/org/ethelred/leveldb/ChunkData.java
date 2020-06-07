package org.ethelred.leveldb;

import com.nukkitx.nbt.tag.Tag;
import java.util.List;
import java.util.Map;

public class ChunkData {
  private final SubChunkData[] subChunkData = new SubChunkData[8];
  private int[] biomes;
  private int[] elevations;
  private List<Tag<?>> blockEntity;
  private List<Tag<?>> pendingTicks;
  private byte version;
  private int finalizedState;
  private List<Tag<?>> entities;
  private Map<Byte, Byte> biomeStates;

  public void setSubChunkData(int subChunkIndex, SubChunkData data) {
    subChunkData[subChunkIndex] = data;
  }

  public void setElevations(int[] elevation) {
    this.elevations = elevation;
  }

  public void setBiomes(int[] biomes) {
    this.biomes = biomes;
  }

  public void setBlockEntity(List<Tag<?>> tag) {
    this.blockEntity = tag;
  }

  public void setPendingTicks(List<Tag<?>> tag) {
    this.pendingTicks = tag;
  }

  public void setVersion(byte b) {
    this.version = b;
  }

  public void setFinalizedState(int value) {
    this.finalizedState = value;
  }

  public void setEntity(List<Tag<?>> tag) {
    this.entities = tag;
  }

  public void setBiomeStates(Map<Byte, Byte> biomeStates) {
    this.biomeStates = biomeStates;
  }
}
