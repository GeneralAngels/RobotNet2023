package org.ga2230net;

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
     * @throws StructError if the format doesn't match the data given
     * @throws StructError if the format is invalid
     */
    public static Object[] unpack(String format, byte... raw) {
        if (formatInvalid(format)) {
            throw new StructError("invalid format");
        }
        ArrayList<FormatPart> genFormat = generalizeFormat(format);

        Object[] result = new Object[unpackLength(genFormat)];

        ByteArrayInputStream bis = new ByteArrayInputStream(raw);
        DataInputStream dis = new DataInputStream(bis);

        int resIndex = 0;
        for (FormatPart part: genFormat) {
            char c = part.type();
            int count = part.count();

            try {
                switch (c) {
                    case 'c' -> { // char, utf-8
                        for (int i = 0; i < count; i++) {
                            result[resIndex] = (char) dis.readByte();
                            resIndex++;
                        }
                    }
                    case 'h' -> { // short
                        for (int i = 0; i < count; i++) {
                            result[resIndex] = dis.readShort();
                            resIndex++;
                        }
                    }
                    case 's' -> { // string
                        byte[] utf8Bytes = new byte[count];
                        int ignored = dis.read(utf8Bytes);
                        result[resIndex] = new String(utf8Bytes, StandardCharsets.UTF_8);
                        resIndex++;
                    }
                    case 'd' -> { // double
                        for (int i = 0; i < count; i++) {
                            result[resIndex] = dis.readDouble();
                            resIndex++;
                        }
                    }
                    case 'i' -> { // int
                        for (int i = 0; i < count; i++) {
                            result[resIndex] = dis.readInt();
                            resIndex++;
                        }
                    }
                    case 'l' -> { // long
                        for (int i = 0; i < count; i++) {
                            result[resIndex] = dis.readLong();
                            resIndex++;
                        }
                    }
                    case 'f' -> { // float
                        for (int i = 0; i < count; i++) {
                            result[resIndex] = dis.readFloat();
                            resIndex++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                throw new StructError("format doesn't match data given");
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
        if (formatInvalid(format)) {
            throw new StructError("invalid format");
        }
        ArrayList<FormatPart> genFormat = generalizeFormat(format);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bos);

        int data_index = 0;
        for (FormatPart part : genFormat) {
            char c = part.type();
            int count = part.count();

            try {
                switch (c) {
                    case 'c' -> {
                        for (int j = 0; j < count; j++) {
                            os.write((char) data[data_index]);
                            data_index++;
                        }
                    }
                    case 'h' -> {
                        for (int j = 0; j < count; j++) {
                            os.writeShort((short) data[data_index]);
                            data_index++;
                        }
                    }
                    case 's' -> {
                        os.write(((String) data[data_index]).getBytes(StandardCharsets.UTF_8));
                        data_index++;
                    }
                    case 'd' -> {
                        for (int j = 0; j < count; j++) {
                            os.writeDouble((double) data[data_index]);
                            data_index++;
                        }
                    }
                    case 'i' -> {
                        for (int j = 0; j < count; j++) {
                            os.writeInt((int) data[data_index]);
                            data_index++;
                        }
                    }
                    case 'l' -> {
                        for (int j = 0; j < count; j++) {
                            os.writeLong((long) data[data_index]);
                            data_index++;
                        }
                    }
                    case 'f' -> {
                        for (int j = 0; j < count; j++) {
                            os.writeFloat((float) data[data_index]);
                            data_index++;
                        }
                    }
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
        if (formatInvalid(format)) {
            throw new StructError("invalid format");
        }

        int size = 0;

        ArrayList<FormatPart> genFormat = generalizeFormat(format);
        for (FormatPart part : genFormat) {
            char c = part.type();
            int count = part.count();

            switch (c) {
                case 'c', 's' -> size += count; // char, string utf-8
                case 'h' -> size += count * 2; // short
                case 'i', 'f' -> size += count * 4; // int, float
                case 'l', 'd' -> size += count * 8; // long, double
            }
        }
        return size;
    }

    /**
     * Takes a format and generalizes it, so it can be used for easier packing and
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
                        } else { // last letter != current letter -> tempC1 + tempS, tempC1 = tempC2, tempS =
                                 // current
                            result.add(new FormatPart(tempC1, tempS));
                            tempC1 = tempC2;
                        }
                        tempC2 = 1;
                        tempS = current;
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
    public static boolean formatInvalid(String format) {
        return !format.matches("([0-9]*[cshidlf])+");
    }

    /**
         * A class that represents a part of a format.
         * a part is a type and its number of its occurrences in a row.
         */
        private record FormatPart(int count, char type) {

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
