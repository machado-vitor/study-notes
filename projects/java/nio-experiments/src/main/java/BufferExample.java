void main() {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    // This Heap buffer has:
    // - position 0
    // - limit 1024
    // - capacity 1024
    buffer.put("Hello".getBytes()); // writes bytes at indices 0..4
    // after write position=5, limit and capacity remains the same.
    buffer.flip(); // prepare for reading
    // flip switches from write to read
    // limit = position (so limit=5 and position=0)
    // now readable region is indices 0..4
    while (buffer.hasRemaining()) { // hasRemaining is the same as position < limit.
        IO.print((char) buffer.get());
    }

    // to write again call clear() or compact
}