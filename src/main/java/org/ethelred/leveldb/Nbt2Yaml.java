package org.ethelred.leveldb;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;
import com.nukkitx.nbt.tag.ByteTag;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.FloatTag;
import com.nukkitx.nbt.tag.IntTag;
import com.nukkitx.nbt.tag.ListTag;
import com.nukkitx.nbt.tag.LongTag;
import com.nukkitx.nbt.tag.ShortTag;
import com.nukkitx.nbt.tag.StringTag;
import com.nukkitx.nbt.tag.Tag;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Nbt2Yaml {

  public static String toYamlString(Tag<?> v) {
    if (v == null) {
      return "";
    }

    var node = toYamlNode(v);
    return node.toString();
  }

  private static YamlNode toYamlNode(Tag<?> t) {
    // shitty dispatch mechanism
    switch (t.getClass().getSimpleName()) {
      case "CompoundTag":
        return toYamlNode((CompoundTag) t);
      case "ByteTag":
        return toYamlNode((ByteTag) t);
      case "ListTag":
        return toYamlNode((ListTag<?>) t);
      case "LongTag":
        return toYamlNode((LongTag) t);
      case "IntTag":
        return toYamlNode((IntTag) t);
      case "ShortTag":
        return toYamlNode((ShortTag) t);
      case "FloatTag":
        return toYamlNode((FloatTag) t);
      case "StringTag":
        return toYamlNode((StringTag) t);
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

  private static YamlMapping toYamlNode(CompoundTag t) {
    YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();
    List<String> sortedKeys = t
      .getValue()
      .keySet()
      .stream()
      .sorted(keyOrdering)
      .collect(Collectors.toList());
    for (var k : sortedKeys) {
      var v = t.getValue().get(k);
      builder = builder.add(k, toYamlNode(v));
    }
    return builder.build();
  }

  private static YamlSequence toYamlNode(ListTag<?> t) {
    YamlSequenceBuilder builder = Yaml.createYamlSequenceBuilder();
    for (var v : t.getValue()) {
      builder = builder.add(toYamlNode(v));
    }
    return builder.build();
  }

  private static YamlNode toYamlNode(ByteTag t) {
    return _scalar("byte!", t.getValue().toString());
  }

  private static YamlNode toYamlNode(LongTag t) {
    return _scalar("long!", t.getValue().toString());
  }

  private static YamlNode toYamlNode(ShortTag t) {
    return _scalar("short!", t.getValue().toString());
  }

  private static YamlNode toYamlNode(IntTag t) {
    return _scalar(null, t.getValue().toString());
  }

  private static YamlNode toYamlNode(FloatTag t) {
    return _scalar(null, t.getValue().toString());
  }

  private static YamlNode toYamlNode(StringTag t) {
    return _scalar(null, t.getValue().toString());
  }

  private static YamlNode _scalar(String typeOverride, String value) {
    return Yaml
      .createYamlScalarBuilder()
      .addLine(typeOverride == null ? value : typeOverride + " " + value)
      .buildPlainScalar();
  }
}
