## What is Java NIO?

NIO is a set of APIs designed for: 
- Non-blocking I/O
- Buffer-oriented data management
- Scalable multiplexing (selectors)

## Core Concepts

### Buffers
In classic I/O read and write use streams - one byte or one line at a time.

In NIO, everything does through buffers e.g. ByteBuffer, CharBuffer, etc.

A Buffer is a fixed-size container for data. 
A Buffer has three:
- position: where the next read/write will happen
- limit: how much data is available (for reading) or space left (for writing)
- capacity: total size of the buffer

