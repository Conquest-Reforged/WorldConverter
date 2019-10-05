package me.dags.converter.util.storage;

public class BitArray {

    public static final int SECTION_SIZE = 16 * 16 * 16;

    private final int size;
    private final int bitsPerEntry;
    private final long maxEntryValue;
    private final long[] longArray;

    public BitArray(int bitsPerEntry) {
        this(bitsPerEntry, SECTION_SIZE);
    }

    public BitArray(int bitsPerEntry, int entries) {
        int totalBits = bitsPerEntry * entries;
        int arraySize = (int) Math.ceil(totalBits / 64D);
        this.size = entries;
        this.bitsPerEntry = bitsPerEntry;
        this.maxEntryValue = (1L << bitsPerEntry) - 1L;
        this.longArray = new long[arraySize];
    }

    public BitArray(int bitsPerEntry, long[] data) {
        this(bitsPerEntry, SECTION_SIZE, data);
    }

    public BitArray(int bitsPerEntry, int entries, long[] data) {
        this.size = entries;
        this.bitsPerEntry = bitsPerEntry;
        this.maxEntryValue = (1L << bitsPerEntry) - 1L;
        this.longArray = data;
    }

    public int size() {
        return longArray.length == 0 ? 0 : size;
    }

    public String toString() {
        return "BitArray{"
                + "bitsPerEntry=" + bitsPerEntry
                + ",length=" + longArray.length
                + "}";
    }

    public long[] getArray() {
        return longArray;
    }

    public void setBits(int index, int value) {
        int i = index * this.bitsPerEntry;
        int j = i >> 6;
        int k = (index + 1) * this.bitsPerEntry - 1 >> 6;
        int l = i ^ j << 6;
        this.longArray[j] = this.longArray[j] & ~(this.maxEntryValue << l) | ((long)value & this.maxEntryValue) << l;
        if (j != k) {
            int i1 = 64 - l;
            int j1 = this.bitsPerEntry - i1;
            this.longArray[k] = this.longArray[k] >>> j1 << j1 | ((long)value & this.maxEntryValue) >> i1;
        }
    }

    public int getBits(int index) {
        int i = index * this.bitsPerEntry;
        int j = i >> 6;
        int k = (index + 1) * this.bitsPerEntry - 1 >> 6;
        int l = i ^ j << 6;
        if (j == k) {
            return (int)(this.longArray[j] >>> l & this.maxEntryValue);
        } else {
            int i1 = 64 - l;
            return (int)((this.longArray[j] >>> l | this.longArray[k] << i1) & this.maxEntryValue);
        }
    }

    public static int minBits(int i) {
        int value = i;
        int count = 0;
        while (value > 0) {
            count++;
            value = value >> 1;
        }
        return Math.max(4, count);
    }
}
