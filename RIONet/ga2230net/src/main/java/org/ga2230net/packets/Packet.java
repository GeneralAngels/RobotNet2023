package org.ga2230net.packets;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.ga2230net.socket_utils.StructUtils;

/**
 * Represents a packet of data to be sent over the network
 * Has a header, a format, and a set of fields
 * A packet should only be created using a PacketBuilder
 */
public class Packet {
    private final LinkedHashMap<String, Object> data;
    private final String format;
    private final String header;

    /**
     * Create a new packet, should only be called by a PacketBuilder
     * @param header the header of the packet
     * @param format the format of the packet
     * @param fields the fields of the packet
     */
    public Packet(String header, String format, String... fields) {
        this.header = header;
        this.format = format;
        data = new LinkedHashMap<>();
        for (String field : fields) {
            data.put(field, null);
        }
    }

    /**
     * Sets a field in the packet
     * @param field the field to set
     * @param value the value to set the field to
     * @throws IllegalArgumentException if the field does not exist in the packet
     */
    public void setField(String field, Object value) throws IllegalArgumentException {
        if (!data.containsKey(field)) {
            throw new IllegalArgumentException("Field " + field + " does not exist in packet " + header);
        }
        data.put(field, value);
    }

    /**
     * Gets a field from the packet
     * @param <T> the type of the field
     * @param field the field to get
     * @return the value of the field
     * @throws IllegalArgumentException if the field does not exist in the packet
     */
    @SuppressWarnings("unchecked")
    public <T> T getField(String field) throws IllegalArgumentException {
        if (!data.containsKey(field)) {
            throw new IllegalArgumentException("Field " + field + " does not exist in packet " + header);
        }
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
     * @return the serialized packet
     */
    public byte[] serialize() {
        String complete_format = String.format("h%ds" + format, header.length());
        Object[] packArray = new Object[2 + data.size()];
        packArray[0] = (short) header.length();
        packArray[1] = header;
        Object[] values = data.values().toArray();
        System.arraycopy(values, 0, packArray, 2, values.length);
        return StructUtils.pack(complete_format, packArray);
    }
}
