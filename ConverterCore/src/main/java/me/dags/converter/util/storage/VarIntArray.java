package me.dags.converter.util.storage;

import java.io.ByteArrayOutputStream;

public class VarIntArray {

    private final int[] ints;

    public VarIntArray(int w, int h, int l) {
        this.ints = new int[w * h * l];
    }

    public void readBytes(byte[] data) {
        if (data.length == ints.length) {
            for (int i = 0; i < data.length; i++) {
                ints[i] = data[i];
            }
        } else {
            int index = 0;
            int value = 0;
            int varIntLen = 0;
            for (int i = 0; i < data.length; i++) {
                while (true) {
                    value |= (data[i] & 127) << (varIntLen++ * 7);
                    if (varIntLen > 5) {
                        throw new RuntimeException("VarInt too big (probably corrupted data)");
                    }
                    if ((data[i] & 128) != 128) {
                        i++;
                        break;
                    }
                    i++;
                }
                ints[index++] = value;
            }
        }
    }

    public byte[] writeBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(ints.length);
        for (int id : ints) {
            write(out, id);
        }
        return out.toByteArray();
    }

    public int get(int index) {
        return ints[index];
    }

    public void set(int index, int value) {
        ints[index] = value;
    }

    private static void write(ByteArrayOutputStream out, int id) {
        while ((id & -128) != 0) {
            out.write(id & 127 | 128);
            id >>>= 7;
        }
        out.write(id);
    }
}
