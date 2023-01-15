package RIONet.packets;

import RIONet.socket_utils.StructUtils;

import java.util.HashMap;

/**
 * Used to build packets according to their config files
 * A packet should only be created using a PacketBuilder
 */
public class PacketBuilder {
    // header to fields and types
    private final HashMap<String, HashMap<String, Character>> packetSchemes;

    /**
     * Creates a new packet builder from a packet directory
     * @param packet_directory the packet directory
     */
    public PacketBuilder(String packet_directory) {
        packetSchemes = parsePacketDirectory(packet_directory);
    }

    /**
     * Builds an empty packet from a header
     * @param header the header of the packet
     * @return the packet
     * @throws IllegalArgumentException if the header does not exist in the packet directory
     */
    public Packet buildFromHeader(String header) throws IllegalArgumentException {
        if (!packetSchemes.containsKey(header)) {
            throw new IllegalArgumentException("Header " + header + " does not exist in packet directory");
        }
        return new Packet(header, formatOf(header), fieldsOf(header));
    }

    /**
     * Builds a packet from a header and its raw body
     * @param header the header of the packet
     * @param raw_body the raw body of the packet
     * @return the packet
     * @throws IllegalArgumentException if the header does not exist in the packet directory
     */
    public Packet buildFromRaw(String header, byte[] raw_body) throws IllegalArgumentException {
        if (!packetSchemes.containsKey(header)) {
            throw new IllegalArgumentException("Header " + header + " does not exist in packet directory");
        }
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
     * @throws IllegalArgumentException if the header does not exist in the packet directory
     */
    public int sizeOf(String header) throws IllegalArgumentException {
        if (!packetSchemes.containsKey(header)) {
            throw new IllegalArgumentException("Header " + header + " does not exist in packet directory");
        }
        return StructUtils.sizeOf(formatOf(header));
    }

    /**
     * Gets the struct format of a packet from its header
     * @param header the header of the packet
     * @return the format of the packet
     * @throws IllegalArgumentException if the header does not exist in the packet directory
     */
    public String formatOf(String header) throws IllegalArgumentException {
        if (!packetSchemes.containsKey(header)) {
            throw new IllegalArgumentException("Header " + header + " does not exist in packet directory");
        }
        HashMap<String, Character> fields = packetSchemes.get(header);
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
     * @throws IllegalArgumentException if the header does not exist in the packet directory
     */
    public String[] fieldsOf(String header) throws IllegalArgumentException {
        if (!packetSchemes.containsKey(header)) {
            throw new IllegalArgumentException("Header " + header + " does not exist in packet directory");
        }
        return packetSchemes.get(header).keySet().toArray(new String[0]);
    }

    /**
     * Parses a packet directory into a HashMap of headers to fields and types
     * @param packet_directory the packet directory
     * @return the parsed packet directory
     */
    private HashMap<String, HashMap<String, Character>> parsePacketDirectory(String packet_directory) {
        HashMap<String, HashMap<String, Character>> packets = new HashMap<>();
        packets.put("EXAMPLE_PACKET", new HashMap<String, Character>());
        packets.get("EXAMPLE_PACKET").put("ifield1", 'i');
        packets.get("EXAMPLE_PACKET").put("dfield2", 'd');

        return packets;
        // TODO implement packet directory parsing
        // TODO implement packet directory validation
        // return null;
    }
}
