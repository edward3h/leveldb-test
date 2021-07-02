package org.ethelred.mc.pack;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MCPackType {
    @JsonProperty("resources")
    RESOURCE,
    @JsonProperty("data")
    BEHAVIOR,
    @JsonProperty("skin_pack")
    SKIN,
}
