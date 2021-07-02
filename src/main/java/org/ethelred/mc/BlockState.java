package org.ethelred.mc;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBlockState.class)
@JsonSerialize(as = ImmutableBlockState.class)
public interface BlockState {
    String name();
    Map<String, Object> states();
    int version();
}
