package RIONet;

public final class Constants {
    public static final class NetworkConstants {
        public final static int DEFAULT_PORT = 2230;
        public final static String PUip = "10.22.30.157"; // TODO update ip.
        public final static int packetByteSize = 6; // TODO: when done with RobotClient/server, remove
        public final static String unpackFormat = "hhhhh";

        public static final class HeaderPacketSizes {
            public final static int MOTOR_BY_PRECENT = 2; // id
        }
    }
}
