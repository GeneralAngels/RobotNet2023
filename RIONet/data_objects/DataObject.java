package RIONet.data_objects;

import java.util.Arrays;

import RIONet.socket_utils.StructUtils;

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
    public byte[] serialize() {
        return StructUtils.pack("h" + format, asObjectArray());
    }

    /**
     * returns an instance of the object created by unpacking a byte array
     * @param data the packed byte array
     */
    public abstract void deserialize(byte[] data);

    public DataHeader getHeader() {
        return header;
    }

    public String getStructFormat() {
        return format;
    }

    /**
     * @return the object header and body as an object array
     */
    public abstract Object[] asObjectArray();

    public String toString() {
        Object[] objects = asObjectArray();
        String header = (DataHeader.values()[(Integer.valueOf((short)objects[0]))]).name();
        String body = Arrays.toString(Arrays.copyOfRange(objects, 1, objects.length));
        return String.format("[%s]: %s", header, body);
    }
}
