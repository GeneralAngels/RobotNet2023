package org.ga2230net;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Used to build packets according to their config files
 * A packet should only be created using a PacketBuilder
 */
public class PacketBuilder {
    // header to fields and types
    private final Map<String, PacketRepresentation> packetSchemes;

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
        return new Packet(header,
                packetSchemes.get(header).fmt(),
                packetSchemes.get(header).singleInstance(),
                packetSchemes.get(header).fields());
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
        String[] fields = fieldsOf(header);
        for (int i = 0; i < body.length; i++) {
            new_packet.setField(fields[i], body[i]);
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

        return StructUtils.sizeOf(packetSchemes.get(header).fmt());
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

        return packetSchemes.get(header).fmt();
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
        return packetSchemes.get(header).fields();
    }

    /**
     * Parses a packet directory into a Map of headers the packets representation
     * @param packet_directory the packet directory
     * @return the parsed packet directory
     */
    private Map<String, PacketRepresentation> parsePacketDirectory(String packet_directory) {
        // Create an instance of the SnakeYAML library
        Yaml yaml = new Yaml();
        // Create a new HashMap to store the result
        Map<String, PacketRepresentation> result = new LinkedHashMap<>();

        try {
            // Create a new File object representing the directory passed as a parameter
            File folder = new File(packet_directory);
            // Check if the directory exists and if it is a directory
            if(!folder.exists() || !folder.isDirectory()){
                throw new FileNotFoundException("The directory " + packet_directory + " was not found or it's not a directory.");
            }
            // Get an array of all the files in the directory
            File[] listOfFiles = folder.listFiles();
            // Check if the list of files is null
            if (listOfFiles == null) {
                throw new FileNotFoundException("The directory " + packet_directory + " was not found.");
            }
            // Iterate through the files
            for (File file : listOfFiles) {
                // Check if the file is a file and has the ".packet" extension
                if (file.isFile() && file.getName().endsWith(".packet")) {
                    // Read the file using FileInputStream
                    InputStream inputStream = new FileInputStream(file);
                    // Parse the file using the load method of the Yaml class
                    Map<String, Object> map = yaml.load(inputStream);

                    // Iterate through the key-value pairs in the parsed file
                    for (Map.Entry<String, Object> entry : map.entrySet()) {

                        // create a new packet entry
                        String header = entry.getKey();
                        StringBuilder fmt = new StringBuilder();
                        ArrayList<String> fields = new ArrayList<>();
                        boolean singleInstance = false;

                        for (Map.Entry<String, Object> innerEntry : ((Map<String, Object>) entry.getValue()).entrySet()) {
                            String currField = innerEntry.getKey();
                            if (currField.equals("single_instance")) {
                                singleInstance = (boolean) innerEntry.getValue();
                            } else {
                                fields.add(innerEntry.getKey());
                                fmt.append(innerEntry.getValue());
                            }
                        }
                        String[] tempArr = new String[fields.size()];
                        // Add the key-value pairs to the result map
                        result.put(header, new PacketRepresentation(header, fields.toArray(tempArr), fmt.toString(), singleInstance));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // return the result map
        return result;
    }
}