package RIONet.socket_utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StructUtils {

    /**
     * unpacks a byte array into an object array ordered by the given format
     *
     * @param format the format to unpack by
     * @param raw    the byte array
     * @return an object array ordered by the given format
     */
    public static Object[] unpack(String format, byte[] raw) {
        format = parseFormat(format);
        Object[] result = new Object[format.length()];

        int pos = 0;

        for (int i = 0; i < format.length(); i++) {
            char type = format.charAt(i);

            switch (type) {
                case 'x': // pad type
                    pos += 1;
                    break;
                case 'c': // char
                    result[i] = (char) raw[pos];
                    pos += 1;
                    break;
                case 'h': // short
                    ByteBuffer buffer = ByteBuffer.allocate(2);
                    buffer.order(ByteOrder.BIG_ENDIAN);
                    buffer.put(raw[pos]);
                    buffer.put(raw[pos + 1]);

                    result[i] = buffer.getShort(0);
                    pos += 2;
                    break;
                case 's': // string
                    StringBuilder s = new StringBuilder();

                    while (raw[pos] != (byte) 0x00) {
                        char chr = (char) raw[pos];
                        s.append(chr);
                        pos += 1;
                    }
                    result[i] = s.toString();
                    break;
                case 'd': // double
                    buffer = ByteBuffer.allocate(8);
                    buffer.order(ByteOrder.BIG_ENDIAN);
                    for (int k = 0; i < 8; i++) {
                        buffer.put(raw[pos + k]);
                    }

                    result[i] = buffer.getDouble();
                    pos += 8;
                    break;
            }
        }

        return result;
    }

    /**
     * packs an Object array into a byte array according to the format given
     *
     * @param format the format to pack data by
     * @param data   the data to pack
     * @return byte array of the data
     */
    public static byte[] pack(String format, Object[] data) {
        format = parseFormat(format);
        int size = sizeOf(format);

        System.out.println(format);

        byte[] bytes = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        for (int i = 0; i < format.length(); i++) {
            switch (format.charAt(i)) {
                case 'c':
                    buffer.putChar((Character) data[i]);
                    break;
                case 'h':
                    buffer.putShort((Short) data[i]);
                    break;
                case 's': // TODO: implement string packing
                    break;
                case 'd':
                    buffer.putDouble((Double) data[i]);
                    break;
                case 'i':
                    buffer.putInt((Integer) data[i]);
                    break;
            }
        }

        // Retrieve all bytes in the buffer
        buffer.clear();
        bytes = new byte[buffer.capacity()];

        // transfer bytes from this buffer into the given destination array
        buffer.get(bytes, 0, bytes.length);
        return bytes;
    }

    /**
     * returns the estimated length of a bytearray of the struct format
     *
     * @param format a struct format
     * @return the estimated size of the format in bytes
     */
    public static int sizeOf(String format) {
        format = parseFormat(format);
        int size = 0;
        char[] chars = format.toCharArray();

        for (char c : chars) {
            switch (c) {
                case 'c':
                case 's':
                    size += 1; // char, string (same as py)
                    break;

                case 'h':
                    size += 2; // short
                    break;

                case 'i':
                case 'f':
                    size += 4; // int, float
                    break;

                case 'l':
                case 'd':
                    size += 8; // long, double
                    break;
            }
        }
        return size;
    }

    /**
     * Takes a format with numbers and parses it into one without numbers.
     * ie: 'i3cd' -> 'icccd'
     * @param format the unparsed format
     * @return the parsed format
     */
    public static String parseFormat(String format) {
        String newFormat = "";
        int multiplier = -1;
        for (int i = 0; i < format.length(); i++) {
            char curr = format.charAt(i);
            if (curr == '1' | curr == '2' | curr == '3' | curr == '4' | curr == '5' | curr == '6' | curr == '7' | curr == '8' | curr == '9' | curr == '0') {
                if (multiplier == -1)
                    multiplier = Character.getNumericValue(curr);
                else
                    multiplier = multiplier * 10 + Character.getNumericValue(curr);
            }

            else {
                if (multiplier == -1)
                    newFormat += curr;

                else
                    newFormat += new String(new char[multiplier]).replace('\0', curr);
                multiplier = -1;
            }
        }

        return newFormat;
    }
}
