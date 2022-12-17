package RIONet.data_objects;

import RIONet.socket_utils.StructUtils;

public class Task extends DataObject {
    private int id;
    private int val;

    public final static String format = "hh";
    public final static int size = StructUtils.sizeOf(format);

    public Task(int id, int val) {
        this.id = id;
        this.val = val;
    }

    public int getID() {
        return id;
    }

    public int getVal() {
        return val;
    }

    public Object[] getAttrs() {
        return new Object[] {
            id,
            val
        };
    }

    public static Task deserealize(byte[] bytes) {
        Object[] objects = StructUtils.unpack(format, bytes);

        return new Task(
            (int)objects[0],
            (int)objects[1]
        );
    }

    public byte[] serialize() {
        return StructUtils.pack(format, getAttrs());
    }
}
