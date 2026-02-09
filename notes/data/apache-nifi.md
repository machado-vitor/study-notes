Apache NiFi is data pipelining tool.

It is designed to automate the flow of data between systems. 

It works in a flow-based programming paradigm, where it exchanges data across predefined connections.

Architecture:
- It has a Web Server as user interface and REST API, where you design the user flow and monitoring system performance.
- Flow Controller: 
  - Processor:
    - Manages the execution of processors, maintains the queues, and ensures data flow.
    - Schedule resource allocation and flow orchestration.
  - Extension N:
    - Process execution, each process runs in its own thread pool. 
    - The flow controller have multiple processors, which allows parallel processing.
  - Linked to this flow controller:
    - Content Repository, where the data content of flow files on disk with configurable retention policies and compression options.
    - FlowFile Repository, which maintain metadata around flow files and their state in the system.
    - Providence Repository, goes the record of the detailed history of every action taken. This is an audit log.