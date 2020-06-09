package org.ethelred.leveldb;

import com.nukkitx.nbt.tag.Tag;
import java.util.List;
import java.util.Map;

public class ChunkData {
  private final SubChunkData[] subChunkData = new SubChunkData[8];
  private int[] biomes;
  private int[] elevations;
  private List<Tag<?>> blockEntity = List.of();
  private List<Tag<?>> pendingTicks = List.of();
  private byte version;
  private int finalizedState;
  private List<Tag<?>> entities = List.of();
  private Map<Byte, Byte> biomeStates = Map.of();

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

  public void setBiomeStates(Map<Byte, Byte> biomeStates) {
    this.biomeStates = biomeStates;
  }

  public int[] getBiomes() {
    return biomes;
  }

  public Map<Byte, Byte> getBiomeStates() {
    return biomeStates;
  }

  public SubChunkData[] getSubChunkData() {
    return subChunkData;
  }

  public int[] getElevations() {
    return elevations;
  }

  public List<Tag<?>> getBlockEntity() {
    return blockEntity;
  }

  public List<Tag<?>> getPendingTicks() {
    return pendingTicks;
  }

  public byte getVersion() {
    return version;
  }

  public int getFinalizedState() {
    return finalizedState;
  }

  public List<Tag<?>> getEntities() {
    return entities;
  }

  public void setEntities(List<Tag<?>> entities) {
    this.entities = entities;
  }
}
