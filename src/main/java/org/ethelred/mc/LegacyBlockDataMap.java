package org.ethelred.mc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.javatuples.Pair;

public class LegacyBlockDataMap {

    private static final String RESOURCE_NAME =
        "/data/legacy_block_data_map.json";

    private Map<String, List<Map<String, Object>>> data;

    private Map<String, List<Map<String, Object>>> _loadData()
        throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream in = getClass().getResourceAsStream(RESOURCE_NAME);
        return mapper.readValue(
            in,
            new TypeReference<Map<String, List<Map<String, Object>>>>() {}
        );
    }

    public LegacyBlockDataMap()
        throws JsonParseException, JsonMappingException, IOException {
        data = _loadData();
    }

    public Pair<String, Integer> convertBlockState(BlockState bs) {
        List<Map<String, Object>> blockConversions = data.get(bs.name());
        _debug(bs.name(), blockConversions);
        _debug(bs);
        if (blockConversions == null) {
            return Pair.with(bs.name(), 0);
        }
        for (int i = 0; i < blockConversions.size(); i++) {
            if (_mapEquals(bs.states(), blockConversions.get(i))) {
                return Pair.with(bs.name(), i);
            }
        }
        return Pair.with(bs.name(), 0);
    }

    private Set<Pair<String, ScalarUnion>> _scalarPairs(
        Map<String, Object> map
    ) {
        return map
            .entrySet()
            .stream()
            .map(e -> Pair.with(e.getKey(), new ScalarUnion(e.getValue())))
            .collect(Collectors.toSet());
    }

    private boolean _mapEquals(Map<String, Object> a, Map<String, Object> b) {
        var pairsA = _scalarPairs(a);
        var pairsB = _scalarPairs(b);
        return Objects.equals(pairsA, pairsB);
    }

    private void _debug(Object... v) {
        // System.err.print(this.getClass().getSimpleName());
        // for (var o: v) {
        // System.err.print(" ");
        // System.err.print(o);
        // if (o != null) {
        // System.err.print(":");
        // System.err.print(o.getClass().getSimpleName());
        // }
        // }
        // System.err.println();
    }
}
