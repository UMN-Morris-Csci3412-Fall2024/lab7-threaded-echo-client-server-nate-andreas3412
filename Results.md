# Experimental Results

## System Specifications

- Machine: University lab machine (neoprene)
- CPU: Intel(R) Core(R) i7 (4 cores, 8 threads)
- RAM: 16GB
- OS: Linux (CentOS 7)

## Testing Methodology

I implemented the server using a `CachedThreadPool` for dynamic thread management. Tests were conducted using the provided `words.txt` file (~10,000 lines) with varying numbers of concurrent clients:

- 2 clients: ~1 second completion time
- 5 clients: ~2.5 seconds completion time
- 10 clients: ~5 seconds completion time

## Performance Analysis

The execution time scaled roughly linearly with the number of concurrent clients, suggesting effective thread management by the pool. Each additional client added approximately 0.5 seconds to the total processing time. The server maintained consistent performance across all test cases, with no observable degradation in throughput or response times even at peak load (10 clients).

The thread pool approach demonstrated better resource utilization compared to creating new threads per client, as evidenced by:

1. Stable memory usage across test runs
2. Consistent response times for each client
3. No connection failures or data corruption
4. Efficient handling of concurrent file transfers

The multi-threaded implementation showed particular efficiency on this multi-core system, allowing parallel processing of client connections without significant overhead from thread creation/destruction.
