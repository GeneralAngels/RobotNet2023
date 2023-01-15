package org.GANet;
import org.GANet.socket_utils.StructUtils;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
public class PacketBuilder {
    // header to fields and types
    private final Map<String, Map<String, Character>> packetSchemes;

    public PacketBuilder(String packet_directory) {
        packetSchemes = parsePacketDirectory(packet_directory);
    }

    /**
     * Builds an empty packet from a header
     *
     * @param header the header of the packet
     * @return the packet
     */
    public Packet buildFromHeader(String header) {
        return new Packet(header, formatOf(header), fieldsOf(header));
    }

    /**
     * Builds a packet from a header and its raw body
     *
     * @param header   the header of the packet
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
     *
     * @param header the header of the packet
     * @return the size of the packet
     */
    public int sizeOf(String header) {
        return StructUtils.sizeOf(formatOf(header));
    }

    /**
     * Gets the struct format of a packet from its header
     *
     * @param header the header of the packet
     * @return the format of the packet
     */
    public String formatOf(String header) {
        Map<String, Character> fields = packetSchemes.get(header);
        String format = "";
        for (Character type : fields.values()) {
            format += type;
        }
        return format;
    }

    /**
     * Gets the fields of a packet from its header
     *
     * @param header the header of the packet
     * @return the fields of the packet
     */
    public String[] fieldsOf(String header) {
        return packetSchemes.get(header).keySet().toArray(new String[0]);
    }

    /**
     * Parses a packet directory into a HashMap of headers to fields and types
     *
     * @param packet_directory the packet directory
     * @return the parsed packet directory
     */

    private Map<String, Map<String, Character>> parsePacketDirectory(String packet_directory) {
        // Create an instance of the SnakeYAML library
        Yaml yaml = new Yaml();
        // Create a new HashMap to store the result
        Map<String, Map<String, Character>> result = new HashMap<>();

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
                        String key = entry.getKey();
                        Map<String, Character> innerMap = new HashMap<>();
                        for (Map.Entry<String, Object> innerEntry : ((Map<String, Object>) entry.getValue()).entrySet()) {
                            innerMap.put(innerEntry.getKey(), ((String) innerEntry.getValue()).charAt(0));

                        }
                        // Add the key-value pairs to the result map
                        result.put(key, innerMap);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // return the result map
        System.out.println(result);
        return result;
    }


}