package org.ethelred.mc;

import static org.ethelred.util.function.CheckedFunction.unchecked;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.nukkitx.jackson.dataformat.nbt.NBTFactory.Feature;
import com.nukkitx.jackson.dataformat.nbt.NBTMapper;
import java.util.stream.Stream;
import org.ethelred.mc.pack.ImmutableMCFunction;
import org.ethelred.mc.pack.ImmutableMCPack;
import org.ethelred.mc.pack.MCFunction;
import org.javatuples.KeyValue;

public class StructureConverter {
  private static final ObjectMapper mapper = new NBTMapper()
    .enable(Feature.LITTLE_ENDIAN)
    .registerModule(new GuavaModule());

  public static void convert(Stream<KeyValue<String, byte[]>> rawStructures) {
    ImmutableMCPack.Builder packBuilder = ImmutableMCPack
      .builder()
      .name("exported_structures");
    rawStructures
      .map(
        unchecked(
          kv ->
            KeyValue.with(
              kv.getKey(),
              mapper.readValue(kv.getValue(), Structure.class)
            )
        )
      )
      .map(kv -> convertStructure(kv.getKey(), kv.getValue()))
      .forEach(f -> packBuilder.addFiles(f));

    var pack = packBuilder.build();
    System.out.println(pack);
  }

  private static MCFunction convertStructure(String name, Structure structure) {
    ImmutableMCFunction.Builder functionBuilder = ImmutableMCFunction
      .builder()
      .name(name);

    return functionBuilder.build();
  }
}
