void main() throws IOException {
    Path path = Paths.get("example.txt");

    try (FileChannel writeChannel = FileChannel.open(
            path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.put("Hello from FileChannel!".getBytes());
        buffer.flip();
        writeChannel.write(buffer);
    }

    try (FileChannel readChannel = FileChannel.open(
            path, StandardOpenOption.READ)) {

        ByteBuffer buffer = ByteBuffer.allocate(64);
        readChannel.read(buffer);
        buffer.flip();

        while (buffer.hasRemaining()) {
            IO.print((char) buffer.get());
        }
    }
}