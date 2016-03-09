######################
RUNNING CPU BENCHMARK (Go to cpu_benchmark folder in root)
######################

To run all float and integer cpu benchmark for 1,2 and 4 threads

>>bash cpu_benchmark.sh
-----------------------

To collect flops for 10 mins (600 seconds)

>>bash cpu_benchmark_600_flops.sh
-----------------------

To collect iops for 10 mins (600 seconds)

>>bash cpu_benchmark_600_iops.sh
-----------------------

######################
RUNNING DISK BENCHMARK (Go to disk_benchmark folder in root)
######################

To run all disk benchmark for block = 1, 1024 and 1048576 bytes

>>bash disk_benchmark.sh
------------------------

########################
RUNNING MEMORY BENCHMARK (Go to memory_benchmark folder in root)
########################

To compile the Memory_benchmark program use

>>make
-------------------------

To run the Memory_benchmark program use

>>make run
-------------------------

To remove the object file(.o file) from the current directory

>>make clean
-------------------------
