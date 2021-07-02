package org.ethelred.mc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableStructureDetail.class)
@JsonSerialize(as = ImmutableStructureDetail.class)
@JsonIgnoreProperties(value = { "entities" })
public interface StructureDetail {
    List<List<Integer>> block_indices();

    @JsonProperty("palette")
    NestedBlockPalette nestedPalette();

    default BlockPalette palette() {
        return nestedPalette().defaultPalette;
    }

    static class NestedBlockPalette {

        @JsonProperty("default")
        private BlockPalette defaultPalette;
    }
}
