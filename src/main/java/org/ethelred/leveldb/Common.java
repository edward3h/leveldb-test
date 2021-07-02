package org.ethelred.leveldb;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * static util class
 */
public class Common {

    private Common() {} // no instantiation

    public static Object readTag(byte[] value) throws IOException {
        try (
            NBTInputStream in = NbtUtils.createReaderLE(
                new ByteArrayInputStream(value)
            )
        ) {
            return in.readTag();
        }
    }

    public static List<Object> readTagList(byte[] value) throws IOException {
        List<Object> result = new ArrayList<>();
        try (
            NBTInputStream in = NbtUtils.createReaderLE(
                new ByteArrayInputStream(value)
            )
        ) {
            while (true) {
                result.add(in.readTag());
            }
        } catch (IOException e) {
            // ignore - doesn't seem to be any other way to detect running out of bytes
        }
        return result;
    }

    public static int readIntLE(byte[] value) {
        return value[0] & (value[1] << 8) & (value[2] << 16) & (value[3] << 24);
    }

    public static <T> void dumpThingByCount(Stream<T> things) {
        dumpThingByCount(things, String::valueOf);
    }

    public static <T> void dumpThingByCount(
        Stream<T> things,
        Function<T, String> toString
    ) {
        Multiset<T> thingsWithCounts = things.collect(
            Collectors.toCollection(HashMultiset::create)
        );
        Multisets
            .copyHighestCountFirst(thingsWithCounts)
            .forEachEntry(
                (thing, count) ->
                    System.out.printf(
                        "%12d => %s%n",
                        count,
                        toString.apply(thing)
                    )
            );
    }

    public static String toHex(byte[] value) {
        StringBuilder buf = new StringBuilder();
        for (byte b : value) {
            String hex = Integer.toHexString(b);
            if (hex.length() < 2) {
                buf.append("0");
            }
            buf.append(hex).append(" ");
        }
        return buf.toString();
    }

    public static Object indent(String indent, String input) {
        return input
            .lines()
            .map(l -> indent + l)
            .collect(Collectors.joining("\n"));
    }
}
