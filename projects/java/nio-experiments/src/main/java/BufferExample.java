import java.nio.ByteBuffer;

public class BufferExample {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("Hello".getBytes());
        buffer.flip(); // prepare for reading
        while (buffer.hasRemaining()) {
            System.out.print((char) buffer.get());
        }
    }
}