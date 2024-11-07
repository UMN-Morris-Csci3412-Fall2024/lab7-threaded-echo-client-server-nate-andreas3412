#!/bin/bash

numCalls=$1
bigFile=$2
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$DIR/../.." && pwd )"

# Debug info
echo "Directory: $DIR"
echo "Project Root: $PROJECT_ROOT"
echo "Input file: $bigFile"

# Check if server is running
nc -z localhost 6013 || { echo "Error: EchoServer not running on port 6013"; exit 1; }

# Check if input file exists
[ ! -f "$bigFile" ] && { echo "Error: Input file not found: $bigFile"; exit 1; }

# Convert relative path to absolute
FULL_INPUT_PATH="$(cd "$(dirname "$bigFile")" && pwd)/$(basename "$bigFile")"

for (( i=0; i<$numCalls; i++ ))
do
    echo "Doing run $i"
    # Updated classpath to point to src directory
    java -cp "$PROJECT_ROOT/src" echoserver.EchoClient < "$FULL_INPUT_PATH" 2>&1 > /dev/null &
    echo "Started client $i with PID $!"
done

echo "Now waiting for all the processes to terminate"
date
wait
echo "Done waiting; all processes are finished"
date