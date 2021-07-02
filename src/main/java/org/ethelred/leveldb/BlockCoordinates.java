package org.ethelred.leveldb;

public class BlockCoordinates {
    private final int x;
    private final int y;
    private final int z;

    public BlockCoordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "BlockCoordinates[" + x + "," + y + "," + z + "]";
    }

    public ChunkKey toChunkKey() {
        return new ChunkKey(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
    }

    public byte toSubChunkIndex() {
        return (byte) (y / 16); // y is never negative
    }

    public int toSubChunkBlockIndex() {
        int sx = Math.floorMod(x, 16);
        int sy = Math.floorMod(y, 16);
        int sz = Math.floorMod(z, 16);
        return sy + (sz * 16) + (sx * 16 * 16); // order given at https://minecraft.gamepedia.com/Bedrock_Edition_level_format#SubChunkPrefix_record_.281.0_and_1.2.13_formats.29
    }

    public static BlockCoordinates fromKeyAndIndex(
        Key recordKey,
        int offsetIndex
    ) {
        if (recordKey.getRecordType() != RecordType.SubChunkPrefix) {
            throw new IllegalArgumentException("Key must be a SubChunkPrefix");
        }
        return fromChunkKeyAndIndexes(
            recordKey.getChunkKey(),
            recordKey.getSubchunkIndex(),
            offsetIndex
        );
    }

    private static BlockCoordinates fromChunkKeyAndIndexes(
        ChunkKey chunkKey,
        byte subchunkIndex,
        int offsetIndex
    ) {
        int sx = (offsetIndex / (16 * 16)) % 16;
        int sy = offsetIndex % 16;
        int sz = (offsetIndex / 16) % 16;
        return new BlockCoordinates(
            chunkKey.getX() * 16 + sx,
            subchunkIndex * 16 + sy,
            chunkKey.getZ() * 16 + sz
        );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BlockCoordinates other = (BlockCoordinates) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        if (z != other.z) return false;
        return true;
    }
}
