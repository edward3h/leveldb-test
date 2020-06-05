package org.ethelred.leveldb;

import com.nukkitx.nbt.stream.LittleEndianDataInputStream;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;

public class BlockStorage implements Iterable<Block> {
  private static final int BLOCKS_IN_SUBCHUNK = 4096;
  List<Block> blocks;

  public BlockStorage(LittleEndianDataInputStream in) throws IOException {
    int storageVersion = in.readUnsignedByte();
    int bitsPerBlock = storageVersion >>> 1;
    int blocksPerWord = Integer.SIZE / bitsPerBlock;
    int blockWordCount =
      BLOCKS_IN_SUBCHUNK /
      blocksPerWord +
      (BLOCKS_IN_SUBCHUNK % blocksPerWord == 0 ? 0 : 1);

    int[] blockStates = new int[blockWordCount];

    for (int i = 0; i < blockStates.length; i++) {
      blockStates[i] = in.readInt();
    }

    int paletteSize = in.readInt();
    CompoundTag[] palette = new CompoundTag[paletteSize];
    try (NBTInputStream nbtIn = new NBTInputStream(in)) {
      for (int i = 0; i < palette.length; i++) {
        palette[i] = (CompoundTag) nbtIn.readTag();
      }
    }
    int bitMask = (2 << (bitsPerBlock - 1)) - 1;
    blocks = new ArrayList<>(BLOCKS_IN_SUBCHUNK);
    for (int blockState : blockStates) {
      for (
        int i = 0;
        i < blocksPerWord && blocks.size() < BLOCKS_IN_SUBCHUNK;
        i++
      ) {
        int blockIndex = blockState & bitMask;
        blocks.add(new Block(palette[blockIndex]));
        blockState = blockState >>> bitsPerBlock;
      }
    }
  }

  @Override
  public String toString() {
    return "BlockStorage []";
  }

  @Override
  public Iterator<Block> iterator() {
    return blocks.iterator();
  }

  @Override
  public Spliterator<Block> spliterator() {
    return blocks.spliterator();
  }
}
