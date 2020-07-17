package org.ethelred.mc;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableStructure.class)
@JsonSerialize(as = ImmutableStructure.class)
public interface Structure {
  int format_version();
  Coordinates size();
  StructureDetail structure();
  Coordinates structure_world_origin();
}
