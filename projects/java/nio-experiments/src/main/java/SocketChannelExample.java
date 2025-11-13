void main() throws IOException {
    InetSocketAddress address = new InetSocketAddress("example.com", 80);
    try (SocketChannel socketChannel = SocketChannel.open(address)) {

        String request = "GET / HTTP/1.1\r\nHost: example.com\r\n\r\n";
        ByteBuffer writeBuffer = ByteBuffer.wrap(request.getBytes());
        socketChannel.write(writeBuffer);

        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        socketChannel.read(readBuffer);
        readBuffer.flip();

        while (readBuffer.hasRemaining()) {
            IO.print((char) readBuffer.get());
        }
    }
}
