package RIONet.data_objects;

import java.util.Arrays;


/**
 * A generalized wrapper for all packet types.
 * Other types must override serialize, deserialize and more
 */
public abstract class DataObject {
    protected final DataHeader header;
    protected final String format;

    /**
     * A contructor used for creating a new Object from a serialized byte array
     */
    public DataObject(byte[] data, DataHeader header, String format) {
        this.header = header;
        this.format = format;
        deserialize(data);
    }

    /**
     * A more general constructor
     */
    public DataObject(DataHeader header, String format) {
        this.header = header;
        this.format = format;
    }

    /**
     * Serializes the object into a packed byte array
     * @return the unpacked data
     */
    public abstract byte[] serialize();

    /**
     * returns an instance of the object created by unpacking a byte array
     * @param data the packed byte array
     */
    public abstract void deserialize(byte[] data);

    public DataHeader getHeader() {
        return header;
    }

    public String getHeaderFormat() {
        return format;
    }

    /**
     * @return the object body as an object array
     */
    public abstract Object[] getBody();

    /**
     * @return the object header and body as an object array
     */
    public abstract Object[] asObjectArray();

    public String toString() {
        return Arrays.toString(getBody());
    }
}
