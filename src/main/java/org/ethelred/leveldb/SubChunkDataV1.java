package org.ethelred.leveldb;

import com.nukkitx.nbt.util.stream.LittleEndianDataInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;

public class SubChunkDataV1 implements SubChunkData {
    private byte version;
    private byte storageCount;
    private List<BlockStorage> blockStorage = new ArrayList<>();

    public SubChunkDataV1(byte[] value) {
        try (
            LittleEndianDataInputStream in = new LittleEndianDataInputStream(
                new ByteArrayInputStream(value)
            )
        ) {
            version = in.readByte();
            if (version == 1) {
                storageCount = 1;
            } else {
                storageCount = in.readByte();
            }
            for (int i = 0; i < storageCount; i++) {
                blockStorage.add(new BlockStorage(in));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String toString() {
        return (
            "SubChunkDataV1 [\n" +
            blockStorage
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n")) +
            "\n]"
        );
    }

    @Override
    public Iterator<Block> iterator() {
        if (blockStorage.isEmpty()) {
            return Collections.emptyIterator();
        }
        return blockStorage.get(0).iterator();
    }

    @Override
    public Spliterator<Block> spliterator() {
        if (blockStorage.isEmpty()) {
            return Spliterators.emptySpliterator();
        }
        return blockStorage.get(0).spliterator();
    }
}
