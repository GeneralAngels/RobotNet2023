package RIONet.data_objects;

import RIONet.socket_utils.StructUtils;


/**
 * this is an example use of a DataObject
 */
public class ExampleData extends DataObject {

    private int dummyInt;
    private double dummyDouble1;
    private double dummyDouble2;

    public ExampleData(byte[] data) {
        super(data, DataHeader.EXAMPLE_HEADER, "idd");
    }

    public ExampleData(int i, double d1, double d2) {
        super(DataHeader.EXAMPLE_HEADER, "idd");
        dummyInt = i;
        dummyDouble1 = d1;
        dummyDouble2 = d2;
    }

    @Override
    public void deserialize(byte[] data) {
        Object[] body = StructUtils.unpack(format, data);

        dummyInt = (int)body[0];
        dummyDouble1 = (double)body[1];
        dummyDouble2 = (double)body[2];
    }

    @Override
    public Object[] asObjectArray() {
        Object[] objects = new Object[format.length() + 1];
        objects[0] = (Object)(short)header.ordinal();
        objects[1] = (Object)dummyInt;
        objects[2] = (Object)dummyDouble1;
        objects[3] = (Object)dummyDouble2;

        return objects;
    }
}
