package RIONet.data_objects;

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
        String format = String.format("%di%dd", ivalues.length, dvalues.length);
        Object[] body = new Object[ivalues.length + dvalues.length];
        for (int i = 0; i < ivalues.length; i++) {
            body[i] = (Object)ivalues[i];
        }
        for (int i = ivalues.length; i < dvalues.length; i++) {
            body[i] = (Object)dvalues[i];
        }
        return StructUtils.pack(format, body);
    }

    @Override
    public String toString() {
        String headeString = header.name() + ": ";
        String bodyString = "";
        for (int i : ivalues) {
            bodyString += Integer.toString(i) + ", ";
        }
        for (double d : dvalues) {
            bodyString += Double.toString(d) + ", ";
        }
        return headeString + bodyString;
    }
}
