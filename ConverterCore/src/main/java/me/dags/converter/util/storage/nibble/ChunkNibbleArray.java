package me.dags.converter.util.storage.nibble;

public class ChunkNibbleArray implements NibbleArray {

    public static final int SIZE = 2048;

    private final byte[] data;

    public ChunkNibbleArray(int size) {
        this.data = new byte[size];
    }

    public ChunkNibbleArray(byte[] storageArray) {
        this.data = storageArray;

        if (storageArray.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + storageArray.length);
        }
    }

    @Override
    public int getNibble(int index) {
        int i = this.getNibbleIndex(index);
        return this.isLowerNibble(index) ? this.data[i] & 15 : this.data[i] >> 4 & 15;
    }

    @Override
    public void setNibble(int index, int value) {
        int i = this.getNibbleIndex(index);

        if (this.isLowerNibble(index)) {
            this.data[i] = (byte) (this.data[i] & 240 | value & 15);
        } else {
            this.data[i] = (byte) (this.data[i] & 15 | (value & 15) << 4);
        }
    }

    @Override
    public byte getByte(int index) {
        return data[index];
    }

    @Override
    public void setByte(int index, byte value) {
        data[index] = value;
    }

    private boolean isLowerNibble(int index) {
        return (index & 1) == 0;
    }

    private int getNibbleIndex(int index) {
        return index >> 1;
    }

    public byte[] getData() {
        return this.data;
    }

    public static NibbleArray.Factory FACTORY = new Factory() {
        @Override
        public NibbleArray read(byte[] data) {
            return new ChunkNibbleArray(data);
        }

        @Override
        public NibbleArray write(int size) {
            return new ChunkNibbleArray(size);
        }
    };
}
