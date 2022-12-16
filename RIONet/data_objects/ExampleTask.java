package RIONet.data_objects;

import RIONet.socket_utils.StructUtils;

public class ExampleTask extends DataObject {
    private int id;
    private int val;

    private String format = "hh";

    public ExampleTask(int id, int val) {
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
        return new Object[]{
            id,
            val
        };
    }
}
