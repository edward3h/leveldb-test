package org.ethelred.mc.pack;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.graph.Traverser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.ethelred.util.function.CheckedConsumer;

public class MCPackWriter {
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

    private MCPackWriter() {}

    public static void writePack(Path outputDir, MCPack pack)
        throws IOException {
        Map<String, MCPackFile> fileRefs = new HashMap<>(pack.files()); // copy to mutable
        _fillDirectories(fileRefs);
        try (
            FileOutputStream fout = new FileOutputStream(
                outputDir.resolve(pack.name() + ".mcpack").toFile()
            );
            ZipOutputStream out = new ZipOutputStream(fout)
        ) {
            out.putNextEntry(new ZipEntry("manifest.json"));
            writeManifest(out, pack);
            _traverseFiles(fileRefs.keySet())
                .forEach(
                    CheckedConsumer.unchecked(
                        p -> {
                            MCPackFile f = fileRefs.get(p);
                            if (f == null) return;
                            out.putNextEntry(new ZipEntry(p));
                            if (f instanceof MCPackDirectory) {
                                out.closeEntry();
                            } else {
                                f.writeTo(out);
                            }
                        }
                    )
                );
        }
    }

    private static void _fillDirectories(Map<String, MCPackFile> fileRefs) {
        Set<String> toAdd = new HashSet<>();
        fileRefs
            .keySet()
            .forEach(
                p -> {
                    int i = -1;
                    while ((i = p.indexOf('/', i + 1)) > -1) {
                        toAdd.add(p.substring(0, i + 1));
                    }
                }
            );
        toAdd.forEach(p -> fileRefs.put(p, new MCPackDirectory(p)));
    }

    private static Iterable<String> _traverseFiles(Set<String> paths) {
        return Traverser
            .forTree(
                (String parent) ->
                    paths
                        .stream()
                        .filter(child -> _isDirectChild(parent, child))
                        .collect(Collectors.toSet())
            )
            .depthFirstPreOrder("");
    }

    private static boolean _isDirectChild(String parent, String child) {
        return (
            child.startsWith(parent) &&
            child.length() > parent.length() &&
            child.substring(parent.length()).matches("[^/]*/?")
        );
    }

    private static void _debugHeader(OutputStream out, String string) {
        PrintStream p = new PrintStream(out);
        p.println();
        p.println("---");
        p.println(string);
        p.println("---");
    }

    private static void writeManifest(OutputStream out, MCPack pack)
        throws IOException {
        OBJECT_MAPPER.writeValue(out, new Manifest(pack));
    }

    private static class Manifest extends JsonSerializable.Base {
        private MCPack pack;

        public Manifest(MCPack pack) {
            this.pack = pack;
        }

        @Override
        public void serialize(
            JsonGenerator gen,
            SerializerProvider serializers
        )
            throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("format_version", 2);

            gen.writeObjectFieldStart("header");
            gen.writeStringField("description", pack.description());
            gen.writeStringField("name", pack.name());
            gen.writeObjectField("min_engine_version", List.of(1, 16, 1));
            gen.writeObjectField("version", List.of(0, 1, 0));
            gen.writeObjectField("uuid", UUID.randomUUID());
            gen.writeEndObject();

            gen.writeObjectFieldStart("metadata");
            gen.writeObjectField("authors", List.of("edward3h"));
            gen.writeStringField(
                "url",
                "https://github.com/edward3h/leveldb-test"
            );
            gen.writeEndObject();

            gen.writeArrayFieldStart("modules");
            gen.writeObject(
                Map.of(
                    "description",
                    "blah",
                    "type",
                    pack.type(),
                    "uuid",
                    UUID.randomUUID(),
                    "version",
                    List.of(0, 1, 0)
                )
            );
            gen.writeEndArray();

            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(
            JsonGenerator gen,
            SerializerProvider serializers,
            TypeSerializer typeSer
        )
            throws IOException {
            serialize(gen, serializers);
        }
    }

    private static class MCPackDirectory implements MCPackFile {
        private String path;

        public MCPackDirectory(String path) {
            this.path = path;
        }

        @Override
        public String getRelativePath() {
            return path;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            // no-op
        }
    }

    public static void writePack(MCPack pack) throws IOException {
        writePack(Paths.get(""), pack);
    }
}
