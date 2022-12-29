package RIONet.data_objects;

import java.util.Arrays;

import RIONet.socket_utils.StructUtils;

public class DataObject {
    private DataHeader header;
    private int[] ivalues;
    private double[] dvalues;

    public DataObject(DataHeader header, int[] ivalues, double[] dvalues) {
        this.header = header;
        this.ivalues = ivalues;
        this.dvalues = dvalues;
    }

    /**
     * get the header of the DataObject
     *
     * @return DataHeader the header
     */
    public DataHeader getHeader() {
        return header;
    }

    /**
     * get the int part of the DataObject body
     *
     * @return int[] the body
     */
    public int[] getIValues() {
        return ivalues;
    }

    /**
     * get the double part of the DataObject body
     *
     * @return double[] the body
     */
    public double[] getDValues() {
        return dvalues;
    }

    /**
     * serializes the DataObject into an array of bytes
     *
     * @return byte[] the serializd DataObject
     */
    public byte[] serialize() {
        String format = String.format("h%di%dd", ivalues.length, dvalues.length);
        Object[] body = new Object[1 + ivalues.length + dvalues.length];
        body[0] = (short)header.ordinal();
        for (int i = 0; i < ivalues.length; i++) {
            body[i + 1] = ivalues[i];
        }
        for (int i = 0; i < dvalues.length; i++) {
            body[i + ivalues.length + 1] = dvalues[i];
        }
        return StructUtils.pack(format, body);
    }

    @Override
    public String toString() {
        return header.name() + ": " + Arrays.toString(ivalues) + Arrays.toString(dvalues);
    }
}
