#!/bin/bash

numCalls=$1
bigFile=$2
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$DIR/../.." && pwd )"

# Debug info
echo "Directory: $DIR"
echo "Project Root: $PROJECT_ROOT"
echo "Input file: $bigFile"

# More verbose server check
echo "Checking server on port 6013..."
if ! nc -z localhost 6013; then
    echo "Error: EchoServer not running on port 6013"
    echo "Start server first with: java -cp src echoserver.EchoServer &"
    exit 1
fi
echo "Server found running on port 6013"

# Check if input file exists
[ ! -f "$bigFile" ] && { echo "Error: Input file not found: $bigFile"; exit 1; }

FULL_INPUT_PATH="$(cd "$(dirname "$bigFile")" && pwd)/$(basename "$bigFile")"

for (( i=0; i<$numCalls; i++ ))
do
    echo "Starting client $i..."
    java -cp "$PROJECT_ROOT/src" echoserver.EchoClient < "$FULL_INPUT_PATH" 2>&1 > client_${i}.log &
    CLIENT_PID=$!
    echo "Started client $i with PID $CLIENT_PID"
    sleep 1  # Add delay between client starts
done

echo "Now waiting for all the processes to terminate"
date
wait
echo "Done waiting; all processes are finished"
date

# Check client logs summary
for (( i=0; i<$numCalls; i++ )); do
    echo "=== Summary of client $i (last 5 lines) ==="
    tail -n 5 client_${i}.log
    echo "Full log in: client_${i}.log"
    echo
done