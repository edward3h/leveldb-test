package org.ethelred.mc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class Coordinates {
  private int x;
  private int y;
  private int z;

  @JsonCreator
  public Coordinates(int[] value) {
    if (value.length != 3) {
      throw new IllegalArgumentException();
    }
    this.x = value[0];
    this.y = value[1];
    this.z = value[2];
  }
}