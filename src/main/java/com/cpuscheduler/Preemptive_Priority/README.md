# Non Preemptive_Priority-Algorithm

Covered Testcases:

 - Test case with a single process (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 5, Priority = 1
   Expected Result: The process should start executing immediately and complete after 5 units of time.

 - Test case with multiple processes having different arrival times and priorities (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 4, Priority = 2
    - Process 2: Arrival Time = 2, Burst Time = 3, Priority = 3
    - Process 3: Arrival Time = 4, Burst Time = 2, Priority = 1
   Expected Result: Process 2 should start executing immediately, followed by Process 1 after 2 units of time, and then Process 3 after 2 more units of time.

 - Test case with multiple processes having the same arrival time and different priorities (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 3, Priority = 3
    - Process 2: Arrival Time = 0, Burst Time = 4, Priority = 2
    - Process 3: Arrival Time = 0, Burst Time = 2, Priority = 1
   Expected Result: The processes should be executed in the order of their priorities, i.e., Process 3, Process 1, and then Process 2.

 - Test case with multiple processes having the same priority, but different burst times (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 4, Priority = 1
    - Process 2: Arrival Time = 1, Burst Time = 2, Priority = 1
    - Process 3: Arrival Time = 2, Burst Time = 5, Priority = 1
   Expected Result: The processes should be executed in the order they arrive, i.e., Process 1, Process 2, and then Process 3.

 - Test case with multiple processes where some complete before others arrive, but with higher priorities (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 2, Priority = 3
    - Process 2: Arrival Time = 5, Burst Time = 3, Priority = 2
    - Process 3: Arrival Time = 6, Burst Time = 4, Priority = 1
   Expected Result: Process 1 should execute immediately, followed by Process 2 after 5 units of time, and then Process 3 after 1 more unit of time.


# Preemptive_Priority-Algorithm

Covered Testcases:

 - Test case with a single process (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 5, Priority = 1
   Expected Result: The process should start executing immediately and complete after 5 units of time.

 - Test case with multiple processes having different arrival times and priorities (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 4, Priority = 2
    - Process 2: Arrival Time = 2, Burst Time = 3, Priority = 3
    - Process 3: Arrival Time = 4, Burst Time = 2, Priority = 1
   Expected Result: Process 2 should start executing immediately, followed by Process 1 after 2 units of time, and then Process 3 after 2 more units of time.

 - Test case with multiple processes having the same arrival time and different priorities (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 3, Priority = 3
    - Process 2: Arrival Time = 0, Burst Time = 4, Priority = 2
    - Process 3: Arrival Time = 0, Burst Time = 2, Priority = 1
   Expected Result: The processes should be executed in the order of their priorities, i.e., Process 3, Process 1, and then Process 2.

 - Test case with multiple processes having the same priority, but different burst times (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 4, Priority = 1
    - Process 2: Arrival Time = 1, Burst Time = 2, Priority = 1
    - Process 3: Arrival Time = 2, Burst Time = 5, Priority = 1
   Expected Result: The processes should be executed in the order they arrive, i.e., Process 1, Process 2, and then Process 3.

 - Test case with multiple processes where some complete before others arrive, but with higher priorities (``Pass``):
    - Process 1: Arrival Time = 0, Burst Time = 2, Priority = 3
    - Process 2: Arrival Time = 5, Burst Time = 3, Priority = 2
    - Process 3: Arrival Time = 6, Burst Time = 4, Priority = 1
   Expected Result: Process 1 should execute immediately, followed by Process 2 after 5 units of time, and then Process 3 after 1 more unit of time.
