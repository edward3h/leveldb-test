package org.ethelred.leveldb;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Nbt2Yaml {

  public static String toYamlString(Object v) {
    if (v == null) {
      return "";
    }

    var node = toYamlNode(v);
    return node.toString();
  }

  private static YamlNode toYamlNode(Object t) {
    // shitty dispatch mechanism
    switch (NbtType.byClass(t.getClass()).getEnum()) {
      case COMPOUND:
        return toYamlNode((NbtMap) t);
      case BYTE:
        return toYamlNode((Byte) t);
      case LIST:
        return toYamlNode((NbtList<?>) t);
      case LONG:
        return toYamlNode((Long) t);
      case INT:
        return toYamlNode((Integer) t);
      case SHORT:
        return toYamlNode((Short) t);
      case FLOAT:
        return toYamlNode((Float) t);
      case STRING:
        return toYamlNode((String) t);
      case BYTE_ARRAY:
        return toYamlNode((byte[]) t);
      default:
        throw new IllegalArgumentException("Unsupported tag type " + t);
    }
  }

  private static final List<String> explicitKeys = List.of("id", "x", "y", "z");
  private static final Comparator<String> keyOrdering = Comparator
    .<String>comparingInt(
      s ->
        explicitKeys.contains(s) ? explicitKeys.indexOf(s) : Integer.MAX_VALUE
    )
    .thenComparing(s -> Character.isUpperCase(s.charAt(0)))
    .thenComparing(Comparator.naturalOrder());

  private static YamlMapping toYamlNode(NbtMap t) {
    YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();
    List<String> sortedKeys = t
      .keySet()
      .stream()
      .sorted(keyOrdering)
      .collect(Collectors.toList());
    for (var k : sortedKeys) {
      var v = t.get(k);
      builder = builder.add(k, toYamlNode(v));
    }
    return builder.build();
  }

  private static YamlSequence toYamlNode(NbtList<?> t) {
    YamlSequenceBuilder builder = Yaml.createYamlSequenceBuilder();
    for (var v : t) {
      builder = builder.add(toYamlNode(v));
    }
    return builder.build();
  }

  private static YamlNode toYamlNode(Byte t) {
    return _scalar("byte!", t.toString());
  }

  private static YamlNode toYamlNode(Long t) {
    return _scalar("long!", t.toString());
  }

  private static YamlNode toYamlNode(Short t) {
    return _scalar("short!", t.toString());
  }

  private static YamlNode toYamlNode(Integer t) {
    return _scalar(null, t.toString());
  }

  private static YamlNode toYamlNode(Float t) {
    return _scalar(null, t.toString());
  }

  private static YamlNode toYamlNode(String t) {
    return _scalar(null, t.toString());
  }

  private static YamlNode toYamlNode(byte[] t) {
    return _scalar(null, "TODO byte[] of length " + t.length);
  }

  private static YamlNode _scalar(String typeOverride, String value) {
    return Yaml
      .createYamlScalarBuilder()
      .addLine(typeOverride == null ? value : typeOverride + " " + value)
      .buildPlainScalar();
  }
}
