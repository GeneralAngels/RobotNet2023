package RIONet;

import java.nio.ByteBuffer;

public class Main {
        public static void main(String[] args) {
                // Listener l = new Listener();
                // System.out.println("constructed");
                // l.run();

                // byte[] bytes = new byte[4];
                //
                // ByteBuffer buffer = ByteBuffer.wrap(bytes);
                // buffer.putShort((short) 5);
                //
                // bytes = new byte[buffer.remaining()];
                // buffer.get(bytes, 0, bytes.length);

                // Create a byte array
                byte[] bytes = new byte[10];

                // Wrap a byte array into a buffer
                ByteBuffer buf = ByteBuffer.wrap(bytes);

                buf.putShort((short) 1);
                buf.putShort((short) 2);
                buf.putShort((short) 3);
                buf.putShort((short) 4);

                // Retrieve all bytes in the buffer
                buf.clear();
                bytes = new byte[buf.capacity()];

                // transfer bytes from this buffer into the given destination array
                buf.get(bytes, 0, bytes.length);
                for (byte b : bytes) {
                        System.out.print(b + ", ");
                }
        }
}