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

1. id
2. parent1
3. parent2
4. timestamp
5. message
6. mappingToBlob


## Algorithms

## Persistence

