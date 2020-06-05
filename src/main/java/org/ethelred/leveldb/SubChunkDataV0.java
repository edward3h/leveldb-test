package org.ethelred.leveldb;

import java.util.Iterator;

public class SubChunkDataV0 implements SubChunkData {

  public SubChunkDataV0(byte[] value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Block> iterator() {
    throw new UnsupportedOperationException();
  }
}
