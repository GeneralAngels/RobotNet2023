package RIONet.data_objects;


public class DataObject {
    private DataHeader header;
    private int[] values;

    public DataObject(DataHeader header, int[] values) {
        this.header = header;
        this.values = values;
    }

    public DataHeader getHeader() {
        return header;
    }

    public int[] getValues() {
        return values;
    }

    public static DataObject deserialize(byte[] bytes) { //TODO: imlement DataObject serialization
        // header = 
        
        // return new DataObject(

        // );
        return null;
    }

    public byte[] serialize() {
        byte[] serialized = new byte[values.length + 1];
        serialized[0] = (byte)(short)(header.ordinal());
        for (int i = 1; i < values.length + 1; i++) {
            serialized[i] = (byte)values[i - 1];
        }

        return serialized;
    }
}
