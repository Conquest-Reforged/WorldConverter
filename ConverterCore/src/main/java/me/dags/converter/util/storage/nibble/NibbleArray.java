package me.dags.converter.util.storage.nibble;

public interface NibbleArray {

    byte[] getData();

    int getNibble(int index);

    byte getByte(int index);

    void setNibble(int index, int value);

    void setByte(int index, byte value);

    interface Factory {

        NibbleArray read(byte[] data);

        NibbleArray write(int size);
    }
}
