package org.ethelred.mc;

import static org.ethelred.util.function.CheckedFunction.unchecked;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.nukkitx.jackson.dataformat.nbt.NBTFactory.Feature;
import com.nukkitx.jackson.dataformat.nbt.NBTMapper;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.ethelred.mc.pack.ImmutableMCFunction;
import org.ethelred.mc.pack.ImmutableMCImage;
import org.ethelred.mc.pack.ImmutableMCPack;
import org.ethelred.mc.pack.MCFunction;
import org.ethelred.mc.pack.MCPackType;
import org.ethelred.mc.pack.MCPackWriter;
import org.javatuples.KeyValue;
import org.javatuples.Pair;

public class StructureConverter {
  private static final ObjectMapper mapper = new NBTMapper()
    .enable(Feature.LITTLE_ENDIAN)
    .registerModule(new GuavaModule());

  public static void convert(Stream<KeyValue<String, byte[]>> rawStructures)
    throws IOException {
    LegacyBlockDataMap lbdm = new LegacyBlockDataMap();
    ImmutableMCPack.Builder packBuilder = ImmutableMCPack
      .builder()
      .name("exported_structures")
      .type(MCPackType.BEHAVIOR)
      .putFiles(
        "pack_icon.png",
        ImmutableMCImage
          .builder()
          .relativePath("pack_icon.png")
          .image(
            () ->
              ImageIO.read(
                StructureConverter.class.getResourceAsStream(
                    "/default_structure_pack_icon.png"
                  )
              )
          )
          .build()
      );
    StringBuilder descriptionBuilder = new StringBuilder(
      "Structure functions built by StructureConverter. "
    );
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
      .peek(kv -> descriptionBuilder.append(kv.getKey()))
      .map(kv -> convertStructure(lbdm, kv.getKey(), kv.getValue()))
      .forEach(f -> packBuilder.putFiles(f.getRelativePath(), f));

    packBuilder.description(descriptionBuilder.toString());
    var pack = packBuilder.build();
    MCPackWriter.writePack(pack);
  }

  private static MCFunction convertStructure(
    LegacyBlockDataMap lbdm,
    String name,
    Structure structure
  ) {
    ImmutableMCFunction.Builder functionBuilder = ImmutableMCFunction
      .builder()
      .name(_normalize(name));
    Coordinates size = structure.size();
    System.out.println("Blocks = " + (size.getX() * size.getY() * size.getZ()));
    BlockPalette palette = structure.structure().palette();
    Iterator<Integer> indices = structure
      .structure()
      .block_indices()
      .get(0)
      .iterator();

    for (int x = 0; x < size.getX(); x++) {
      for (int y = 0; y < size.getY(); y++) {
        for (int z = 0; z < size.getZ(); z++) {
          functionBuilder.addCommands(
            _setBlock(
              x,
              y,
              z,
              lbdm.convertBlockState(palette.get(indices.next()))
            )
          );
        }
      }
    }

    return functionBuilder.build();
  }

  private static String _normalize(String name) {
    int i = name.lastIndexOf("_");
    if (i > -1) {
      name = name.substring(i + 1);
    }
    return name.replaceAll("\\W+", "_").toLowerCase();
  }

  private static String _setBlock(
    int x,
    int y,
    int z,
    Pair<String, Integer> blockDef
  ) {
    String blockName = blockDef.getValue0();
    int colon = blockName.lastIndexOf(':');
    if (colon > -1) {
      blockName = blockName.substring(colon + 1);
    }
    return String.format(
      "setblock ~%d ~%d ~%d %s %d",
      x,
      y,
      z,
      blockName,
      blockDef.getValue1()
    );
  }
}
