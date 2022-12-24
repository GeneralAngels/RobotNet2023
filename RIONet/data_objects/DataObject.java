package RIONet.data_objects;

public class DataObject {
    private DataHeader header;
    private int[] values;

    public DataObject(DataHeader header, int[] values) {
        this.header = header;
        this.values = values;
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
     * get the body of the DataObject
     * 
     * @return int[] the body
     */
    public int[] getValues() {
        return values;
    }

    /**
     * serializes the DataObject into an array of bytes
     * 
     * @return byte[] the serializd DataObject
     */
    public byte[] serialize() {
        byte[] serialized = new byte[values.length + 1];
        serialized[0] = (byte) (short) (header.ordinal());
        for (int i = 1; i < values.length + 1; i++) {
            serialized[i] = (byte) values[i - 1];
        }

        return serialized;
    }
}
