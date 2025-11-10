void main() {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    buffer.put("Hello".getBytes());
    buffer.flip(); // prepare for reading
    while (buffer.hasRemaining()) {
        IO.print((char) buffer.get());
    }
}