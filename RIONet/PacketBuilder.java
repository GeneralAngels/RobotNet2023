package RIONet;

import RIONet.socket_utils.StructUtils;

import java.util.HashMap;


public class PacketBuilder {
    // header to fields and types
    private final HashMap<String, HashMap<String, Character>> packets;

    public PacketBuilder(String packet_directory) {
        packets = parsePacketDirectory(packet_directory);
    }

    /**
     * Builds an empty packet from a header
     * @param header the header of the packet
     * @return the packet
     */
    public Packet buildFromHeader(String header) {
        return new Packet(header, formatOf(header), fieldsOf(header));
    }

    /**
     * Builds a packet from a header and its raw body
     * @param header the header of the packet
     * @param raw_body the raw body of the packet
     * @return the packet
     */
    public Packet buildFromRaw(String header, byte[] raw_body) {
        Packet new_packet = buildFromHeader(header);
        Object[] body = StructUtils.unpack(formatOf(header), raw_body);
        for (int i = 0; i < body.length; i++) {
            new_packet.setField(fieldsOf(header)[i], body[i]);
        }
        return new_packet;
    }

    /**
     * Gets the number of bytes a packet will be from its header
     * @param header the header of the packet
     * @return the size of the packet
     */
    public int sizeOf(String header) {
        return StructUtils.sizeOf(formatOf(header));
    }

    /**
     * Gets the struct format of a packet from its header
     * @param header the header of the packet
     * @return the format of the packet
     */
    public String formatOf(String header) {
        HashMap<String, Character> fields = packets.get(header);
        String format = "";
        for (Character type : fields.values()) {
            format += type;
        }
        return format;
    }

    /**
     * Gets the fields of a packet from its header
     * @param header the header of the packet
     * @return the fields of the packet
     */
    public String[] fieldsOf(String header) {
        return packets.get(header).keySet().toArray(new String[0]);
    }

    /**
     * Parses a packet directory into a HashMap of headers to fields and types
     * @param packet_directory the packet directory
     * @return the parsed packet directory
     */
    private HashMap<String, HashMap<String, Character>> parsePacketDirectory(String packet_directory) {
        // TODO implement packet directory parsing
        return null;
    }
}
