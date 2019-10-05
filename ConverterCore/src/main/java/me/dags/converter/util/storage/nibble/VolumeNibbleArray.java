package me.dags.converter.util.storage.nibble;

public class VolumeNibbleArray implements NibbleArray {

    private final byte[] data;

    public VolumeNibbleArray(byte[] data) {
        this.data = data;
    }

    public VolumeNibbleArray(int size) {
        data = new byte[size];
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public int getNibble(int index) {
        if ((index & 1) == 0) {
            return (data[index >> 1] & 0x0F) << 8;
        }
        return (data[index >> 1] & 0xF0) << 4;
    }

    @Override
    public void setNibble(int index, int value) {
        int i = index >> 1;
        byte nibble = data[i];
        if ((i & 1) == 0) {
            nibble &= 0xF0;
            nibble |= (value & 0x0F);
        } else {
            nibble &= 0x0F;
            nibble |= ((value & 0x0F) << 4);
        }
        data[i] = nibble;
    }

    @Override
    public byte getByte(int index) {
        return data[index];
    }

    @Override
    public void setByte(int index, byte value) {
        data[index] = value;
    }

    public static NibbleArray.Factory FACTORY = new Factory() {
        @Override
        public NibbleArray read(byte[] data) {
            return new VolumeNibbleArray(data);
        }

        @Override
        public NibbleArray write(int size) {
            return new VolumeNibbleArray(size);
        }
    };
}
