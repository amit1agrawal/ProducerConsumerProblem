# ProducerConsumerProblem
A solution to producer–consumer problem (also known as the bounded-buffer problem) with Delegation and Message Priority.
Steps to run it:
1. Create a New Java project in Eclipse.
2. Create a package com.ms.practice. 
3. Show the java file into the package and run it.That's it.

3 producers are created one each for message with priority High, Medium & Low. The delegator creates 3 threads, one each to continiously read messages from High, Medium & Low priority queue.The queue is maintained by Delegator. The messages read in the queue are pushed to Consumer(listener) who registered earlier to read messages from that particular Priority queue. List of consumers for a particular priority are maintained in separate HashMap so multiple consumers can register for message of that priority.
Each Queue is an array of 1024 length but it can be easily converted to LinkedList to have infinite(theoritically) length.
