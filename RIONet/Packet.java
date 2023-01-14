package RIONet;

import java.util.Arrays;
import java.util.HashMap;

import RIONet.socket_utils.StructUtils;

/**
 * Represents a packet of data to be sent over the network
 * Has a header, a format, and a set of fields
 * A packet should only be created using a PacketBuilder
 */
public class Packet {
    private final HashMap<String, Object> data;
    private final String format;
    private final String header;

    public Packet(String header, String format, String... fields) {
        this.header = header;
        this.format = format;
        data = new HashMap<String, Object>();
        for (String field : fields) {
            data.put(field, null);
        }
    }

    public void setField(String field, Object value) {
        data.put(field, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getField(String field) {
        return (T) data.get(field);
    }

    public String getHeader() {
        return header;
    }

    public String getFormat() {
        return format;
    }

    public String toString() {
        return header + " " + format + " " + data.toString();
    }

    /**
     * serializes the packet into a packed struct byte array
     * 
     * @return the serialized packet
     */
    public byte[] serialize() {
        String complete_format = String.format("h%ds" + format, header.length());
        Object[] packArray = new Object[2 + data.size()];
        packArray[0] = (short) header.length();
        packArray[1] = header;
        Object[] values = data.values().toArray();
        for (int i = 0; i < data.size(); i++) {
            packArray[i + 2] = values[i];
        }

        System.out.println(complete_format);
        System.out.println(Arrays.toString(packArray));

        return StructUtils.pack(complete_format, packArray);
    }
}
