
#FileServer

[![Build Status](https://travis-ci.org/helioss1/fileserver.svg)](https://travis-ci.org/helioss1/fileserver)
[![Coverage Status](https://coveralls.io/repos/helioss1/fileserver/badge.png?branch=master)](https://coveralls.io/r/helioss1/fileserver?branch=master)

Server is issuing given text file to a client line by line.

## Building & Starting server:
It needs Java 8 to run.

    mvn clean package
    java -jar target/fileserver.jar --fileName=src/test/resources/test.txt
    
## Client commands:
Connecting client to the server port 15050 (ex you may use: nc localhost 15050) and then write:

* `GET n` - get n'th line of file
* `QUIT` - disconnect client
* `SHUTDOWN` - close server
