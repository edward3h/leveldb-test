package org.ethelred.mc;

import static org.ethelred.util.function.CheckedFunction.unchecked;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.nukkitx.jackson.dataformat.nbt.NBTFactory.Feature;
import com.nukkitx.jackson.dataformat.nbt.NBTMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    enum Rotation {
        R0("", c -> c),
        R90("_rot90", c -> new Coordinates(-c.z, c.y, c.x)),
        R180("_rot180", c -> new Coordinates(-c.x, c.y, -c.z)),
        R270("_rot270", c -> new Coordinates(c.z, c.y, -c.x));

        String suffix;
        private Function<Coordinates, Coordinates> rotationFunction;

        private Rotation(
            String suffix,
            Function<Coordinates, Coordinates> rotationFunction
        ) {
            this.suffix = suffix;
            this.rotationFunction = rotationFunction;
        }

        public Coordinates rotate(Coordinates coordinates) {
            return rotationFunction.apply(coordinates);
        }
    }

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
            .flatMap(
                kv ->
                    convertStructure(lbdm, kv.getKey(), kv.getValue()).stream()
            )
            .forEach(f -> packBuilder.putFiles(f.getRelativePath(), f));

        packBuilder.description(descriptionBuilder.toString());
        var pack = packBuilder.build();
        MCPackWriter.writePack(pack);
    }

    private static Collection<MCFunction> convertStructure(
        LegacyBlockDataMap lbdm,
        String name,
        Structure structure
    ) {
        Map<Rotation, ImmutableMCFunction.Builder> builders = new HashMap<>();
        for (Rotation rotation : Rotation.values()) {
            ImmutableMCFunction.Builder functionBuilder = ImmutableMCFunction
                .builder()
                .name(_normalize(name) + rotation.suffix);
            builders.put(rotation, functionBuilder);
        }
        Coordinates size = structure.size();
        System.out.println(
            "Blocks = " + (size.getX() * size.getY() * size.getZ())
        );
        BlockPalette palette = structure.structure().palette();
        Iterator<Integer> indices = structure
            .structure()
            .block_indices()
            .get(0)
            .iterator();

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    final int lx = x;
                    final int ly = y;
                    final int lz = z;
                    Pair<String, Integer> blockDef = lbdm.convertBlockState(
                        palette.get(indices.next())
                    );
                    // TODO: rotation doesn't take into account facing of blocks e.g. stairs
                    builders.forEach(
                        (rotation, functionBuilder) ->
                            functionBuilder.addCommands(
                                _setBlock(
                                    rotation.rotate(
                                        new Coordinates(lx, ly, lz)
                                    ),
                                    blockDef
                                )
                            )
                    );
                }
            }
        }

        return builders
            .values()
            .stream()
            .map(x -> x.build())
            .collect(Collectors.toList());
    }

    private static String _normalize(String name) {
        int i = name.lastIndexOf("_");
        if (i > -1) {
            name = name.substring(i + 1);
        }
        return name.replaceAll("\\W+", "_").toLowerCase();
    }

    private static String _setBlock(
        Coordinates c,
        Pair<String, Integer> blockDef
    ) {
        String blockName = blockDef.getValue0();
        int colon = blockName.lastIndexOf(':');
        if (colon > -1) {
            blockName = blockName.substring(colon + 1);
        }
        return String.format(
            "setblock ~%d ~%d ~%d %s %d",
            c.x,
            c.y,
            c.z,
            blockName,
            blockDef.getValue1()
        );
    }
}
