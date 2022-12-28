package RIONet;

import java.util.HashMap;

import RIONet.data_objects.DataHeader;

public final class Constants {
    public static final class NetworkConstants {
        public final static int DEFAULT_PORT = 6666;
        public final static String PUip = "10.22.30.157"; // TODO update ip.

        // TODO: put in config file
        public static final HashMap<DataHeader, Integer> HeaderPacketSizes = new HashMap<DataHeader, Integer>() {{
            put(DataHeader.EXAMPLE_HEADER, 2);
        }};
    }
}
