package org.GANet.socket_utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * A class for handling struct packing and unpacking.
 * This class was built to mimic the python struct module.
 */
public class StructUtils {

    /**
     * unpacks a byte array into an object array ordered by the given format
     *
     * @param format the format to unpack by
     * @param raw    the byte array
     * @return an object array ordered by the given format
     * @throws StructError if the format doesnt match the data given
     * @throws StructError if the format is invalid
     */
    public static Object[] unpack(String format, byte... raw) {
        if (!validateFormat(format)) {
            throw new StructError("invalid format");
        }
        ArrayList<FormatPart> genFormat = generalizeFormat(format);

        Object[] result = new Object[unpackLength(genFormat)];

        ByteArrayInputStream bis = new ByteArrayInputStream(raw);
        DataInputStream dis = new DataInputStream(bis);

        for (int i = 0; i < genFormat.size(); i++) {
            char c = genFormat.get(i).getType();
            int count = genFormat.get(i).getCount();

            try {
                switch (c) {
                    case 'c': // char, utf-8
                        result[i] = (char) dis.readByte();
                        break;
                    case 'h': // short
                        result[i] = dis.readShort();
                        break;
                    case 's': // string
                        byte[] utf8Bytes = new byte[count];
                        dis.read(utf8Bytes);
                        result[i] = new String(utf8Bytes, StandardCharsets.UTF_8);
                        break;
                    case 'd': // double
                        result[i] = dis.readDouble();
                        break;
                    case 'i': // int
                        result[i] = dis.readInt();
                        break;
                    case 'l': // long
                        result[i] = dis.readLong();
                        break;
                    case 'f': // float
                        result[i] = dis.readFloat();
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                throw new StructError("format doesnt match data given");
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
     * @throws StructError if the format is invalid
     */
    public static byte[] pack(String format, Object[] data) {
        if (!validateFormat(format)) {
            throw new StructError("invalid format");
        }
        ArrayList<FormatPart> genFormat = generalizeFormat(format);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bos);

        int data_index = 0;
        for (FormatPart part : genFormat) {
            char c = part.getType();
            int count = part.getCount();

            try {
                switch (c) {
                    case 'c':
                        for (int j = 0; j < count; j++) {
                            os.write((char) data[data_index]);
                            data_index++;
                        }
                        break;
                    case 'h':
                        for (int j = 0; j < count; j++) {
                            os.writeShort((short) data[data_index]);
                            data_index++;
                        }
                        break;
                    case 's':
                        os.write(((String) data[data_index]).getBytes(StandardCharsets.UTF_8));
                        data_index++;
                        break;
                    case 'd':
                        for (int j = 0; j < count; j++) {
                            os.writeDouble((double) data[data_index]);
                            data_index++;
                        }
                        break;
                    case 'i':
                        for (int j = 0; j < count; j++) {
                            os.writeInt((int) data[data_index]);
                            data_index++;
                        }
                        break;
                    case 'l':
                        for (int j = 0; j < count; j++) {
                            os.writeLong((long) data[data_index]);
                            data_index++;
                        }
                        break;
                    case 'f':
                        for (int j = 0; j < count; j++) {
                            os.writeFloat((float) data[data_index]);
                            data_index++;
                        }
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bos.toByteArray();
    }

    /**
     * returns the estimated length of a bytearray of the struct format
     *
     * @param format a struct format
     * @return the estimated size of the format in bytes
     * @throws StructError if the format is invalid
     */
    public static int sizeOf(String format) {
        if (!validateFormat(format)) {
            throw new StructError("invalid format");
        }

        int size = 0;

        ArrayList<FormatPart> genFormat = generalizeFormat(format);
        for (FormatPart part : genFormat) {
            char c = part.getType();
            int count = part.getCount();

            switch (c) {
                case 'c':
                case 's':
                    size += count * 1; // char, string utf-8
                    break;

                case 'h':
                    size += count * 2; // short
                    break;

                case 'i':
                case 'f':
                    size += count * 4; // int, float
                    break;

                case 'l':
                case 'd':
                    size += count * 8; // long, double
                    break;
            }
        }
        return size;
    }

    /**
     * Takes a format and generalizes it so it can be used for easier packing and
     * unpacking.
     * ie: 'i3cdd' -> '1i3c2d'
     *
     * @param format the format
     * @return the generalized format
     */
    private static ArrayList<FormatPart> generalizeFormat(String format) {
        ArrayList<FormatPart> result = new ArrayList<>();
        int tempC1 = 1;
        int tempC2 = 1;

        char tempS = ' '; // null char

        boolean lastWasNumber = false;

        for (char current : format.toCharArray()) {
            if (Character.isDigit(current)) {
                int v = Character.getNumericValue(current);
                if (tempS == ' ') {
                    if (lastWasNumber)
                        tempC1 = tempC1 * 10 + v;
                    else
                        tempC1 = v;
                } else {
                    if (lastWasNumber)
                        tempC2 = tempC2 * 10 + v;
                    else
                        tempC2 = v;
                }
                lastWasNumber = true;
            } else {
                if (tempS == ' ') {
                    tempS = current;
                } else {
                    if (current == 's') { // s -> tempC2/1 + s
                        result.add(new FormatPart(tempC1, tempS));
                        result.add(new FormatPart(tempC2, current));
                        tempC1 = 1;
                        tempC2 = 1;
                        tempS = ' ';
                    } else { // is a non s char
                        if (current == tempS) { // last letter = current letter -> tempC1 += tempC2, tempS = current
                            tempC1 += tempC2;
                            tempC2 = 1;
                            tempS = current;
                        } else { // last letter != current letter -> tempC1 + tempS, tempC1 = tempC2, tempS =
                                 // current
                            result.add(new FormatPart(tempC1, tempS));
                            tempC1 = tempC2;
                            tempC2 = 1;
                            tempS = current;
                        }
                    }
                }
                lastWasNumber = false;
            }
        }
        result.add(new FormatPart(tempC1, format.charAt(format.length() - 1)));

        return result;
    }

    /**
     * returns the length of the data that will be unpacked
     *
     * @param format the generalized format of the data
     * @return the length of the data
     */
    private static int unpackLength(ArrayList<FormatPart> format) {
        int count = 0;
        for (FormatPart part : format)
            count += part.objCount();
        return count;
    }

    /**
     * returns whether the format is valid or not.
     * a valid format is a string that only contains the struct format chars and the number of times they occur.
     * @param format the format to validate
     * @return whether the format is valid or not
     */
    public static boolean validateFormat(String format) {
        return format.matches("([0-9]*[cshidlfs])+");
    }

    /**
     * A class that represents a part of a format.
     * a part is a type and its number of occurences in a row.
     */
    private static class FormatPart {
        private int count;
        private char type;

        public FormatPart(int count, char type) {
            this.count = count;
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        public char getType() {
            return type;
        }

        /**
         * returns the number of objects that will be unpacked
         * 
         * @return the number of objects that will be unpacked
         */
        public int objCount() {
            if (type == 's') {
                return 1;
            } else {
                return count;
            }
        }

        public String toString() {
            return count + " " + type;
        }
    }
}
