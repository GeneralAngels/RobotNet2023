package RIONet.data_objects;

import RIONet.socket_utils.StructUtils;

public class RobotUpdate extends DataObject {
    private int val1;
    private int val2;

    public final static String format = "hh";
    public final static int size = StructUtils.sizeOf(format);

    public RobotUpdate(int val1, int val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    public int getVal1() {
        return val1;
    }

    public int getVal2() {
        return val2;
    }

    public Object[] getAttrs() {
        return new Object[] {
            val1,
            val2
        };
    }

    public static RobotUpdate deserealize(byte[] bytes) {
        Object[] objects = StructUtils.unpack(format, bytes);

        return new RobotUpdate(
            (int)objects[0],
            (int)objects[1]
        );
    }

    public byte[] serialize() {
        return StructUtils.pack(format, getAttrs());
    }
}
