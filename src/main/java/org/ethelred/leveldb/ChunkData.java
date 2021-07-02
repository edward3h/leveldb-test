package org.ethelred.leveldb;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ChunkData {

    private final SubChunkData[] subChunkData = new SubChunkData[16];
    private int[] biomes;
    private int[] elevations;
    private List<Object> blockEntity = List.of();
    private List<Object> pendingTicks = List.of();
    private byte version;
    private int finalizedState;
    private List<Object> entities = List.of();
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

    public void setBlockEntity(List<Object> tag) {
        this.blockEntity = tag;
    }

    public void setPendingTicks(List<Object> tag) {
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

    public List<Object> getBlockEntity() {
        return blockEntity;
    }

    public List<Object> getPendingTicks() {
        return pendingTicks;
    }

    public byte getVersion() {
        return version;
    }

    public int getFinalizedState() {
        return finalizedState;
    }

    public List<Object> getEntities() {
        return entities;
    }

    public void setEntities(List<Object> entities) {
        this.entities = entities;
    }

    public Stream<Block> getBlocks() {
        return Stream
            .of(subChunkData)
            .filter(Objects::nonNull)
            .flatMap(s -> StreamSupport.stream(s.spliterator(), false));
    }
}
