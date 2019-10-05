package me.dags.converter.util.storage;

public class ShortArray {

    private static final byte[] empty = new byte[0];

    private final byte[] data;
    private byte[] adds;

    public ShortArray(int size) {
        data = new byte[size];
        adds = empty;
    }

    public ShortArray(byte[] data, byte[] adds) {
        this.data = data;
        this.adds = adds;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getAdds() {
        return adds;
    }

    public int getShort(int index) {
        if (adds.length == 0 || (index >> 1) >= adds.length) {
            return data[index] & 0xFF;
        } else {
            return getShort(index, adds) + (data[index] & 0xFF);
        }
    }

    public void setShort(int index, int value) {
        adds = setShort(index, value, data, adds);
    }

    private static int getShort(int index, byte[] values) {
        if ((index & 1) == 0) {
            return (values[index >> 1] & 0x0F) << 8;
        }
        return (values[index >> 1] & 0xF0) << 4;
    }

    private static byte[] setShort(int index, int id, byte[] blocks, byte[] adds) {
        if (id > 255) {
            if (adds.length == 0) {
                adds = new byte[(blocks.length >> 1) + 1];
            }
            if ((index & 1) == 0) {
                adds[index >> 1] = (byte) (adds[index >> 1] & 0xF0 | (id >> 8) & 0xF);
            } else {
                adds[index >> 1] = (byte) (adds[index >> 1] & 0xF | ((id >> 8) & 0xF) << 4);
            }
        }
        blocks[index] = (byte) id;
        return adds;
    }

    public static int getNibble(int index, byte[] values) {
        if ((index & 1) == 0) {
            return (values[index >> 1] & 0x0F) << 8;
        }
        return (values[index >> 1] & 0xF0) << 4;
    }

    public static void setNibble(int index, int meta, byte[] data) {
        int i = index >> 1;
        byte nibble = data[i];
        if ((i & 1) == 0) {
            nibble &= 0xF0;
            nibble |= (meta & 0x0F);
        } else {
            nibble &= 0x0F;
            nibble |= ((meta & 0x0F) << 4);
        }
        data[i] = nibble;
    }
}
