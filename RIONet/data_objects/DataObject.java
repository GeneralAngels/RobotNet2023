package RIONet.data_objects;

public class DataObject {
    private DataHeader header;
    private int[] values;

    public DataObject(DataHeader header, int[] values) {
        this.header = header;
        this.values = values;
    }

    public static DataObject deserialize(byte[] bytes) {
        // header = 
        
        // return new DataObject(

        // );
        return null;
    }

    public byte[] serialize() {
        return null;
    }
}
