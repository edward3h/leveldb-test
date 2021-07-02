package org.ethelred.leveldb;

public class ChunkKey {

    private final int x;
    private final int z;

    public ChunkKey(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ChunkKey other = (ChunkKey) obj;
        if (x != other.x) return false;
        if (z != other.z) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ChunkKey [" + x + "," + z + "]";
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
