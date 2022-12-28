package RIONet.data_objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        byte[] bytes = new byte[4 * values.length + 2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.putShort((short)header.ordinal());

        for (int i = 0; i < values.length; i++) {
            buffer.putInt(values[i]);
        }

        // Retrieve all bytes in the buffer
        buffer.clear();
        bytes = new byte[buffer.capacity()];

        // transfer bytes from this buffer into the given destination array
        buffer.get(bytes, 0, bytes.length);
        return bytes;
    }

    @Override
    public String toString() {
        String headeString = header.name() + ": ";
        String bodyString = "";
        for (int i : values) {
            bodyString += Integer.toString(i) + ", ";
        }
        return headeString + bodyString;
    }
}
