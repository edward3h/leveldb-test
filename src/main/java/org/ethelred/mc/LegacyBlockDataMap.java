package org.ethelred.mc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
}
