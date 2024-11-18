# Gitlet Design Document

**Name**: Xinxin

## Classes and Data Structures

### Main
This is the entry point to the program. It takes in arguments from the command line and
based on the command (the first element of the args array) calls the corresponding 
command in <code>Repository</code> which will actually execute the logic of the command. 
It also validates the arguments based on the command to ensure that enough arguments
were passed in.
#### Fields

This class has no fields and hence no associated state: it simply validates 
arguments and defers the execution to the <code>Repository</code> class.


### Commit

#### Fields

1. <code>public String id</code> A unique id of the Commit.
2. <code>public Commit parent1</code> First parent of the Commit.
3. <code>public Commit parent2</code> Second parent of the Commit.
4. <code>timestamp</code> The time the Commit was created.
5. <code>message</code> The message of this Commit.
6. <code>mappingToBlob</code> A mapping of SHA-1 hash of blobs to the files in the blobs directory.


## Algorithms

## Persistence

