package org.ethelred.mc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBlockPalette.class)
@JsonSerialize(as = ImmutableBlockPalette.class)
@JsonIgnoreProperties({ "block_position_data" }) // can't set these with setblock so ignore for now
public interface BlockPalette {
  @JsonProperty("block_palette")
  List<BlockState> blockStates();

  default BlockState get(int index) {
    return blockStates().get(index);
  }
}
