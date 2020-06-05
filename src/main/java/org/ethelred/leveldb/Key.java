package org.ethelred.leveldb;

import com.google.common.io.LittleEndianDataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Key {
  private int x;
  private int z;

  @SuppressWarnings("unused")
  private int dimension;

  private RecordType recordType;
  private byte subchunkIndex;
  private String special;

  public Key(byte[] bytes) {
    special = new String(bytes);
    try (
      LittleEndianDataInputStream in = new LittleEndianDataInputStream(
        new ByteArrayInputStream(bytes)
      )
    ) {
      x = in.readInt();
      z = in.readInt();
      if (bytes.length > 10) {
        dimension = in.readInt();
      }
      recordType = RecordType.fromByte(in.readByte());
      if (bytes.length == 10 || bytes.length == 14) {
        subchunkIndex = in.readByte();
      }
    } catch (IOException e) {
      // ignore
    }
  }

  @Override
  public String toString() {
    if (recordType == RecordType.Unknown) {
      return "Key [" + special + "]";
    }
    return (
      "Key [" + x + ", " + z + ", " + subchunkIndex + ", " + recordType + "]"
    );
  }

  public boolean isSpecial() {
    return recordType == RecordType.Unknown;
  }

  public RecordType getRecordType() {
    return recordType;
  }

  public int getX() {
    return x;
  }

  public int getZ() {
    return z;
  }

  public byte getSubchunkIndex() {
    return subchunkIndex;
  }
}
